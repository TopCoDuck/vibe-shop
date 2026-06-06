package com.vibeshop.domain.review.repository;

import com.vibeshop.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByProductIdOrderByCreatedAtDesc(Long productId);

    List<Review> findByUserIdOrderByCreatedAtDesc(Long userId);

    boolean existsByUserIdAndProductId(Long userId, Long productId);

    Optional<Review> findByIdAndUserId(Long id, Long userId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.product.id = :productId")
    long countByProductId(@Param("productId") Long productId);

    @Query("SELECT COALESCE(AVG(r.rating), 0) FROM Review r WHERE r.product.id = :productId")
    double avgRatingByProductId(@Param("productId") Long productId);
}
