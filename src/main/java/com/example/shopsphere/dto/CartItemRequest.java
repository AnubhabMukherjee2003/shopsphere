package com.example.shopsphere.dto;

import com.example.shopsphere.model.Cart;
import com.example.shopsphere.model.CartItem;
import com.example.shopsphere.model.Product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CartItemRequest {
	@NotNull private Long productId;
	@Min(1) private int quantity;

	public CartItem toEntity(Cart cart, Product product) {
		return new CartItem(cart, product, quantity);
	}
	public Long getProductId() { return productId; }
	public void setProductId(Long productId) { this.productId = productId; }
	public int getQuantity() { return quantity; }
	public void setQuantity(int quantity) { this.quantity = quantity; }
}