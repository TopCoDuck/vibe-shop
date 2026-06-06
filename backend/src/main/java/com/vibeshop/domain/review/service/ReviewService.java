package com.vibeshop.domain.review.service;

import com.vibeshop.domain.product.entity.Product;
import com.vibeshop.domain.product.repository.ProductRepository;
import com.vibeshop.domain.review.dto.ReviewRequest;
import com.vibeshop.domain.review.dto.ReviewResponse;
import com.vibeshop.domain.review.dto.ReviewSummary;
import com.vibeshop.domain.review.entity.Review;
import com.vibeshop.domain.review.repository.ReviewRepository;
import com.vibeshop.domain.user.entity.User;
import com.vibeshop.domain.user.repository.UserRepository;
import com.vibeshop.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    /** 상품 리뷰 목록 + 통계 */
    public ReviewSummary getByProduct(Long productId, String email) {
        List<Review> reviews = reviewRepository.findByProductIdOrderByCreatedAtDesc(productId);
        double avg = reviewRepository.avgRatingByProductId(productId);
        long total = reviewRepository.countByProductId(productId);

        Long myId = email != null ? getUser(email).getId() : null;

        List<ReviewResponse> responses = reviews.stream().map(r -> {
            ReviewResponse res = new ReviewResponse(r);
            if (myId != null && r.getUser().getId().equals(myId)) {
                res.setMyReview(true);
            }
            return res;
        }).toList();

        return new ReviewSummary(Math.round(avg * 10.0) / 10.0, total, responses);
    }

    /** 리뷰 작성 */
    @Transactional
    public ReviewResponse create(Long productId, String email, ReviewRequest request) {
        User user = getUser(email);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> BusinessException.notFound("상품을 찾을 수 없습니다."));

        if (reviewRepository.existsByUserIdAndProductId(user.getId(), productId)) {
            throw BusinessException.badRequest("이미 리뷰를 작성한 상품입니다.");
        }

        Review review = Review.builder()
                .user(user)
                .product(product)
                .rating(request.getRating())
                .title(request.getTitle())
                .content(request.getContent())
                .imageUrl(request.getImageUrl())
                .build();

        Review saved = reviewRepository.save(review);
        ReviewResponse res = new ReviewResponse(saved);
        res.setMyReview(true);
        return res;
    }

    /** 리뷰 수정 */
    @Transactional
    public ReviewResponse update(Long reviewId, String email, ReviewRequest request) {
        User user = getUser(email);
        Review review = reviewRepository.findByIdAndUserId(reviewId, user.getId())
                .orElseThrow(() -> BusinessException.notFound("리뷰를 찾을 수 없거나 수정 권한이 없습니다."));

        review.update(request.getRating(), request.getTitle(), request.getContent(), request.getImageUrl());

        ReviewResponse res = new ReviewResponse(review);
        res.setMyReview(true);
        return res;
    }

    /** 리뷰 삭제 */
    @Transactional
    public void delete(Long reviewId, String email) {
        User user = getUser(email);
        Review review = reviewRepository.findByIdAndUserId(reviewId, user.getId())
                .orElseThrow(() -> BusinessException.notFound("리뷰를 찾을 수 없거나 삭제 권한이 없습니다."));
        reviewRepository.delete(review);
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }
}
