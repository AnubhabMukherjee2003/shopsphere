package com.example.shopsphere.dto;

import com.example.shopsphere.model.CartItem;

import jakarta.validation.constraints.Min;

public class CartItemUpdateRequest {
	@Min(1) private int quantity;

	public CartItem applyTo(CartItem item) {
		item.setQuantity(quantity);
		return item;
	}
	public int getQuantity() { return quantity; }
	public void setQuantity(int quantity) { this.quantity = quantity; }
}