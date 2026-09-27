package com.example.shopsphere.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.shopsphere.dto.OrderCreateRequest;
import com.example.shopsphere.dto.OrderItemUpdateRequest;
import com.example.shopsphere.dto.OrderResponse;
import com.example.shopsphere.service.OrderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
	private final OrderService service;

	public OrderController(OrderService service) { this.service = service; }

	@PostMapping
	public ResponseEntity<OrderResponse> create(@RequestHeader("usr-d") Long userId,
			@Valid @RequestBody OrderCreateRequest request) {
		return ResponseEntity.status(201).body(OrderResponse.fromEntity(service.create(userId, request)));
	}

	@PostMapping("/{orderId}/items")
	public ResponseEntity<OrderResponse> addItems(@RequestHeader("usr-d") Long userId, @PathVariable Long orderId,
			@Valid @RequestBody OrderCreateRequest request) {
		return ResponseEntity.ok(OrderResponse.fromEntity(service.addItems(userId, orderId, request)));
	}

	@PutMapping("/{orderId}/items/{itemId}")
	public ResponseEntity<OrderResponse> updateItem(@RequestHeader("usr-d") Long userId, @PathVariable Long orderId,
			@PathVariable Long itemId, @Valid @RequestBody OrderItemUpdateRequest request) {
		return ResponseEntity.ok(OrderResponse.fromEntity(service.updateItem(userId, orderId, itemId, request.getQuantity())));
	}

	@DeleteMapping("/{orderId}/items/{itemId}")
	public ResponseEntity<OrderResponse> removeItem(@RequestHeader("usr-d") Long userId, @PathVariable Long orderId,
			@PathVariable Long itemId) {
		return ResponseEntity.ok(OrderResponse.fromEntity(service.removeItem(userId, orderId, itemId)));
	}

	@GetMapping("/history")
	public ResponseEntity<List<OrderResponse>> history(@RequestHeader("usr-d") Long userId) {
		return ResponseEntity.ok(service.history(userId).stream().map(OrderResponse::fromEntity).toList());
	}

	@GetMapping("/{orderId}")
	public ResponseEntity<OrderResponse> get(@RequestHeader("usr-d") Long userId, @PathVariable Long orderId) {
		return ResponseEntity.ok(OrderResponse.fromEntity(service.get(userId, orderId)));
	}

	@PutMapping("/{orderId}/cancel")
	public ResponseEntity<OrderResponse> cancel(@RequestHeader("usr-d") Long userId, @PathVariable Long orderId) {
		return ResponseEntity.ok(OrderResponse.fromEntity(service.cancel(userId, orderId)));
	}
}