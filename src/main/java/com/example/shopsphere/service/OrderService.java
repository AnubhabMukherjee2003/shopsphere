package com.example.shopsphere.service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.shopsphere.model.Cart;
import com.example.shopsphere.model.CartItem;
import com.example.shopsphere.model.CartStatus;
import com.example.shopsphere.model.Order;
import com.example.shopsphere.model.OrderItem;
import com.example.shopsphere.model.OrderStatus;
import com.example.shopsphere.model.Product;
import com.example.shopsphere.model.ProductStatus;
import com.example.shopsphere.model.User;
import com.example.shopsphere.repo.CartRepository;
import com.example.shopsphere.repo.OrderRepository;
import com.example.shopsphere.repo.ProductRepository;
import com.example.shopsphere.repo.userRepo;

@Service
public class OrderService {
	private final userRepo userRepository;
	private final CartRepository cartRepository;
	private final OrderRepository orderRepository;
	private final ProductRepository productRepository;

	public OrderService(userRepo userRepository, CartRepository cartRepository,
			OrderRepository orderRepository, ProductRepository productRepository) {
		this.userRepository = userRepository;
		this.cartRepository = cartRepository;
		this.orderRepository = orderRepository;
		this.productRepository = productRepository;
	}

	@Transactional
	public Order checkout(Long userId, List<Long> productIds) {
		User user = userRepository.findById(userId).orElseThrow(() -> notFound("User not found"));
		Cart cart = cartRepository.findByUser(user).orElseThrow(() -> notFound("Cart not found"));
		if (cart.getStatus() != CartStatus.ACTIVE) {
			throw new ApiException(HttpStatus.CONFLICT, "Cart is not active");
		}
		if (cart.getItems().isEmpty()) throw new ApiException(HttpStatus.CONFLICT, "Cart is empty");
		Set<Long> selectedProductIds = new HashSet<>(productIds);
		if (selectedProductIds.size() != productIds.size()) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Product IDs must be unique");
		}
		List<CartItem> selectedItems = cart.getItems().stream()
				.filter(item -> selectedProductIds.contains(item.getProduct().getId())).toList();
		if (selectedItems.size() != selectedProductIds.size()) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "One or more products are not in the cart");
		}

		BigDecimal total = BigDecimal.ZERO;
		Order order = new Order(user, BigDecimal.ZERO);
		for (CartItem cartItem : selectedItems) {
			Product product = cartItem.getProduct();
			if (product.getStatus() != ProductStatus.ACTIVE || product.getStockQuantity() < cartItem.getQuantity()) {
				throw new ApiException(HttpStatus.CONFLICT, "Product is unavailable or out of stock: " + product.getName());
			}
			BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
			total = total.add(lineTotal);
			order.getItems().add(new OrderItem(order, product, cartItem.getQuantity(), product.getPrice()));
			product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
			if (product.getStockQuantity() == 0) product.setStatus(ProductStatus.OUT_OF_STOCK);
			productRepository.save(product);
		}
		order.setTotalAmount(total);
		Order saved = orderRepository.save(order);
		cart.getItems().removeAll(selectedItems);
		if (cart.getItems().isEmpty()) cart.setStatus(CartStatus.CHECKED_OUT);
		cartRepository.save(cart);
		return saved;
	}

	@Transactional(readOnly = true)
	public Order get(Long userId, Long orderId) {
		User user = userRepository.findById(userId).orElseThrow(() -> notFound("User not found"));
		return orderRepository.findByIdAndUser(orderId, user).orElseThrow(() -> notFound("Order not found"));
	}

	@Transactional
	public Order cancel(Long userId, Long orderId) {
		User user = userRepository.findById(userId).orElseThrow(() -> notFound("User not found"));
		Order order = orderRepository.findByIdAndUser(orderId, user).orElseThrow(() -> notFound("Order not found"));
		if (order.getStatus() == OrderStatus.CANCELLED || order.getStatus() == OrderStatus.SHIPPED
				|| order.getStatus() == OrderStatus.DELIVERED) {
			throw new ApiException(HttpStatus.CONFLICT, "Order cannot be cancelled in its current state");
		}
		for (OrderItem item : order.getItems()) {
			Product product = item.getProduct();
			product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
			if (product.getStatus() == ProductStatus.OUT_OF_STOCK) product.setStatus(ProductStatus.ACTIVE);
			productRepository.save(product);
		}
		order.setStatus(OrderStatus.CANCELLED);
		return orderRepository.save(order);
	}

	private ApiException notFound(String message) { return new ApiException(HttpStatus.NOT_FOUND, message); }
}