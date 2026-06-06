package com.vibeshop.domain.faq.dto;

import lombok.Getter;

@Getter
public class FaqRequest {
    private String category;
    private String question;
    private String answer;
    private String link;
    private int sortOrder;
    private boolean active = true;
}
