package com.vibeshop.domain.coupon.repository;

import com.vibeshop.domain.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

    List<Coupon> findByActiveTrueAndEndDateGreaterThanEqualOrderByIdAsc(LocalDate date);
}
