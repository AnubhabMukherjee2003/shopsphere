package com.example.shopsphere.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class CheckoutRequest {
	@NotEmpty
	private List<@NotNull Long> productIds;

	public List<Long> toProductIds() { return List.copyOf(productIds); }
	public List<Long> getProductIds() { return productIds; }
	public void setProductIds(List<Long> productIds) { this.productIds = productIds; }
}