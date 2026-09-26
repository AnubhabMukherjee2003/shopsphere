package com.example.shopsphere.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.shopsphere.model.Cart;
import com.example.shopsphere.model.CartItem;
import com.example.shopsphere.model.Product;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
	Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
}