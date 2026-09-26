package com.example.shopsphere.dto;

import java.util.List;

import com.example.shopsphere.model.Cart;

public class CartResponse {
	private Long id;
	private String status;
	private List<CartItemResponse> items;

	public static CartResponse fromEntity(Cart cart) {
		CartResponse response = new CartResponse();
		response.id = cart.getId();
		response.status = cart.getStatus().name();
		response.items = cart.getItems().stream().map(CartItemResponse::fromEntity).toList();
		return response;
	}
	public Long getId() { return id; }
	public String getStatus() { return status; }
	public List<CartItemResponse> getItems() { return items; }
}