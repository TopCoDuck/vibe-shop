package com.vibeshop.domain.inquiry.service;

import com.vibeshop.domain.inquiry.dto.InquiryRequest;
import com.vibeshop.domain.inquiry.dto.InquiryResponse;
import com.vibeshop.domain.inquiry.entity.Inquiry;
import com.vibeshop.domain.inquiry.repository.InquiryRepository;
import com.vibeshop.domain.user.entity.User;
import com.vibeshop.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final UserRepository userRepository;

    /** 내 문의 목록 조회 */
    public List<InquiryResponse> getMyInquiries(String email) {
        User user = getUser(email);
        return inquiryRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream().map(InquiryResponse::new).toList();
    }

    /** 문의 등록 */
    @Transactional
    public InquiryResponse create(String email, InquiryRequest req) {
        User user = getUser(email);
        Inquiry inquiry = Inquiry.builder()
                .user(user)
                .type(req.getType())
                .content(req.getContent())
                .replyMethod(req.getReplyMethod())
                .imageUrls(req.getImageUrls())
                .build();
        return new InquiryResponse(inquiryRepository.save(inquiry));
    }

    /** 관리자: 전체 문의 조회 */
    public List<InquiryResponse> getAll() {
        return inquiryRepository.findAll().stream()
                .map(InquiryResponse::new).toList();
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }
}
