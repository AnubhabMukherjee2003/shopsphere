package com.example.shopsphere.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.shopsphere.model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
	private final byte[] secret;
	private final long expirationMs;

	public JwtService(@Value("${shopsphere.jwt.secret}") String secret,
			@Value("${shopsphere.jwt.expiration-ms}") long expirationMs) {
		this.secret = secret.getBytes(StandardCharsets.UTF_8);
		this.expirationMs = expirationMs;
	}

	public String generateToken(User user) {
		Date now = new Date();
		return Jwts.builder()
				.subject(user.getId().toString())
				.claim("email", user.getEmail())
				.claim("role", user.getRole().name())
				.issuedAt(now)
				.expiration(new Date(now.getTime() + expirationMs))
				.signWith(Keys.hmacShaKeyFor(secret))
				.compact();
	}

	public Claims parse(String token) {
		return Jwts.parser().verifyWith(Keys.hmacShaKeyFor(secret)).build()
				.parseSignedClaims(token).getPayload();
	}
}