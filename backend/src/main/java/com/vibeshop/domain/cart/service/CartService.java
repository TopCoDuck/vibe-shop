package com.vibeshop.domain.cart.service;

import com.vibeshop.domain.cart.dto.CartItemResponse;
import com.vibeshop.domain.cart.entity.CartItem;
import com.vibeshop.domain.cart.repository.CartItemRepository;
import com.vibeshop.domain.product.entity.Product;
import com.vibeshop.domain.product.repository.ProductRepository;
import com.vibeshop.domain.user.entity.User;
import com.vibeshop.domain.user.repository.UserRepository;
import com.vibeshop.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public List<CartItemResponse> getCart(String email) {
        User user = getUser(email);
        return cartItemRepository.findByUserIdWithProduct(user.getId())
                .stream().map(CartItemResponse::new).toList();
    }

    @Transactional
    public CartItemResponse addItem(String email, Long productId, int quantity) {
        User user = getUser(email);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> BusinessException.notFound("상품을 찾을 수 없습니다."));

        if (product.getStock() < quantity) {
            throw BusinessException.badRequest("재고가 부족합니다.");
        }

        CartItem cartItem = cartItemRepository.findByUserIdAndProductId(user.getId(), productId)
                .map(existing -> {
                    existing.updateQuantity(existing.getQuantity() + quantity);
                    return existing;
                })
                .orElseGet(() -> CartItem.builder()
                        .user(user)
                        .product(product)
                        .quantity(quantity)
                        .build());

        return new CartItemResponse(cartItemRepository.save(cartItem));
    }

    @Transactional
    public CartItemResponse updateQuantity(String email, Long cartItemId, int quantity) {
        User user = getUser(email);
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> BusinessException.notFound("장바구니 항목을 찾을 수 없습니다."));

        if (!cartItem.getUser().getId().equals(user.getId())) {
            throw BusinessException.forbidden("접근 권한이 없습니다.");
        }
        if (quantity <= 0) {
            cartItemRepository.delete(cartItem);
            return null;
        }
        cartItem.updateQuantity(quantity);
        return new CartItemResponse(cartItem);
    }

    @Transactional
    public void removeItem(String email, Long cartItemId) {
        User user = getUser(email);
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> BusinessException.notFound("장바구니 항목을 찾을 수 없습니다."));
        if (!cartItem.getUser().getId().equals(user.getId())) {
            throw BusinessException.forbidden("접근 권한이 없습니다.");
        }
        cartItemRepository.delete(cartItem);
    }

    @Transactional
    public void clearCart(String email) {
        User user = getUser(email);
        cartItemRepository.deleteByUserId(user.getId());
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> BusinessException.notFound("사용자를 찾을 수 없습니다."));
    }
}
