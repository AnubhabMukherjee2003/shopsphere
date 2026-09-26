package com.example.shopsphere.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.shopsphere.dto.CheckoutRequest;
import com.example.shopsphere.dto.OrderResponse;
import com.example.shopsphere.service.OrderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
	private final OrderService service;

	public OrderController(OrderService service) { this.service = service; }

	@PostMapping("/checkout")
	public ResponseEntity<OrderResponse> checkout(@RequestHeader("usr-d") Long userId,
			@Valid @RequestBody CheckoutRequest request) {
		return ResponseEntity.ok(OrderResponse.fromEntity(service.checkout(userId, request.toProductIds())));
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