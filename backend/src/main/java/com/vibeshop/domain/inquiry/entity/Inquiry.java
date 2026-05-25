package com.vibeshop.domain.inquiry.entity;

import com.vibeshop.domain.user.entity.User;
import com.vibeshop.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inquiries")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Inquiry extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 50)
    private String type;           // 배송 문의, 주문/결제, 반품/교환, 상품 문의, 회원/계정, 기타

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, length = 20)
    private String replyMethod;    // 문의내역, 전화, 문자, 답변불필요

    @Column
    private String imageUrls;      // comma-separated (추후 S3 등 연동 시 확장)

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "접수";  // 접수, 처리중, 완료
}
