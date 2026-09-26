package com.example.shopsphere.dto;

import com.example.shopsphere.model.Category;

import jakarta.validation.constraints.NotBlank;

public class CategoryRequest {
	@NotBlank private String name;
	private String description;

	public Category toEntity() { return new Category(name, description); }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
}