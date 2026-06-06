package com.vibeshop.domain.claim.dto;

import com.vibeshop.domain.claim.entity.Claim;
import com.vibeshop.domain.claim.entity.ClaimReason;
import com.vibeshop.domain.claim.entity.ClaimStatus;
import com.vibeshop.domain.claim.entity.ClaimType;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ClaimResponse {

    private final Long id;
    private final Long orderId;
    private final ClaimType type;
    private final ClaimReason reason;
    private final String reasonDetail;
    private final String imageUrl;
    private final ClaimStatus status;
    private final String adminComment;
    private final LocalDateTime createdAt;

    public ClaimResponse(Claim claim) {
        this.id = claim.getId();
        this.orderId = claim.getOrder().getId();
        this.type = claim.getType();
        this.reason = claim.getReason();
        this.reasonDetail = claim.getReasonDetail();
        this.imageUrl = claim.getImageUrl();
        this.status = claim.getStatus();
        this.adminComment = claim.getAdminComment();
        this.createdAt = claim.getCreatedAt();
    }
}
