package com.example.shopsphere.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.shopsphere.dto.CategoryRequest;
import com.example.shopsphere.model.Category;
import com.example.shopsphere.model.CategoryStatus;
import com.example.shopsphere.repo.CategoryRepository;

@Service
public class CategoryService {
	private final CategoryRepository repository;

	public CategoryService(CategoryRepository repository) { this.repository = repository; }

	@Transactional
	public Category create(CategoryRequest request) {
		if (repository.existsByName(request.getName())) {
			throw new ApiException(HttpStatus.CONFLICT, "Category name is already in use");
		}
		return repository.save(request.toEntity());
	}

	@Transactional(readOnly = true)
	public List<Category> findAll() {
		return repository.findAll().stream().filter(category -> category.getStatus() != CategoryStatus.DELETED).toList();
	}

	@Transactional(readOnly = true)
	public Category find(Long id) {
		Category category = repository.findById(id).orElseThrow(() -> notFound("Category not found"));
		if (category.getStatus() == CategoryStatus.DELETED) throw notFound("Category not found");
		return category;
	}

	@Transactional
	public Category update(Long id, CategoryRequest request) {
		Category category = find(id);
		if (!category.getName().equals(request.getName()) && repository.existsByName(request.getName())) {
			throw new ApiException(HttpStatus.CONFLICT, "Category name is already in use");
		}
		category.setName(request.getName());
		category.setDescription(request.getDescription());
		return repository.save(category);
	}

	@Transactional
	public void delete(Long id) {
		Category category = find(id);
		category.setStatus(CategoryStatus.DELETED);
		repository.save(category);
	}

	private ApiException notFound(String message) { return new ApiException(HttpStatus.NOT_FOUND, message); }
}