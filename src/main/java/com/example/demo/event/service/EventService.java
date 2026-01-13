package com.example.demo.event.service;

import com.example.demo.calendar.entity.Calendar;
import com.example.demo.calendar.repository.CalendarRepository;
import com.example.demo.event.dto.EventCreateRequestDto;
import com.example.demo.event.dto.EventDeleteRequestDto;
import com.example.demo.event.dto.EventResponseDto;
import com.example.demo.event.dto.EventUpdateRequestDto;
import com.example.demo.event.entity.Event;
import com.example.demo.event.entity.EventStatus;
import com.example.demo.event.repository.EventRepository;
import com.example.demo.eventslot.dto.EventSlotResponseDto;
import com.example.demo.eventslot.service.EventSlotService;
import com.example.demo.user.entity.User;
import com.example.demo.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final CalendarRepository calendarRepository;
    private final EventSlotService eventSlotService;
    private final UserRepository userRepository;

    public EventService(EventRepository eventRepository,
                        CalendarRepository calendarRepository,
                        EventSlotService eventSlotService,
                        UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.calendarRepository = calendarRepository;
        this.eventSlotService = eventSlotService;
        this.userRepository = userRepository;
    }

    @Transactional
    public EventResponseDto createEvent(UUID pathUserId, UUID calendarId, EventCreateRequestDto request) {
        User currentUser = getCurrentUser();
        ensurePathUserMatches(pathUserId, currentUser);

        Calendar calendar = calendarRepository.findById(calendarId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Calendar not found"));

        ensureCalendarOwner(calendar, currentUser);

        EventStatus status = parseAndMapStatus(request.getStatus());

        Event event = new Event(
                UUID.randomUUID(),
                calendar,
                currentUser,
                status,
                request.getStartAt(),
                request.getEndAt(),
                request.getColor()
        );

        Event saved = eventRepository.save(event);

        List<EventSlotResponseDto> slotResponses = eventSlotService.createSlots(saved, request.getSlots());

        return toResponse(saved, slotResponses);
    }

    @Transactional(readOnly = true)
    public List<EventResponseDto> getEvents(UUID pathUserId, UUID calendarId) {
        User currentUser = getCurrentUser();
        ensurePathUserMatches(pathUserId, currentUser);

        Calendar calendar = calendarRepository.findById(calendarId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Calendar not found"));

        ensureCalendarAccess(calendar, currentUser);

        List<Event> events = eventRepository.findByCalendar(calendar);
        List<EventResponseDto> responses = new ArrayList<>();
        for (Event event : events) {
            List<EventSlotResponseDto> slots = eventSlotService.findByEvent(event);
            responses.add(toResponse(event, slots));
        }
        return responses;
    }

    @Transactional
    public EventResponseDto updateEvent(UUID pathUserId, UUID calendarId, EventUpdateRequestDto request) {
        User currentUser = getCurrentUser();
        ensurePathUserMatches(pathUserId, currentUser);

        Calendar calendar = calendarRepository.findById(calendarId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Calendar not found"));

        Event event = eventRepository.findByIdAndCalendar(request.getEventId(), calendar)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));

        ensureEventAccess(calendar, event, currentUser);

        EventStatus status = request.getStatus() != null 
                ? parseAndMapStatus(request.getStatus()) 
                : null;

        event.update(
                status,
                request.getStartAt(),
                request.getEndAt(),
                request.getColor()
        );

        List<EventSlotResponseDto> slots = null;
        if (request.getSlots() != null) {
            slots = eventSlotService.replaceSlots(event, request.getSlots());
        } else {
            slots = eventSlotService.findByEvent(event);
        }

        return toResponse(event, slots);
    }

    @Transactional
    public void deleteEvent(UUID pathUserId, UUID calendarId, EventDeleteRequestDto request) {
        User currentUser = getCurrentUser();
        ensurePathUserMatches(pathUserId, currentUser);

        Calendar calendar = calendarRepository.findById(calendarId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Calendar not found"));

        Event event = eventRepository.findByIdAndCalendar(request.getEventId(), calendar)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));

        ensureEventAccess(calendar, event, currentUser);

        eventRepository.delete(event);
    }

    private EventResponseDto toResponse(Event event, List<EventSlotResponseDto> slots) {
        return new EventResponseDto(
                event.getId(),
                event.getStatus().name(),
                event.getStartAt(),
                event.getEndAt(),
                event.getColor(),
                event.getCreatedAt(),
                event.getUpdatedAt(),
                slots
        );
    }

    /**
     * 외부에서 입력받은 status 문자열을 EventStatus enum으로 변환합니다.
     * "ACTIVE"는 "PLANNED"로 매핑됩니다.
     * 허용되지 않는 값은 ResponseStatusException(400 Bad Request)을 발생시킵니다.
     */
    private EventStatus parseAndMapStatus(String statusStr) {
        if (statusStr == null || statusStr.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "Status cannot be null or empty. Allowed values: PLANNED, CONFIRMED, DONE, CANCELLED (or ACTIVE which maps to PLANNED)");
        }

        String normalized = statusStr.trim().toUpperCase();

        // "ACTIVE"를 "PLANNED"로 매핑
        if ("ACTIVE".equals(normalized)) {
            normalized = "PLANNED";
        }

        try {
            EventStatus status = EventStatus.valueOf(normalized);
            return status;
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid status: " + statusStr + ". Allowed values: PLANNED, CONFIRMED, DONE, CANCELLED (or ACTIVE which maps to PLANNED)");
        }
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

    private void ensurePathUserMatches(UUID pathUserId, User currentUser) {
        if (pathUserId != null && !pathUserId.equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User mismatch");
        }
    }

    private void ensureCalendarOwner(Calendar calendar, User currentUser) {
        if (!calendar.getOwner().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No permission on calendar");
        }
    }

    private void ensureCalendarAccess(Calendar calendar, User currentUser) {
        if (!calendar.getOwner().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No permission on calendar");
        }
    }

    private void ensureEventAccess(Calendar calendar, Event event, User currentUser) {
        UUID userId = currentUser.getId();
        boolean calendarOwner = calendar.getOwner().getId().equals(userId);
        boolean eventCreator = event.getCreatedBy().getId().equals(userId);
        if (!calendarOwner && !eventCreator) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No permission on event");
        }
    }
}


