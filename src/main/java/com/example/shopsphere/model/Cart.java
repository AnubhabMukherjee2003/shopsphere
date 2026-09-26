package com.example.shopsphere.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "carts")
public class Cart extends BaseEntity {
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private CartStatus status = CartStatus.ACTIVE;

	@OneToOne(optional = false)
	@JoinColumn(name = "user_id", nullable = false, unique = true)
	private User user;

	@OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CartItem> items = new ArrayList<>();

	protected Cart() { }

	public Cart(User user) { this.user = user; }

	public CartStatus getStatus() { return status; }
	public void setStatus(CartStatus status) { this.status = status; }
	public User getUser() { return user; }
	public void setUser(User user) { this.user = user; }
	public List<CartItem> getItems() { return items; }
}
enum CartStatus {
	ACTIVE, CHECKED_OUT, ABANDONED
}