package com.example.shopsphere.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;

@Entity
@Table(name = "products")
public class Product extends BaseEntity {
	@Column(nullable = false)
	private String name;
	private String description;

	@DecimalMin(value = "0.01")
	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal price;

	@Min(0)
	@Column(nullable = false)
	private int stockQuantity;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ProductStatus status = ProductStatus.ACTIVE;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "category_id", nullable = false)
	private Category category;

	protected Product() { }

	public Product(String name, BigDecimal price, int stockQuantity, Category category) {
		this.name = name;
		this.price = price;
		this.stockQuantity = stockQuantity;
		this.category = category;
	}

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
	public BigDecimal getPrice() { return price; }
	public void setPrice(BigDecimal price) { this.price = price; }
	public int getStockQuantity() { return stockQuantity; }
	public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }
	public ProductStatus getStatus() { return status; }
	public void setStatus(ProductStatus status) { this.status = status; }
	public Category getCategory() { return category; }
	public void setCategory(Category category) { this.category = category; }
}
enum ProductStatus {
	ACTIVE, INACTIVE, OUT_OF_STOCK, DISCONTINUED, DELETED
}