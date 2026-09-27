package com.example.shopsphere.dto;

import jakarta.validation.constraints.Min;

public class OrderItemUpdateRequest {
	@Min(1)
	private int quantity;

	public int getQuantity() { return quantity; }
	public void setQuantity(int quantity) { this.quantity = quantity; }
}