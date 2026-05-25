package com.vibeshop.domain.inquiry.dto;

import com.vibeshop.domain.inquiry.entity.Inquiry;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class InquiryResponse {
    private final Long id;
    private final String type;
    private final String content;
    private final String replyMethod;
    private final String imageUrls;
    private final String status;
    private final LocalDateTime createdAt;

    public InquiryResponse(Inquiry inquiry) {
        this.id          = inquiry.getId();
        this.type        = inquiry.getType();
        this.content     = inquiry.getContent();
        this.replyMethod = inquiry.getReplyMethod();
        this.imageUrls   = inquiry.getImageUrls();
        this.status      = inquiry.getStatus();
        this.createdAt   = inquiry.getCreatedAt();
    }
}
