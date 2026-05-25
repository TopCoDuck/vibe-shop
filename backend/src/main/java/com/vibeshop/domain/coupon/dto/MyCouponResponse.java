package com.vibeshop.domain.coupon.dto;

import com.vibeshop.domain.coupon.entity.CouponType;
import com.vibeshop.domain.coupon.entity.UserCoupon;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class MyCouponResponse {
    private final Long userCouponId;
    private final Long couponId;
    private final String name;
    private final String description;
    private final CouponType type;
    private final int value;
    private final int minOrderAmount;
    private final int maxDiscountAmount;
    private final String targetCategory;
    private final LocalDate endDate;
    private final boolean used;
    private final LocalDateTime downloadedAt;
    private final LocalDateTime usedAt;

    public MyCouponResponse(UserCoupon uc) {
        this.userCouponId = uc.getId();
        this.couponId = uc.getCoupon().getId();
        this.name = uc.getCoupon().getName();
        this.description = uc.getCoupon().getDescription();
        this.type = uc.getCoupon().getType();
        this.value = uc.getCoupon().getValue();
        this.minOrderAmount = uc.getCoupon().getMinOrderAmount();
        this.maxDiscountAmount = uc.getCoupon().getMaxDiscountAmount();
        this.targetCategory = uc.getCoupon().getTargetCategory();
        this.endDate = uc.getCoupon().getEndDate();
        this.used = uc.isUsed();
        this.downloadedAt = uc.getDownloadedAt();
        this.usedAt = uc.getUsedAt();
    }
}
