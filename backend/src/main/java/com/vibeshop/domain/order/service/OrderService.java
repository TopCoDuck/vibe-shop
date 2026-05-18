package com.vibeshop.domain.order.service;

import com.vibeshop.domain.cart.entity.CartItem;
import com.vibeshop.domain.cart.repository.CartItemRepository;
import com.vibeshop.domain.order.dto.OrderRequest;
import com.vibeshop.domain.order.dto.OrderResponse;
import com.vibeshop.domain.order.entity.Order;
import com.vibeshop.domain.order.entity.OrderItem;
import com.vibeshop.domain.order.entity.OrderStatus;
import com.vibeshop.domain.order.repository.OrderRepository;
import com.vibeshop.domain.user.entity.User;
import com.vibeshop.domain.user.repository.UserRepository;
import com.vibeshop.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;

    @Transactional
    public OrderResponse createOrder(String email, OrderRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> BusinessException.notFound("사용자를 찾을 수 없습니다."));

        List<CartItem> cartItems = cartItemRepository.findAllById(request.getCartItemIds());
        if (cartItems.isEmpty()) {
            throw BusinessException.badRequest("선택한 장바구니 항목이 없습니다.");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        Order order = Order.builder()
                .user(user)
                .shippingAddress(request.getShippingAddress())
                .receiverName(request.getReceiverName())
                .receiverPhone(request.getReceiverPhone())
                .totalAmount(BigDecimal.ZERO)
                .build();

        for (CartItem cartItem : cartItems) {
            if (!cartItem.getUser().getId().equals(user.getId())) {
                throw BusinessException.forbidden("접근 권한이 없습니다.");
            }
            cartItem.getProduct().decreaseStock(cartItem.getQuantity());

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(cartItem.getProduct())
                    .quantity(cartItem.getQuantity())
                    .price(cartItem.getProduct().getPrice())
                    .build();
            order.addOrderItem(orderItem);
            totalAmount = totalAmount.add(orderItem.getSubtotal());
        }

        // totalAmount 설정을 위한 새 Order 생성 (불변 필드이므로)
        Order savedOrder = Order.builder()
                .user(user)
                .shippingAddress(request.getShippingAddress())
                .receiverName(request.getReceiverName())
                .receiverPhone(request.getReceiverPhone())
                .totalAmount(totalAmount)
                .build();

        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = OrderItem.builder()
                    .order(savedOrder)
                    .product(cartItem.getProduct())
                    .quantity(cartItem.getQuantity())
                    .price(cartItem.getProduct().getPrice())
                    .build();
            savedOrder.addOrderItem(orderItem);
        }

        Order result = orderRepository.save(savedOrder);
        cartItemRepository.deleteAll(cartItems);

        return new OrderResponse(result);
    }

    public Page<OrderResponse> getMyOrders(String email, Pageable pageable) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> BusinessException.notFound("사용자를 찾을 수 없습니다."));
        return orderRepository.findByUserId(user.getId(), pageable).map(OrderResponse::new);
    }

    public OrderResponse getOrder(String email, Long orderId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> BusinessException.notFound("사용자를 찾을 수 없습니다."));
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> BusinessException.notFound("주문을 찾을 수 없습니다."));
        if (!order.getUser().getId().equals(user.getId())) {
            throw BusinessException.forbidden("접근 권한이 없습니다.");
        }
        return new OrderResponse(order);
    }

    @Transactional
    public OrderResponse cancelOrder(String email, Long orderId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> BusinessException.notFound("사용자를 찾을 수 없습니다."));
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> BusinessException.notFound("주문을 찾을 수 없습니다."));
        if (!order.getUser().getId().equals(user.getId())) {
            throw BusinessException.forbidden("접근 권한이 없습니다.");
        }
        if (order.getStatus() != OrderStatus.PENDING) {
            throw BusinessException.badRequest("취소할 수 없는 주문 상태입니다.");
        }
        order.updateStatus(OrderStatus.CANCELLED);
        order.getOrderItems().forEach(item -> item.getProduct().increaseStock(item.getQuantity()));
        return new OrderResponse(order);
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> BusinessException.notFound("주문을 찾을 수 없습니다."));
        order.updateStatus(status);
        return new OrderResponse(order);
    }

    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable).map(OrderResponse::new);
    }
}
