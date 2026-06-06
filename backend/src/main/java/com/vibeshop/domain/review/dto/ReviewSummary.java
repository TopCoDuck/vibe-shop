package com.vibeshop.domain.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ReviewSummary {
    private final double avgRating;
    private final long totalCount;
    private final List<ReviewResponse> reviews;
}
