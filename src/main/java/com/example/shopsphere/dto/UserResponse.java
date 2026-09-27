package com.example.shopsphere.dto;

import com.example.shopsphere.model.User;

public class UserResponse {
	private Long id;
	private String name;
	private String email;
	private String phone;
	private String status;
	private String role;

	public static UserResponse fromEntity(User user) {
		UserResponse response = new UserResponse();
		response.id = user.getId();
		response.name = user.getName();
		response.email = user.getEmail();
		response.phone = user.getPhone();
		response.status = user.getStatus().name();
		response.role = user.getRole() == null ? "CUSTOMER" : user.getRole().name();
		return response;
	}
	public Long getId() { return id; }
	public String getName() { return name; }
	public String getEmail() { return email; }
	public String getPhone() { return phone; }
	public String getStatus() { return status; }
	public String getRole() { return role; }
}