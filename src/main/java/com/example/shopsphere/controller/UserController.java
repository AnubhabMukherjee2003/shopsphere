package com.example.shopsphere.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.shopsphere.dto.AuthResponse;
import com.example.shopsphere.dto.LoginRequest;
import com.example.shopsphere.dto.UserRegisterRequest;
import com.example.shopsphere.dto.UserResponse;
import com.example.shopsphere.security.JwtService;
import com.example.shopsphere.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/user")
public class UserController {
	private final UserService userService;
	private final JwtService jwtService;

	public UserController(UserService userService, JwtService jwtService) {
		this.userService = userService;
		this.jwtService = jwtService;
	}

	@PostMapping("/register")
	public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRegisterRequest request) {
		return ResponseEntity.status(201).body(UserResponse.fromEntity(userService.registerUser(request.toEntity())));
	}

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
		var user = userService.login(request.getEmail(), request.getPassword());
		String token = jwtService.generateToken(user);
		return ResponseEntity.ok(new AuthResponse(token, UserResponse.fromEntity(user)));
	}
}