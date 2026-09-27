package com.example.shopsphere.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

public class OrderCreateRequest {
	@NotEmpty
	@Valid
	private List<OrderItemRequest> items;

	public List<OrderItemRequest> getItems() { return items; }
	public void setItems(List<OrderItemRequest> items) { this.items = items; }
}