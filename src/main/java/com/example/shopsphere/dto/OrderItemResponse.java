package com.example.shopsphere.dto;

import java.math.BigDecimal;

import com.example.shopsphere.model.OrderItem;

public class OrderItemResponse {
	private Long id;
	private Long productId;
	private String productName;
	private int quantity;
	private BigDecimal unitPrice;

	public static OrderItemResponse fromEntity(OrderItem item) {
		OrderItemResponse response = new OrderItemResponse();
		response.id = item.getId();
		response.productId = item.getProduct().getId();
		response.productName = item.getProduct().getName();
		response.quantity = item.getQuantity();
		response.unitPrice = item.getUnitPrice();
		return response;
	}
	public Long getId() { return id; }
	public Long getProductId() { return productId; }
	public String getProductName() { return productName; }
	public int getQuantity() { return quantity; }
	public BigDecimal getUnitPrice() { return unitPrice; }
}