package com.vibeshop.domain.inquiry.dto;

import lombok.Getter;

@Getter
public class InquiryRequest {
    private String type;
    private String content;
    private String replyMethod;
    private String imageUrls;  // comma-separated URLs (optional)
}
