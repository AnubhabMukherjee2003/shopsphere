package com.example.shopsphere.dto;

import com.example.shopsphere.model.CartItem;

public class CartItemResponse {
	private Long id;
	private Long productId;
	private String productName;
	private int quantity;

	public static CartItemResponse fromEntity(CartItem item) {
		CartItemResponse response = new CartItemResponse();
		response.id = item.getId();
		response.productId = item.getProduct().getId();
		response.productName = item.getProduct().getName();
		response.quantity = item.getQuantity();
		return response;
	}
	public Long getId() { return id; }
	public Long getProductId() { return productId; }
	public String getProductName() { return productName; }
	public int getQuantity() { return quantity; }
}