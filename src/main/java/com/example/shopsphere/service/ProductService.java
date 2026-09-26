package com.example.shopsphere.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.shopsphere.dto.ProductRequest;
import com.example.shopsphere.model.Category;
import com.example.shopsphere.model.CategoryStatus;
import com.example.shopsphere.model.Product;
import com.example.shopsphere.model.ProductStatus;
import com.example.shopsphere.repo.CategoryRepository;
import com.example.shopsphere.repo.ProductRepository;

@Service
public class ProductService {
	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;

	public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
		this.productRepository = productRepository;
		this.categoryRepository = categoryRepository;
	}

	@Transactional
	public Product create(ProductRequest request) {
		Category category = activeCategory(request.getCategoryId());
		return productRepository.save(request.toEntity(category));
	}

	@Transactional(readOnly = true)
	public List<Product> findAll() {
		return productRepository.findAll().stream().filter(product -> product.getStatus() != ProductStatus.DELETED).toList();
	}

	@Transactional(readOnly = true)
	public Product find(Long id) {
		Product product = productRepository.findById(id).orElseThrow(() -> notFound("Product not found"));
		if (product.getStatus() == ProductStatus.DELETED) throw notFound("Product not found");
		return product;
	}

	@Transactional
	public Product update(Long id, ProductRequest request) {
		Product product = find(id);
		product.setName(request.getName());
		product.setDescription(request.getDescription());
		product.setPrice(request.getPrice());
		product.setStockQuantity(request.getStockQuantity());
		product.setCategory(activeCategory(request.getCategoryId()));
		if (product.getStockQuantity() > 0 && product.getStatus() == ProductStatus.OUT_OF_STOCK) {
			product.setStatus(ProductStatus.ACTIVE);
		}
		return productRepository.save(product);
	}

	@Transactional
	public void delete(Long id) {
		Product product = find(id);
		product.setStatus(ProductStatus.DELETED);
		productRepository.save(product);
	}

	private Category activeCategory(Long id) {
		Category category = categoryRepository.findById(id).orElseThrow(() -> notFound("Category not found"));
		if (category.getStatus() != CategoryStatus.ACTIVE) {
			throw new ApiException(HttpStatus.CONFLICT, "Category is not active");
		}
		return category;
	}

	private ApiException notFound(String message) { return new ApiException(HttpStatus.NOT_FOUND, message); }
}