package com.vibeshop.domain.event.repository;

import com.vibeshop.domain.event.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByActiveTrueOrderBySortOrderAscIdAsc();

    List<Event> findByActiveTrueAndEndDateGreaterThanEqualOrderBySortOrderAscIdAsc(LocalDate date);
}
