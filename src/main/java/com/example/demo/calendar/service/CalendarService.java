package com.example.demo.calendar.service;

import com.example.demo.calendar.dto.CalendarCreateRequestDto;
import com.example.demo.calendar.dto.CalendarDetailResponseDto;
import com.example.demo.calendar.dto.CalendarResponseDto;
import com.example.demo.calendar.entity.Calendar;
import com.example.demo.calendar.repository.CalendarRepository;
import com.example.demo.chat.entity.ChatSession;
import com.example.demo.chat.repository.ChatSessionRepository;
import com.example.demo.event.dto.EventResponseDto;
import com.example.demo.event.entity.Event;
import com.example.demo.event.repository.EventRepository;
import com.example.demo.eventslot.dto.EventSlotResponseDto;
import com.example.demo.eventslot.dto.EventSlotWithEventIdResponseDto;
import com.example.demo.eventslot.service.EventSlotService;
import com.example.demo.user.entity.User;
import com.example.demo.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CalendarService {

    private final CalendarRepository calendarRepository;
    private final EventRepository eventRepository;
    private final EventSlotService eventSlotService;
    private final UserRepository userRepository;
    private final ChatSessionRepository chatSessionRepository;

    public CalendarService(CalendarRepository calendarRepository,
                           EventRepository eventRepository,
                           EventSlotService eventSlotService,
                           UserRepository userRepository,
                           ChatSessionRepository chatSessionRepository) {
        this.calendarRepository = calendarRepository;
        this.eventRepository = eventRepository;
        this.eventSlotService = eventSlotService;
        this.userRepository = userRepository;
        this.chatSessionRepository = chatSessionRepository;
    }

    @Transactional
    public CalendarResponseDto createCalendar(CalendarCreateRequestDto request) {
        User currentUser = getCurrentUser();

        Calendar calendar = new Calendar(
                UUID.randomUUID(),
                currentUser,
                request.getTimezone()
        );

        Calendar saved = calendarRepository.save(calendar);

        // 채팅방 자동 생성 (중복 방지)
        ChatSession chatSession = chatSessionRepository.findByCalendarId(saved.getId())
                .orElseGet(() -> {
                    ChatSession newSession = new ChatSession(
                            UUID.randomUUID(),
                            currentUser.getId(),
                            saved.getId()
                    );
                    return chatSessionRepository.save(newSession);
                });

        return toResponse(saved, chatSession.getId());
    }

    @Transactional(readOnly = true)
    public List<CalendarResponseDto> getMyCalendars() {
        User currentUser = getCurrentUser();
        List<Calendar> calendars = calendarRepository.findByOwner(currentUser);

        List<CalendarResponseDto> responses = new ArrayList<>();
        for (Calendar calendar : calendars) {
            UUID chatSessionId = chatSessionRepository.findByCalendarId(calendar.getId())
                    .map(ChatSession::getId)
                    .orElse(null);
            responses.add(toResponse(calendar, chatSessionId));
        }
        return responses;
    }

    @Transactional(readOnly = true)
    public CalendarDetailResponseDto getCalendarDetail(UUID calendarId) {
        User currentUser = getCurrentUser();

        Calendar calendar = calendarRepository.findById(calendarId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Calendar not found"));

        if (!calendar.getOwner().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No permission on calendar");
        }

        List<Event> events = eventRepository.findByCalendar(calendar);
        List<EventResponseDto> eventResponses = new ArrayList<>();
        for (Event event : events) {
            List<EventSlotResponseDto> slots = eventSlotService.findByEvent(event);
            eventResponses.add(new EventResponseDto(
                    event.getId(),
                    event.getStatus().name(),
                    event.getStartAt(),
                    event.getEndAt(),
                    event.getColor(),
                    event.getCreatedAt(),
                    event.getUpdatedAt(),
                    slots
            ));
        }

        return new CalendarDetailResponseDto(
                calendar.getId(),
                calendar.getTimezone(),
                calendar.getCreatedAt(),
                calendar.getUpdatedAt(),
                eventResponses
        );
    }

    @Transactional(readOnly = true)
    public List<EventSlotWithEventIdResponseDto> getSlotsByDay(UUID calendarId, LocalDate date) {
        User currentUser = getCurrentUser();

        Calendar calendar = calendarRepository.findById(calendarId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Calendar not found"));

        if (!calendar.getOwner().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No permission on calendar");
        }

        return eventSlotService.findByCalendarAndDate(calendarId, date);
    }

    private CalendarResponseDto toResponse(Calendar calendar, UUID chatSessionId) {
        return new CalendarResponseDto(
            calendar.getId(),
            calendar.getTimezone(),
            chatSessionId,
            calendar.getCreatedAt(),
            calendar.getUpdatedAt()
        );
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthenticated");
        }
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }
}


