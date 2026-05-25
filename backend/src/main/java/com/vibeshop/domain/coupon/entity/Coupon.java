package com.vibeshop.domain.coupon.entity;

import com.vibeshop.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "coupons")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Coupon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    /** PERCENTAGE: 퍼센트 할인, FIXED: 고정 금액 할인 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CouponType type;

    /** 할인 값 (PERCENTAGE면 %, FIXED면 원) */
    @Column(nullable = false)
    private int value;

    /** 최소 주문 금액 */
    @Column(nullable = false)
    @Builder.Default
    private int minOrderAmount = 0;

    /** 최대 할인 금액 (PERCENTAGE일 때 상한선, 0이면 제한없음) */
    @Column(nullable = false)
    @Builder.Default
    private int maxDiscountAmount = 0;

    /** 적용 카테고리명 (null이면 전체) */
    @Column(length = 100)
    private String targetCategory;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    /** 전체 발급 가능 수 (0이면 무제한) */
    @Column(nullable = false)
    @Builder.Default
    private int totalCount = 0;

    /** 현재까지 다운로드 수 */
    @Column(nullable = false)
    @Builder.Default
    private int downloadedCount = 0;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    public void incrementDownload() {
        this.downloadedCount++;
    }

    public boolean isDownloadable() {
        return active
                && !LocalDate.now().isAfter(endDate)
                && (totalCount == 0 || downloadedCount < totalCount);
    }
}
