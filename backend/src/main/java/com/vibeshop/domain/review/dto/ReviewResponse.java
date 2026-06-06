package com.vibeshop.domain.review.dto;

import com.vibeshop.domain.review.entity.Review;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReviewResponse {

    private final Long id;
    private final Long userId;
    private final String userName;
    private final Long productId;
    private final int rating;
    private final String title;
    private final String content;
    private final String imageUrl;
    private final LocalDateTime createdAt;
    private boolean myReview;

    public ReviewResponse(Review review) {
        this.id = review.getId();
        this.userId = review.getUser().getId();
        this.userName = maskName(review.getUser().getName());
        this.productId = review.getProduct().getId();
        this.rating = review.getRating();
        this.title = review.getTitle();
        this.content = review.getContent();
        this.imageUrl = review.getImageUrl();
        this.createdAt = review.getCreatedAt();
    }

    public void setMyReview(boolean myReview) {
        this.myReview = myReview;
    }

    /** 이름 마스킹: 홍길동 → 홍*동 */
    private String maskName(String name) {
        if (name == null || name.length() <= 1) return name;
        if (name.length() == 2) return name.charAt(0) + "*";
        return name.charAt(0) + "*".repeat(name.length() - 2) + name.charAt(name.length() - 1);
    }
}
