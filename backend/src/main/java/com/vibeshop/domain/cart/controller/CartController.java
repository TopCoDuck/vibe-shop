package com.vibeshop.domain.cart.controller;

import com.vibeshop.domain.cart.dto.CartItemResponse;
import com.vibeshop.domain.cart.service.CartService;
import com.vibeshop.global.common.ApiResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CartItemResponse>>> getCart(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(cartService.getCart(userDetails.getUsername())));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CartItemResponse>> addItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CartRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                cartService.addItem(userDetails.getUsername(), request.getProductId(), request.getQuantity())));
    }

    @PutMapping("/{cartItemId}")
    public ResponseEntity<ApiResponse<CartItemResponse>> updateQuantity(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long cartItemId,
            @RequestBody QuantityRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                cartService.updateQuantity(userDetails.getUsername(), cartItemId, request.getQuantity())));
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<ApiResponse<Void>> removeItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long cartItemId) {
        cartService.removeItem(userDetails.getUsername(), cartItemId);
        return ResponseEntity.ok(ApiResponse.success("장바구니에서 삭제되었습니다.", null));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearCart(@AuthenticationPrincipal UserDetails userDetails) {
        cartService.clearCart(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("장바구니를 비웠습니다.", null));
    }

    @Getter
    static class CartRequest {
        private Long productId;
        private int quantity;
    }

    @Getter
    static class QuantityRequest {
        private int quantity;
    }
}
