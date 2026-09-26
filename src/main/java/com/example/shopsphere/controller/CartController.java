package com.example.shopsphere.controller;

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

import com.example.shopsphere.dto.CartItemRequest;
import com.example.shopsphere.dto.CartItemUpdateRequest;
import com.example.shopsphere.dto.CartResponse;
import com.example.shopsphere.service.CartService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cart")
public class CartController {
	private final CartService service;

	public CartController(CartService service) { this.service = service; }

	@GetMapping
	public ResponseEntity<CartResponse> get(@RequestHeader("usr-d") Long userId) {
		return ResponseEntity.ok(CartResponse.fromEntity(service.getCart(userId)));
	}

	@PostMapping("/items")
	public ResponseEntity<CartResponse> addItem(@RequestHeader("usr-d") Long userId, @Valid @RequestBody CartItemRequest request) {
		return ResponseEntity.ok(CartResponse.fromEntity(service.addItem(userId, request)));
	}

	@PutMapping("/items/{cartItemId}")
	public ResponseEntity<CartResponse> updateItem(@RequestHeader("usr-d") Long userId, @PathVariable Long cartItemId,
			@Valid @RequestBody CartItemUpdateRequest request) {
		return ResponseEntity.ok(CartResponse.fromEntity(service.updateItem(userId, cartItemId, request.getQuantity())));
	}

	@DeleteMapping("/items/{cartItemId}")
	public ResponseEntity<CartResponse> deleteItem(@RequestHeader("usr-d") Long userId, @PathVariable Long cartItemId) {
		return ResponseEntity.ok(CartResponse.fromEntity(service.deleteItem(userId, cartItemId)));
	}

	@DeleteMapping("/items")
	public ResponseEntity<Void> clear(@RequestHeader("usr-d") Long userId) {
		service.clear(userId);
		return ResponseEntity.noContent().build();
	}
}