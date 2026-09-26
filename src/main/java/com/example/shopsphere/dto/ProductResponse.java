package com.example.shopsphere.dto;

import java.math.BigDecimal;

import com.example.shopsphere.model.Product;

public class ProductResponse {
	private Long id;
	private String name;
	private String description;
	private BigDecimal price;
	private int stockQuantity;
	private String status;
	private Long categoryId;

	public static ProductResponse fromEntity(Product product) {
		ProductResponse response = new ProductResponse();
		response.id = product.getId();
		response.name = product.getName();
		response.description = product.getDescription();
		response.price = product.getPrice();
		response.stockQuantity = product.getStockQuantity();
		response.status = product.getStatus().name();
		response.categoryId = product.getCategory().getId();
		return response;
	}
	public Long getId() { return id; }
	public String getName() { return name; }
	public String getDescription() { return description; }
	public BigDecimal getPrice() { return price; }
	public int getStockQuantity() { return stockQuantity; }
	public String getStatus() { return status; }
	public Long getCategoryId() { return categoryId; }
}