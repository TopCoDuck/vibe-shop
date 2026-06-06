package com.vibeshop.domain.claim.entity;

import com.vibeshop.domain.order.entity.Order;
import com.vibeshop.domain.user.entity.User;
import com.vibeshop.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "claims",
        uniqueConstraints = @UniqueConstraint(columnNames = {"order_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Claim extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ClaimType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ClaimReason reason;

    @Column(columnDefinition = "TEXT")
    private String reasonDetail;

    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ClaimStatus status = ClaimStatus.REQUESTED;

    /** 관리자 처리 메모 */
    @Column(columnDefinition = "TEXT")
    private String adminComment;

    public void updateStatus(ClaimStatus status, String adminComment) {
        this.status = status;
        if (adminComment != null) this.adminComment = adminComment;
    }

    public void cancel() {
        if (this.status != ClaimStatus.REQUESTED) {
            throw new IllegalStateException("접수 상태에서만 취소할 수 있습니다.");
        }
        this.status = ClaimStatus.REJECTED;
    }
}
