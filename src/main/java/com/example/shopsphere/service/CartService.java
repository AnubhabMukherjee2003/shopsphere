package com.example.shopsphere.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.shopsphere.dto.CartItemRequest;
import com.example.shopsphere.model.Cart;
import com.example.shopsphere.model.CartItem;
import com.example.shopsphere.model.CartStatus;
import com.example.shopsphere.model.Product;
import com.example.shopsphere.model.ProductStatus;
import com.example.shopsphere.model.User;
import com.example.shopsphere.repo.CartItemRepository;
import com.example.shopsphere.repo.CartRepository;
import com.example.shopsphere.repo.ProductRepository;
import com.example.shopsphere.repo.userRepo;

@Service
public class CartService {
	private final userRepo userRepository;
	private final CartRepository cartRepository;
	private final CartItemRepository itemRepository;
	private final ProductRepository productRepository;

	public CartService(userRepo userRepository, CartRepository cartRepository,
			CartItemRepository itemRepository, ProductRepository productRepository) {
		this.userRepository = userRepository;
		this.cartRepository = cartRepository;
		this.itemRepository = itemRepository;
		this.productRepository = productRepository;
	}

	@Transactional
	public Cart getCart(Long userId) { return activeCart(userId); }

	@Transactional
	public Cart addItem(Long userId, CartItemRequest request) {
		Cart cart = activeCart(userId);
		Product product = productRepository.findById(request.getProductId())
				.orElseThrow(() -> notFound("Product not found"));
		if (product.getStatus() != ProductStatus.ACTIVE || product.getStockQuantity() <= 0) {
			throw new ApiException(HttpStatus.CONFLICT, "Product is not available");
		}
		CartItem item = itemRepository.findByCartAndProduct(cart, product).orElse(null);
		int newQuantity = request.getQuantity() + (item == null ? 0 : item.getQuantity());
		if (newQuantity > product.getStockQuantity()) {
			throw new ApiException(HttpStatus.CONFLICT, "Requested quantity exceeds stock");
		}
		if (item == null) item = request.toEntity(cart, product);
		else item.setQuantity(newQuantity);
		itemRepository.save(item);
		return cart;
	}

	@Transactional
	public Cart updateItem(Long userId, Long itemId, int quantity) {
		Cart cart = activeCart(userId);
		CartItem item = itemRepository.findById(itemId).orElseThrow(() -> notFound("Cart item not found"));
		if (!item.getCart().getId().equals(cart.getId())) throw notFound("Cart item not found");
		if (quantity < 1) throw new ApiException(HttpStatus.BAD_REQUEST, "Quantity must be greater than zero");
		if (quantity > item.getProduct().getStockQuantity()) {
			throw new ApiException(HttpStatus.CONFLICT, "Requested quantity exceeds stock");
		}
		item.setQuantity(quantity);
		itemRepository.save(item);
		return cart;
	}

	@Transactional
	public Cart deleteItem(Long userId, Long itemId) {
		Cart cart = activeCart(userId);
		CartItem item = itemRepository.findById(itemId).orElseThrow(() -> notFound("Cart item not found"));
		if (!item.getCart().getId().equals(cart.getId())) throw notFound("Cart item not found");
		itemRepository.delete(item);
		return cart;
	}

	@Transactional
	public Cart clear(Long userId) {
		Cart cart = activeCart(userId);
		itemRepository.deleteAll(cart.getItems());
		cart.getItems().clear();
		return cartRepository.save(cart);
	}

	private Cart activeCart(Long userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> notFound("User not found"));
		Cart cart = user.getCart();
		if (cart == null) {
			throw new ApiException(HttpStatus.CONFLICT, "User does not have a cart");
		}
		if (cart.getStatus() != CartStatus.ACTIVE) {
			throw new ApiException(HttpStatus.CONFLICT, "User does not have an active cart");
		}
		return cart;
	}

	private ApiException notFound(String message) { return new ApiException(HttpStatus.NOT_FOUND, message); }
}