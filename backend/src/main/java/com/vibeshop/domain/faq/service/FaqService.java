package com.vibeshop.domain.faq.service;

import com.vibeshop.domain.faq.dto.FaqResponse;
import com.vibeshop.domain.faq.entity.Faq;
import com.vibeshop.domain.faq.repository.FaqRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FaqService {

    private final FaqRepository faqRepository;

    /** 공개용: active 항목만, 카테고리+정렬순 */
    public List<FaqResponse> getActiveFaqs() {
        return faqRepository.findByActiveTrueOrderByCategoryAscSortOrderAsc()
                .stream().map(FaqResponse::new).toList();
    }

    /** 관리자용: 전체 */
    public List<Faq> getAll() {
        return faqRepository.findAll();
    }

    @Transactional
    public Faq create(String category, String question, String answer,
                      String link, int sortOrder) {
        Faq faq = Faq.builder()
                .category(category)
                .question(question)
                .answer(answer)
                .link(link)
                .sortOrder(sortOrder)
                .active(true)
                .build();
        return faqRepository.save(faq);
    }

    @Transactional
    public Faq update(Long id, String category, String question, String answer,
                      String link, int sortOrder, boolean active) {
        Faq faq = faqRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("FAQ를 찾을 수 없습니다."));
        faq.update(category, question, answer, link, sortOrder, active);
        return faq;
    }

    @Transactional
    public void delete(Long id) {
        faqRepository.deleteById(id);
    }
}
