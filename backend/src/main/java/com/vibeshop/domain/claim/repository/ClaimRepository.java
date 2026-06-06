package com.vibeshop.domain.claim.repository;

import com.vibeshop.domain.claim.entity.Claim;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClaimRepository extends JpaRepository<Claim, Long> {

    List<Claim> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Claim> findByOrderId(Long orderId);

    Optional<Claim> findByIdAndUserId(Long id, Long userId);

    boolean existsByOrderId(Long orderId);

    @Query("SELECT c FROM Claim c JOIN FETCH c.user JOIN FETCH c.order ORDER BY c.createdAt DESC")
    Page<Claim> findAllWithDetails(Pageable pageable);

    @Query("SELECT c FROM Claim c JOIN FETCH c.order WHERE c.user.id = :userId ORDER BY c.createdAt DESC")
    List<Claim> findByUserIdWithOrder(@Param("userId") Long userId);
}
