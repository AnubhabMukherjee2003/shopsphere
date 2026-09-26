package com.example.shopsphere.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "categories")
public class Category extends BaseEntity {
	@NotBlank
	@Column(nullable = false, unique = true)
	private String name;
	private String description;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private CategoryStatus status = CategoryStatus.ACTIVE;

	@OneToMany(mappedBy = "category")
	private List<Product> products = new ArrayList<>();

	protected Category() { }

	public Category(String name, String description) {
		this.name = name;
		this.description = description;
	}

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
	public CategoryStatus getStatus() { return status; }
	public void setStatus(CategoryStatus status) { this.status = status; }
	public List<Product> getProducts() { return products; }
}
