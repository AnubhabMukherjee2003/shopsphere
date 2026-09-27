package com.example.shopsphere.security;

public record AuthenticatedUser(Long id, String email, String role) { }