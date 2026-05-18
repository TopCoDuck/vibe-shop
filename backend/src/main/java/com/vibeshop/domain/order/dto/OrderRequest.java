package com.vibeshop.domain.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

import java.util.List;

@Getter
public class OrderRequest {

    @NotEmpty(message = "주문 항목을 선택해주세요.")
    private List<Long> cartItemIds;

    @NotBlank(message = "배송지를 입력해주세요.")
    private String shippingAddress;

    @NotBlank(message = "수령인 이름을 입력해주세요.")
    private String receiverName;

    @NotBlank(message = "수령인 연락처를 입력해주세요.")
    private String receiverPhone;
}
