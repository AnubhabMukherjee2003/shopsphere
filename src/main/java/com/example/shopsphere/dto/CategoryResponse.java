package com.example.shopsphere.dto;

import com.example.shopsphere.model.Category;

public class CategoryResponse {
	private Long id;
	private String name;
	private String description;
	private String status;

	public static CategoryResponse fromEntity(Category category) {
		CategoryResponse response = new CategoryResponse();
		response.id = category.getId();
		response.name = category.getName();
		response.description = category.getDescription();
		response.status = category.getStatus().name();
		return response;
	}
	public Long getId() { return id; }
	public String getName() { return name; }
	public String getDescription() { return description; }
	public String getStatus() { return status; }
}