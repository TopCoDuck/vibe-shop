package com.vibeshop.domain.inquiry.controller;

import com.vibeshop.domain.inquiry.dto.InquiryRequest;
import com.vibeshop.domain.inquiry.dto.InquiryResponse;
import com.vibeshop.domain.inquiry.service.InquiryService;
import com.vibeshop.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inquiries")
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;

    /** 내 문의 목록 */
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<InquiryResponse>>> getMyInquiries(
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(ApiResponse.success(
                inquiryService.getMyInquiries(user.getUsername())));
    }

    /** 문의 등록 */
    @PostMapping
    public ResponseEntity<ApiResponse<InquiryResponse>> create(
            @AuthenticationPrincipal UserDetails user,
            @RequestBody InquiryRequest req) {
        return ResponseEntity.ok(ApiResponse.success(
                inquiryService.create(user.getUsername(), req)));
    }

    /** 관리자: 전체 문의 조회 */
    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<List<InquiryResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(inquiryService.getAll()));
    }
}
