package com.vibeshop.domain.cart.dto;

import com.vibeshop.domain.cart.entity.CartItem;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class CartItemResponse {
    private final Long id;
    private final Long productId;
    private final String productName;
    private final String imageUrl;
    private final BigDecimal price;
    private final Integer quantity;
    private final BigDecimal subtotal;

    public CartItemResponse(CartItem cartItem) {
        this.id = cartItem.getId();
        this.productId = cartItem.getProduct().getId();
        this.productName = cartItem.getProduct().getName();
        this.imageUrl = cartItem.getProduct().getImageUrl();
        this.price = cartItem.getProduct().getPrice();
        this.quantity = cartItem.getQuantity();
        this.subtotal = cartItem.getProduct().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
    }
}
