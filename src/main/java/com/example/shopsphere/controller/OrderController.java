package com.example.shopsphere.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.shopsphere.dto.OrderCreateRequest;
import com.example.shopsphere.dto.OrderItemUpdateRequest;
import com.example.shopsphere.dto.OrderResponse;
import com.example.shopsphere.security.AuthenticatedUser;
import com.example.shopsphere.service.OrderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
	private final OrderService service;

	public OrderController(OrderService service) { this.service = service; }

	@PostMapping
	public ResponseEntity<OrderResponse> create(@AuthenticationPrincipal AuthenticatedUser principal,
			@Valid @RequestBody OrderCreateRequest request) {
		return ResponseEntity.status(201).body(OrderResponse.fromEntity(service.create(principal.id(), request)));
	}

	@PostMapping("/{orderId}/items")
	public ResponseEntity<OrderResponse> addItems(@AuthenticationPrincipal AuthenticatedUser principal, @PathVariable Long orderId,
			@Valid @RequestBody OrderCreateRequest request) {
		return ResponseEntity.ok(OrderResponse.fromEntity(service.addItems(principal.id(), orderId, request)));
	}

	@PutMapping("/{orderId}/items/{itemId}")
	public ResponseEntity<OrderResponse> updateItem(@AuthenticationPrincipal AuthenticatedUser principal, @PathVariable Long orderId,
			@PathVariable Long itemId, @Valid @RequestBody OrderItemUpdateRequest request) {
		return ResponseEntity.ok(OrderResponse.fromEntity(service.updateItem(principal.id(), orderId, itemId, request.getQuantity())));
	}

	@DeleteMapping("/{orderId}/items/{itemId}")
	public ResponseEntity<OrderResponse> removeItem(@AuthenticationPrincipal AuthenticatedUser principal, @PathVariable Long orderId,
			@PathVariable Long itemId) {
		return ResponseEntity.ok(OrderResponse.fromEntity(service.removeItem(principal.id(), orderId, itemId)));
	}

	@GetMapping("/history")
	public ResponseEntity<List<OrderResponse>> history(@AuthenticationPrincipal AuthenticatedUser principal) {
		return ResponseEntity.ok(service.history(principal.id()).stream().map(OrderResponse::fromEntity).toList());
	}

	@GetMapping("/{orderId}")
	public ResponseEntity<OrderResponse> get(@AuthenticationPrincipal AuthenticatedUser principal, @PathVariable Long orderId) {
		return ResponseEntity.ok(OrderResponse.fromEntity(service.get(principal.id(), orderId)));
	}

	@PutMapping("/{orderId}/cancel")
	public ResponseEntity<OrderResponse> cancel(@AuthenticationPrincipal AuthenticatedUser principal, @PathVariable Long orderId) {
		return ResponseEntity.ok(OrderResponse.fromEntity(service.cancel(principal.id(), orderId)));
	}
}