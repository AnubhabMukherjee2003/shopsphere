package com.example.shopsphere.service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.shopsphere.dto.OrderCreateRequest;
import com.example.shopsphere.dto.OrderItemRequest;
import com.example.shopsphere.model.Order;
import com.example.shopsphere.model.OrderItem;
import com.example.shopsphere.model.OrderStatus;
import com.example.shopsphere.model.Product;
import com.example.shopsphere.model.ProductStatus;
import com.example.shopsphere.model.User;
import com.example.shopsphere.repo.OrderRepository;
import com.example.shopsphere.repo.ProductRepository;
import com.example.shopsphere.repo.userRepo;

@Service
public class OrderService {
	private final userRepo userRepository;
	private final OrderRepository orderRepository;
	private final ProductRepository productRepository;

	public OrderService(userRepo userRepository, OrderRepository orderRepository,
			ProductRepository productRepository) {
		this.userRepository = userRepository;
		this.orderRepository = orderRepository;
		this.productRepository = productRepository;
	}

	@Transactional
	public Order create(Long userId, OrderCreateRequest request) {
		User user = userRepository.findById(userId).orElseThrow(() -> notFound("User not found"));
		Order order = new Order(user, BigDecimal.ZERO);
		Set<Long> productIds = new HashSet<>();
		for (OrderItemRequest itemRequest : request.getItems()) {
			if (!productIds.add(itemRequest.getProductId())) {
				throw new ApiException(HttpStatus.BAD_REQUEST, "Product IDs must be unique");
			}
			addItemToOrder(order, itemRequest);
		}
		return orderRepository.save(order);
	}

	@Transactional
	public Order addItems(Long userId, Long orderId, OrderCreateRequest request) {
		Order order = editableOrder(userId, orderId);
		Set<Long> productIds = new HashSet<>();
		for (OrderItem item : order.getItems()) productIds.add(item.getProduct().getId());
		for (OrderItemRequest itemRequest : request.getItems()) {
			if (!productIds.add(itemRequest.getProductId())) {
				throw new ApiException(HttpStatus.CONFLICT, "Product is already in the order");
			}
			addItemToOrder(order, itemRequest);
		}
		return orderRepository.save(order);
	}

	@Transactional
	public Order updateItem(Long userId, Long orderId, Long itemId, int quantity) {
		Order order = editableOrder(userId, orderId);
		OrderItem item = item(order, itemId);
		int difference = quantity - item.getQuantity();
		if (difference > 0) reserve(item.getProduct(), difference);
		if (difference < 0) release(item.getProduct(), -difference);
		item.setQuantity(quantity);
		recalculate(order);
		return orderRepository.save(order);
	}

	@Transactional
	public Order removeItem(Long userId, Long orderId, Long itemId) {
		Order order = editableOrder(userId, orderId);
		OrderItem item = item(order, itemId);
		release(item.getProduct(), item.getQuantity());
		order.getItems().remove(item);
		recalculate(order);
		return orderRepository.save(order);
	}

	@Transactional(readOnly = true)
	public List<Order> history(Long userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> notFound("User not found"));
		return orderRepository.findByUserOrderByCreatedAtDesc(user);
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
			release(item.getProduct(), item.getQuantity());
		}
		order.setStatus(OrderStatus.CANCELLED);
		return orderRepository.save(order);
	}

	private void addItemToOrder(Order order, OrderItemRequest request) {
		Product product = productRepository.findById(request.getProductId())
				.orElseThrow(() -> notFound("Product not found"));
		reserve(product, request.getQuantity());
		order.getItems().add(request.toEntity(order, product));
		recalculate(order);
	}

	private Order editableOrder(Long userId, Long orderId) {
		Order order = get(userId, orderId);
		if (order.getStatus() != OrderStatus.CREATED) {
			throw new ApiException(HttpStatus.CONFLICT, "Order can no longer be edited");
		}
		return order;
	}

	private OrderItem item(Order order, Long itemId) {
		return order.getItems().stream().filter(item -> item.getId().equals(itemId)).findFirst()
				.orElseThrow(() -> notFound("Order item not found"));
	}

	private void reserve(Product product, int quantity) {
		if (product.getStatus() != ProductStatus.ACTIVE || product.getStockQuantity() < quantity) {
			throw new ApiException(HttpStatus.CONFLICT, "Product is unavailable or out of stock: " + product.getName());
		}
		product.setStockQuantity(product.getStockQuantity() - quantity);
		if (product.getStockQuantity() == 0) product.setStatus(ProductStatus.OUT_OF_STOCK);
		productRepository.save(product);
	}

	private void release(Product product, int quantity) {
		product.setStockQuantity(product.getStockQuantity() + quantity);
		if (product.getStatus() == ProductStatus.OUT_OF_STOCK) product.setStatus(ProductStatus.ACTIVE);
		productRepository.save(product);
	}

	private void recalculate(Order order) {
		BigDecimal total = order.getItems().stream()
				.map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		order.setTotalAmount(total);
	}

	private ApiException notFound(String message) { return new ApiException(HttpStatus.NOT_FOUND, message); }
}