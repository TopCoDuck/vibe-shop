package com.vibeshop.domain.wishlist.dto;

import com.vibeshop.domain.wishlist.entity.Wishlist;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class WishlistResponse {

    private final Long id;
    private final Long productId;
    private final String productName;
    private final String imageUrl;
    private final BigDecimal price;
    private final String categoryName;
    private final Integer stock;
    private final LocalDateTime createdAt;

    public WishlistResponse(Wishlist wishlist) {
        this.id = wishlist.getId();
        this.productId = wishlist.getProduct().getId();
        this.productName = wishlist.getProduct().getName();
        this.imageUrl = wishlist.getProduct().getImageUrl();
        this.price = wishlist.getProduct().getPrice();
        this.categoryName = wishlist.getProduct().getCategory() != null
                ? wishlist.getProduct().getCategory().getName() : null;
        this.stock = wishlist.getProduct().getStock();
        this.createdAt = wishlist.getCreatedAt();
    }
}
