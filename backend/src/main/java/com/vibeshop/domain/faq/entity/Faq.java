package com.vibeshop.domain.faq.entity;

import com.vibeshop.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "faqs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Faq extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(nullable = false)
    private String question;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String answer;

    private String link;

    @Column(nullable = false)
    private int sortOrder;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    public void update(String category, String question, String answer,
                       String link, int sortOrder, boolean active) {
        this.category = category;
        this.question = question;
        this.answer   = answer;
        this.link     = link;
        this.sortOrder = sortOrder;
        this.active   = active;
    }
}
