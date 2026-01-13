package com.example.demo.calendar.repository;

import com.example.demo.calendar.entity.Calendar;
import com.example.demo.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CalendarRepository extends JpaRepository<Calendar, UUID> {

    List<Calendar> findByOwner(User owner);
}


