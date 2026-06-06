package com.vibeshop.domain.claim.entity;

public enum ClaimReason {
    CHANGE_MIND,           // 단순 변심
    DEFECTIVE,             // 상품 불량/파손
    WRONG_ITEM,            // 오배송
    DIFFERENT_FROM_DESC,   // 상품 설명과 다름
    DELAYED_DELIVERY,      // 배송 지연
    OTHER                  // 기타
}
