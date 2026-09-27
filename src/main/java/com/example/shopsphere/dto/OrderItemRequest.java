package com.example.shopsphere.dto;

import com.example.shopsphere.model.Order;
import com.example.shopsphere.model.OrderItem;
import com.example.shopsphere.model.Product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class OrderItemRequest {
	@NotNull
	private Long productId;

	@Min(1)
	private int quantity;

	public OrderItem toEntity(Order order, Product product) {
		return new OrderItem(order, product, quantity, product.getPrice());
	}

	public Long getProductId() { return productId; }
	public void setProductId(Long productId) { this.productId = productId; }
	public int getQuantity() { return quantity; }
	public void setQuantity(int quantity) { this.quantity = quantity; }
}