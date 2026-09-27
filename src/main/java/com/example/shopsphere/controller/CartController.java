package com.example.shopsphere.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import com.example.shopsphere.security.AuthenticatedUser;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cart")
public class CartController {
	private final CartService service;

	public CartController(CartService service) { this.service = service; }

	@GetMapping
	public ResponseEntity<CartResponse> get(@AuthenticationPrincipal AuthenticatedUser principal) {
		return ResponseEntity.ok(CartResponse.fromEntity(service.getCart(principal.id())));
	}

	@PostMapping("/items")
	public ResponseEntity<CartResponse> addItem(@AuthenticationPrincipal AuthenticatedUser principal, @Valid @RequestBody CartItemRequest request) {
		return ResponseEntity.ok(CartResponse.fromEntity(service.addItem(principal.id(), request)));
	}

	@PutMapping("/items/{cartItemId}")
	public ResponseEntity<CartResponse> updateItem(@AuthenticationPrincipal AuthenticatedUser principal, @PathVariable Long cartItemId,
			@Valid @RequestBody CartItemUpdateRequest request) {
		return ResponseEntity.ok(CartResponse.fromEntity(service.updateItem(principal.id(), cartItemId, request.getQuantity())));
	}

	@DeleteMapping("/items/{cartItemId}")
	public ResponseEntity<CartResponse> deleteItem(@AuthenticationPrincipal AuthenticatedUser principal, @PathVariable Long cartItemId) {
		return ResponseEntity.ok(CartResponse.fromEntity(service.deleteItem(principal.id(), cartItemId)));
	}

	@DeleteMapping("/items")
	public ResponseEntity<Void> clear(@AuthenticationPrincipal AuthenticatedUser principal) {
		service.clear(principal.id());
		return ResponseEntity.noContent().build();
	}
}