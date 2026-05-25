package com.vibeshop.domain.event.entity;

import com.vibeshop.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "events")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Event extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String badge;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 200)
    private String subtitle;

    @Column(columnDefinition = "TEXT")
    private String description;

    /** CSS gradient or solid color string */
    @Column(nullable = false, length = 200)
    private String background;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String textColor = "#ffffff";

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String accentColor = "#ffffff";

    /** 이동할 링크 (e.g. /products, /products?categoryId=1) */
    @Column(nullable = false, length = 200)
    private String link;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    @Builder.Default
    private int sortOrder = 0;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;
}
