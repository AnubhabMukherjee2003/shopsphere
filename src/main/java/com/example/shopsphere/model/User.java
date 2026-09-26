package com.example.shopsphere.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "users")
public class User extends BaseEntity {
	@NotBlank
	@Column(nullable = false)
	private String name;

	@Email
	@NotBlank
	@Column(nullable = false, unique = true)
	private String email;

	@NotBlank
	@Column(nullable = false)
	private String password;

	private String phone;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private UserStatus status = UserStatus.ACTIVE;

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private Cart cart;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
	private List<Order> orders = new ArrayList<>();

	protected User() { }

	public User(String name, String email, String password) {
		this.name = name;
		this.email = email;
		this.password = password;
		this.cart = new Cart(this);
	}

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }
	public String getPassword() { return password; }
	public void setPassword(String password) { this.password = password; }
	public String getPhone() { return phone; }
	public void setPhone(String phone) { this.phone = phone; }
	public UserStatus getStatus() { return status; }
	public void setStatus(UserStatus status) { this.status = status; }
	public Cart getCart() { return cart; }
	public void setCart(Cart cart) { this.cart = cart; }
	public List<Order> getOrders() { return orders; }
}
enum UserStatus {
	ACTIVE, INACTIVE, BLOCKED, DELETED;
}