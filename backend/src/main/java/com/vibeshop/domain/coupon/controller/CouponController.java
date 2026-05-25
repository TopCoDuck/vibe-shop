package com.vibeshop.domain.coupon.controller;

import com.vibeshop.domain.coupon.dto.CouponResponse;
import com.vibeshop.domain.coupon.dto.MyCouponResponse;
import com.vibeshop.domain.coupon.service.CouponService;
import com.vibeshop.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    /** 다운로드 가능한 쿠폰 목록 (비로그인 가능, 로그인 시 다운여부 포함) */
    @GetMapping
    public ResponseEntity<ApiResponse<List<CouponResponse>>> getAvailable(
            @AuthenticationPrincipal UserDetails user) {
        String email = user != null ? user.getUsername() : null;
        return ResponseEntity.ok(ApiResponse.success(couponService.getAvailable(email)));
    }

    /** 쿠폰 다운로드 */
    @PostMapping("/{id}/download")
    public ResponseEntity<ApiResponse<Void>> download(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails user) {
        couponService.download(user.getUsername(), id);
        return ResponseEntity.ok(ApiResponse.success("쿠폰이 다운로드되었습니다.", null));
    }

    /** 내 쿠폰 목록 */
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<MyCouponResponse>>> getMyCoupons(
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(ApiResponse.success(couponService.getMyCoupons(user.getUsername())));
    }
}
