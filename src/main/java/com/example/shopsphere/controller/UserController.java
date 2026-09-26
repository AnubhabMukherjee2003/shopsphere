package com.example.shopsphere.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.shopsphere.dto.UserRegisterRequest;
import com.example.shopsphere.dto.UserResponse;
import com.example.shopsphere.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/user")
public class UserController {
	private final UserService userService;

	public UserController(UserService userService) { this.userService = userService; }

	@PostMapping("/register")
	public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRegisterRequest request) {
		return ResponseEntity.status(201).body(UserResponse.fromEntity(userService.registerUser(request.toEntity())));
	}
}