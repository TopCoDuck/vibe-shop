package com.vibeshop.domain.claim.controller;

import com.vibeshop.domain.claim.dto.AdminClaimStatusRequest;
import com.vibeshop.domain.claim.dto.ClaimRequest;
import com.vibeshop.domain.claim.dto.ClaimResponse;
import com.vibeshop.domain.claim.service.ClaimService;
import com.vibeshop.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ClaimController {

    private final ClaimService claimService;

    /** 클레임 접수 */
    @PostMapping("/api/orders/{orderId}/claims")
    public ResponseEntity<ApiResponse<ClaimResponse>> create(
            @PathVariable Long orderId,
            @RequestBody @Valid ClaimRequest request,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(ApiResponse.success(claimService.create(orderId, user.getUsername(), request)));
    }

    /** 주문별 클레임 조회 (없으면 data: null) */
    @GetMapping("/api/orders/{orderId}/claims")
    public ResponseEntity<ApiResponse<ClaimResponse>> getByOrder(
            @PathVariable Long orderId,
            @AuthenticationPrincipal UserDetails user) {
        ClaimResponse claim = claimService.getByOrder(orderId, user.getUsername());
        return ResponseEntity.ok(ApiResponse.success(claim));
    }

    /** 내 클레임 목록 */
    @GetMapping("/api/claims/my")
    public ResponseEntity<ApiResponse<List<ClaimResponse>>> getMyClaims(
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(ApiResponse.success(claimService.getMyClaims(user.getUsername())));
    }

    /** 클레임 상세 */
    @GetMapping("/api/claims/{claimId}")
    public ResponseEntity<ApiResponse<ClaimResponse>> getById(
            @PathVariable Long claimId,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(ApiResponse.success(claimService.getById(claimId, user.getUsername())));
    }

    /** 클레임 취소 */
    @DeleteMapping("/api/claims/{claimId}")
    public ResponseEntity<ApiResponse<Void>> cancel(
            @PathVariable Long claimId,
            @AuthenticationPrincipal UserDetails user) {
        claimService.cancel(claimId, user.getUsername());
        return ResponseEntity.ok(ApiResponse.success("클레임이 취소되었습니다.", null));
    }

    /** [관리자] 전체 클레임 목록 */
    @GetMapping("/api/claims/admin")
    public ResponseEntity<ApiResponse<Page<ClaimResponse>>> getAllForAdmin(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(claimService.getAllForAdmin(pageable)));
    }

    /** [관리자] 상태 변경 */
    @PutMapping("/api/claims/admin/{claimId}/status")
    public ResponseEntity<ApiResponse<ClaimResponse>> updateStatus(
            @PathVariable Long claimId,
            @RequestBody @Valid AdminClaimStatusRequest request) {
        return ResponseEntity.ok(ApiResponse.success(claimService.updateStatus(claimId, request)));
    }
}
