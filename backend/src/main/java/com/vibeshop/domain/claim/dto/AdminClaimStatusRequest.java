package com.vibeshop.domain.claim.dto;

import com.vibeshop.domain.claim.entity.ClaimStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminClaimStatusRequest {

    @NotNull(message = "상태를 선택해 주세요.")
    private ClaimStatus status;

    private String adminComment;
}
