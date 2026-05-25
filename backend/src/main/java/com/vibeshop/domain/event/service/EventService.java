package com.vibeshop.domain.event.service;

import com.vibeshop.domain.event.dto.EventResponse;
import com.vibeshop.domain.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {

    private final EventRepository eventRepository;

    /** 활성화된 이벤트 전체 (기간 무관 — 프론트에서 상태 계산) */
    public List<EventResponse> getAll() {
        return eventRepository.findByActiveTrueOrderBySortOrderAscIdAsc()
                .stream().map(EventResponse::new).toList();
    }
}
