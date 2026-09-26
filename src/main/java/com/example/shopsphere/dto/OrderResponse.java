package com.example.shopsphere.dto;

import java.math.BigDecimal;
import java.util.List;

import com.example.shopsphere.model.Order;

public class OrderResponse {
	private Long id;
	private String status;
	private BigDecimal totalAmount;
	private List<OrderItemResponse> items;

	public static OrderResponse fromEntity(Order order) {
		OrderResponse response = new OrderResponse();
		response.id = order.getId();
		response.status = order.getStatus().name();
		response.totalAmount = order.getTotalAmount();
		response.items = order.getItems().stream().map(OrderItemResponse::fromEntity).toList();
		return response;
	}
	public Long getId() { return id; }
	public String getStatus() { return status; }
	public BigDecimal getTotalAmount() { return totalAmount; }
	public List<OrderItemResponse> getItems() { return items; }
}