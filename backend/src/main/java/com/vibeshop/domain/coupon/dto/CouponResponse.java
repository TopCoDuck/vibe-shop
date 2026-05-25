package com.vibeshop.domain.coupon.dto;

import com.vibeshop.domain.coupon.entity.Coupon;
import com.vibeshop.domain.coupon.entity.CouponType;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class CouponResponse {
    private final Long id;
    private final String name;
    private final String description;
    private final CouponType type;
    private final int value;
    private final int minOrderAmount;
    private final int maxDiscountAmount;
    private final String targetCategory;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final int totalCount;
    private final int downloadedCount;
    private final boolean downloadable;
    /** 현재 사용자가 이미 다운로드했는지 (서비스에서 set) */
    private boolean alreadyDownloaded;

    public CouponResponse(Coupon c) {
        this.id = c.getId();
        this.name = c.getName();
        this.description = c.getDescription();
        this.type = c.getType();
        this.value = c.getValue();
        this.minOrderAmount = c.getMinOrderAmount();
        this.maxDiscountAmount = c.getMaxDiscountAmount();
        this.targetCategory = c.getTargetCategory();
        this.startDate = c.getStartDate();
        this.endDate = c.getEndDate();
        this.totalCount = c.getTotalCount();
        this.downloadedCount = c.getDownloadedCount();
        this.downloadable = c.isDownloadable();
    }

    public void setAlreadyDownloaded(boolean v) {
        this.alreadyDownloaded = v;
    }
}
