package com.vibeshop.domain.claim.dto;

import com.vibeshop.domain.claim.entity.ClaimReason;
import com.vibeshop.domain.claim.entity.ClaimType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ClaimRequest {

    @NotNull(message = "클레임 유형을 선택해 주세요.")
    private ClaimType type;

    @NotNull(message = "사유를 선택해 주세요.")
    private ClaimReason reason;

    private String reasonDetail;

    private String imageUrl;
}
