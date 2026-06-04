package com.vibeshop.domain.review.controller;

import com.vibeshop.domain.review.dto.ReviewRequest;
import com.vibeshop.domain.review.dto.ReviewResponse;
import com.vibeshop.domain.review.dto.ReviewSummary;
import com.vibeshop.domain.review.service.ReviewService;
import com.vibeshop.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /** 상품 리뷰 목록 조회 (비로그인 가능) */
    @GetMapping("/api/products/{productId}/reviews")
    public ResponseEntity<ApiResponse<ReviewSummary>> getReviews(
            @PathVariable Long productId,
            @AuthenticationPrincipal UserDetails user) {
        String email = user != null ? user.getUsername() : null;
        return ResponseEntity.ok(ApiResponse.success(reviewService.getByProduct(productId, email)));
    }

    /** 리뷰 작성 */
    @PostMapping("/api/products/{productId}/reviews")
    public ResponseEntity<ApiResponse<ReviewResponse>> create(
            @PathVariable Long productId,
            @RequestBody @Valid ReviewRequest request,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(ApiResponse.success(reviewService.create(productId, user.getUsername(), request)));
    }

    /** 리뷰 수정 */
    @PutMapping("/api/reviews/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewResponse>> update(
            @PathVariable Long reviewId,
            @RequestBody @Valid ReviewRequest request,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(ApiResponse.success(reviewService.update(reviewId, user.getUsername(), request)));
    }

    /** 리뷰 삭제 */
    @DeleteMapping("/api/reviews/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal UserDetails user) {
        reviewService.delete(reviewId, user.getUsername());
        return ResponseEntity.ok(ApiResponse.success("리뷰가 삭제되었습니다.", null));
    }
}
