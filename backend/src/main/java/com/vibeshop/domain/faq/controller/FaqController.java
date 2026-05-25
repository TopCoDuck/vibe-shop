package com.vibeshop.domain.faq.controller;

import com.vibeshop.domain.faq.dto.FaqResponse;
import com.vibeshop.domain.faq.entity.Faq;
import com.vibeshop.domain.faq.service.FaqService;
import com.vibeshop.global.common.ApiResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/faq")
@RequiredArgsConstructor
public class FaqController {

    private final FaqService faqService;

    /** 공개 API: active FAQ 전체 조회 */
    @GetMapping
    public ResponseEntity<ApiResponse<List<FaqResponse>>> getPublic() {
        return ResponseEntity.ok(ApiResponse.success(faqService.getActiveFaqs()));
    }

    // ── 관리자 API ──

    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<List<Faq>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(faqService.getAll()));
    }

    @PostMapping("/admin")
    public ResponseEntity<ApiResponse<Faq>> create(@RequestBody FaqRequest req) {
        return ResponseEntity.ok(ApiResponse.success(
                faqService.create(req.getCategory(), req.getQuestion(),
                        req.getAnswer(), req.getLink(), req.getSortOrder())));
    }

    @PutMapping("/admin/{id}")
    public ResponseEntity<ApiResponse<Faq>> update(@PathVariable Long id,
                                                    @RequestBody FaqRequest req) {
        return ResponseEntity.ok(ApiResponse.success(
                faqService.update(id, req.getCategory(), req.getQuestion(),
                        req.getAnswer(), req.getLink(), req.getSortOrder(), req.isActive())));
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        faqService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("삭제되었습니다.", null));
    }

    @Getter
    static class FaqRequest {
        private String category;
        private String question;
        private String answer;
        private String link;
        private int sortOrder;
        private boolean active = true;
    }
}
