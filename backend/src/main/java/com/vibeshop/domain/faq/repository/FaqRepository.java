package com.vibeshop.domain.faq.repository;

import com.vibeshop.domain.faq.entity.Faq;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FaqRepository extends JpaRepository<Faq, Long> {
    List<Faq> findByActiveTrueOrderByCategoryAscSortOrderAsc();
    List<Faq> findByCategoryAndActiveTrueOrderBySortOrderAsc(String category);
}
