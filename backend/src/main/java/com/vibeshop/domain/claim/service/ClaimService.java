package com.vibeshop.domain.claim.service;

import com.vibeshop.domain.claim.dto.AdminClaimStatusRequest;
import com.vibeshop.domain.claim.dto.ClaimRequest;
import com.vibeshop.domain.claim.dto.ClaimResponse;
import com.vibeshop.domain.claim.entity.Claim;
import com.vibeshop.domain.claim.entity.ClaimType;
import com.vibeshop.domain.claim.repository.ClaimRepository;
import com.vibeshop.domain.order.entity.Order;
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

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    /** 클레임 접수 */
    @Transactional
    public ClaimResponse create(Long orderId, String email, ClaimRequest request) {
        User user = getUser(email);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> BusinessException.notFound("주문을 찾을 수 없습니다."));

        // 본인 주문인지 확인
        if (!order.getUser().getId().equals(user.getId())) {
            throw BusinessException.forbidden("접근 권한이 없습니다.");
        }

        // 이미 클레임 존재 여부
        if (claimRepository.existsByOrderId(orderId)) {
            throw BusinessException.badRequest("이미 클레임이 접수된 주문입니다.");
        }

        // 클레임 유형별 가능한 주문 상태 검증
        validateClaimable(order.getStatus(), request.getType());

        Claim claim = Claim.builder()
                .user(user)
                .order(order)
                .type(request.getType())
                .reason(request.getReason())
                .reasonDetail(request.getReasonDetail())
                .imageUrl(request.getImageUrl())
                .build();

        return new ClaimResponse(claimRepository.save(claim));
    }

    /** 내 클레임 목록 */
    public List<ClaimResponse> getMyClaims(String email) {
        User user = getUser(email);
        return claimRepository.findByUserIdWithOrder(user.getId())
                .stream().map(ClaimResponse::new).toList();
    }

    /** 클레임 상세 (본인만) */
    public ClaimResponse getById(Long claimId, String email) {
        User user = getUser(email);
        Claim claim = claimRepository.findByIdAndUserId(claimId, user.getId())
                .orElseThrow(() -> BusinessException.notFound("클레임을 찾을 수 없습니다."));
        return new ClaimResponse(claim);
    }

    /** 주문별 클레임 조회 (없으면 null 반환) */
    public ClaimResponse getByOrder(Long orderId, String email) {
        User user = getUser(email);
        return claimRepository.findByOrderId(orderId)
                .map(claim -> {
                    if (!claim.getUser().getId().equals(user.getId())) {
                        throw BusinessException.forbidden("접근 권한이 없습니다.");
                    }
                    return new ClaimResponse(claim);
                })
                .orElse(null);
    }

    /** 클레임 취소 (REQUESTED 상태만, 본인만) */
    @Transactional
    public void cancel(Long claimId, String email) {
        User user = getUser(email);
        Claim claim = claimRepository.findByIdAndUserId(claimId, user.getId())
                .orElseThrow(() -> BusinessException.notFound("클레임을 찾을 수 없습니다."));
        claim.cancel();
    }

    /** [관리자] 전체 클레임 목록 */
    public Page<ClaimResponse> getAllForAdmin(Pageable pageable) {
        return claimRepository.findAllWithDetails(pageable).map(ClaimResponse::new);
    }

    /** [관리자] 클레임 상태 변경 */
    @Transactional
    public ClaimResponse updateStatus(Long claimId, AdminClaimStatusRequest request) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> BusinessException.notFound("클레임을 찾을 수 없습니다."));
        claim.updateStatus(request.getStatus(), request.getAdminComment());
        return new ClaimResponse(claim);
    }

    // ── 내부 검증 ──

    private void validateClaimable(OrderStatus orderStatus, ClaimType claimType) {
        boolean valid = switch (claimType) {
            case CANCEL -> orderStatus == OrderStatus.PENDING || orderStatus == OrderStatus.PAID;
            case RETURN, EXCHANGE, REFUND -> orderStatus == OrderStatus.DELIVERED;
        };
        if (!valid) {
            throw BusinessException.badRequest(
                    "현재 주문 상태(" + orderStatus + ")에서는 " + claimType + " 클레임을 신청할 수 없습니다.");
        }
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }
}
