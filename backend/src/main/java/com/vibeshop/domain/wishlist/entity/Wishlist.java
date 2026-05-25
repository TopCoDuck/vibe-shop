package com.vibeshop.domain.wishlist.entity;

import com.vibeshop.domain.product.entity.Product;
import com.vibeshop.domain.user.entity.User;
import com.vibeshop.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "wishlists",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "product_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Wishlist extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}
