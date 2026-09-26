package com.example.shopsphere.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.shopsphere.model.Cart;
import com.example.shopsphere.model.User;

public interface CartRepository extends JpaRepository<Cart, Long> {
	Optional<Cart> findByUser(User user);
}