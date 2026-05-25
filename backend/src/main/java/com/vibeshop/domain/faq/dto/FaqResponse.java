package com.vibeshop.domain.faq.dto;

import com.vibeshop.domain.faq.entity.Faq;
import lombok.Getter;

@Getter
public class FaqResponse {
    private final Long id;
    private final String category;
    private final String question;
    private final String answer;
    private final String link;
    private final int sortOrder;

    public FaqResponse(Faq faq) {
        this.id        = faq.getId();
        this.category  = faq.getCategory();
        this.question  = faq.getQuestion();
        this.answer    = faq.getAnswer();
        this.link      = faq.getLink();
        this.sortOrder = faq.getSortOrder();
    }
}
