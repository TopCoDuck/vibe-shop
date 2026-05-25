package com.vibeshop.domain.event.controller;

import com.vibeshop.domain.event.dto.EventResponse;
import com.vibeshop.domain.event.service.EventService;
import com.vibeshop.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<EventResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(eventService.getAll()));
    }
}
