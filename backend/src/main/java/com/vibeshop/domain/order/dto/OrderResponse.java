package com.vibeshop.domain.order.dto;

import com.vibeshop.domain.order.entity.Order;
import com.vibeshop.domain.order.entity.OrderItem;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class OrderResponse {
    private final Long id;
    private final List<OrderItemDto> items;
    private final BigDecimal totalAmount;
    private final String status;
    private final String shippingAddress;
    private final String receiverName;
    private final String receiverPhone;
    private final LocalDateTime createdAt;

    public OrderResponse(Order order) {
        this.id = order.getId();
        this.items = order.getOrderItems().stream().map(OrderItemDto::new).toList();
        this.totalAmount = order.getTotalAmount();
        this.status = order.getStatus().name();
        this.shippingAddress = order.getShippingAddress();
        this.receiverName = order.getReceiverName();
        this.receiverPhone = order.getReceiverPhone();
        this.createdAt = order.getCreatedAt();
    }

    @Getter
    public static class OrderItemDto {
        private final Long productId;
        private final String productName;
        private final String imageUrl;
        private final Integer quantity;
        private final BigDecimal price;
        private final BigDecimal subtotal;

        public OrderItemDto(OrderItem item) {
            this.productId = item.getProduct().getId();
            this.productName = item.getProduct().getName();
            this.imageUrl = item.getProduct().getImageUrl();
            this.quantity = item.getQuantity();
            this.price = item.getPrice();
            this.subtotal = item.getSubtotal();
        }
    }
}
