package com.example.demo.eventslot.repository;

import com.example.demo.event.entity.Event;
import com.example.demo.eventslot.entity.EventSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface EventSlotRepository extends JpaRepository<EventSlot, UUID> {

    List<EventSlot> findByEvent(Event event);

    @Query("SELECT s FROM EventSlot s " +
           "JOIN s.event e " +
           "WHERE e.calendar.id = :calendarId " +
           "AND DATE(s.slotStartAt) = :date " +
           "ORDER BY s.slotStartAt ASC")
    List<EventSlot> findByCalendarIdAndStartDate(
            @Param("calendarId") UUID calendarId,
            @Param("date") LocalDate date
    );
}


