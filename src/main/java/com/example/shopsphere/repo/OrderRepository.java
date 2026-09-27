package com.example.shopsphere.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.shopsphere.model.Order;
import com.example.shopsphere.model.User;

public interface OrderRepository extends JpaRepository<Order, Long> {
	Optional<Order> findByIdAndUser(Long id, User user);
	List<Order> findByUserOrderByCreatedAtDesc(User user);
}