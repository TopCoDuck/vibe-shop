package com.vibeshop.domain.wishlist.controller;

import com.vibeshop.domain.wishlist.dto.WishlistResponse;
import com.vibeshop.domain.wishlist.service.WishlistService;
import com.vibeshop.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<WishlistResponse>>> getMyWishlist(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                wishlistService.getMyWishlist(userDetails.getUsername())));
    }

    @GetMapping("/ids")
    public ResponseEntity<ApiResponse<Set<Long>>> getMyWishlistIds(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                wishlistService.getMyWishlistProductIds(userDetails.getUsername())));
    }

    @GetMapping("/{productId}/status")
    public ResponseEntity<ApiResponse<Boolean>> isWished(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.success(
                wishlistService.isWished(userDetails.getUsername(), productId)));
    }

    @PostMapping("/{productId}/toggle")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> toggle(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long productId) {
        boolean wished = wishlistService.toggle(userDetails.getUsername(), productId);
        return ResponseEntity.ok(ApiResponse.success(Map.of("wished", wished)));
    }
}
