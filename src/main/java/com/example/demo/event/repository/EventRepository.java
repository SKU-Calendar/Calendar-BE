package com.example.demo.event.repository;

import com.example.demo.calendar.entity.Calendar;
import com.example.demo.event.entity.Event;
import com.example.demo.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {

    List<Event> findByCalendar(Calendar calendar);

    Optional<Event> findByIdAndCalendar(UUID id, Calendar calendar);

    List<Event> findByCalendarAndCreatedBy(Calendar calendar, User createdBy);
}


