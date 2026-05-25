package com.vibeshop.domain.event.dto;

import com.vibeshop.domain.event.entity.Event;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class EventResponse {
    private final Long id;
    private final String badge;
    private final String title;
    private final String subtitle;
    private final String description;
    private final String background;
    private final String textColor;
    private final String accentColor;
    private final String link;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final int sortOrder;

    public EventResponse(Event e) {
        this.id = e.getId();
        this.badge = e.getBadge();
        this.title = e.getTitle();
        this.subtitle = e.getSubtitle();
        this.description = e.getDescription();
        this.background = e.getBackground();
        this.textColor = e.getTextColor();
        this.accentColor = e.getAccentColor();
        this.link = e.getLink();
        this.startDate = e.getStartDate();
        this.endDate = e.getEndDate();
        this.sortOrder = e.getSortOrder();
    }
}
