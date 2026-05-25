package com.vibeshop.domain.coupon.service;

import com.vibeshop.domain.coupon.dto.CouponResponse;
import com.vibeshop.domain.coupon.dto.MyCouponResponse;
import com.vibeshop.domain.coupon.entity.Coupon;
import com.vibeshop.domain.coupon.entity.UserCoupon;
import com.vibeshop.domain.coupon.repository.CouponRepository;
import com.vibeshop.domain.coupon.repository.UserCouponRepository;
import com.vibeshop.domain.user.entity.User;
import com.vibeshop.domain.user.repository.UserRepository;
import com.vibeshop.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CouponService {

    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;
    private final UserRepository userRepository;

    /** 현재 다운로드 가능한 쿠폰 목록 (비로그인 가능) */
    public List<CouponResponse> getAvailable(String email) {
        List<Coupon> coupons = couponRepository
                .findByActiveTrueAndEndDateGreaterThanEqualOrderByIdAsc(LocalDate.now());

        Set<Long> downloaded = Set.of();
        if (email != null) {
            User user = getUser(email);
            downloaded = userCouponRepository.findByUserIdWithCoupon(user.getId())
                    .stream().map(uc -> uc.getCoupon().getId())
                    .collect(Collectors.toSet());
        }

        final Set<Long> dl = downloaded;
        return coupons.stream().map(c -> {
            CouponResponse r = new CouponResponse(c);
            r.setAlreadyDownloaded(dl.contains(c.getId()));
            return r;
        }).toList();
    }

    /** 쿠폰 다운로드 */
    @Transactional
    public void download(String email, Long couponId) {
        User user = getUser(email);
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> BusinessException.notFound("쿠폰을 찾을 수 없습니다."));

        if (!coupon.isDownloadable())
            throw BusinessException.badRequest("다운로드할 수 없는 쿠폰입니다.");

        if (userCouponRepository.existsByUserIdAndCouponId(user.getId(), couponId))
            throw BusinessException.badRequest("이미 다운로드한 쿠폰입니다.");

        coupon.incrementDownload();
        userCouponRepository.save(
                UserCoupon.builder().user(user).coupon(coupon).build()
        );
    }

    /** 내 쿠폰 목록 */
    public List<MyCouponResponse> getMyCoupons(String email) {
        User user = getUser(email);
        return userCouponRepository.findByUserIdWithCoupon(user.getId())
                .stream().map(MyCouponResponse::new).toList();
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }
}
