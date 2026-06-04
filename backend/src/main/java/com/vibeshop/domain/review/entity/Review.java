package com.vibeshop.domain.review.entity;

import com.vibeshop.domain.product.entity.Product;
import com.vibeshop.domain.user.entity.User;
import com.vibeshop.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reviews",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "product_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /** 평점 1~5 */
    @Column(nullable = false)
    private int rating;

    @Column(length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /** 리뷰 이미지 URL (선택) */
    private String imageUrl;

    public void update(int rating, String title, String content, String imageUrl) {
        this.rating = rating;
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
    }
}
