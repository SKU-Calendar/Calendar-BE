package com.example.demo.eventslot.service;

import com.example.demo.calendar.entity.Calendar;
import com.example.demo.calendar.repository.CalendarRepository;
import com.example.demo.event.entity.Event;
import com.example.demo.event.entity.EventStatus;
import com.example.demo.event.repository.EventRepository;
import com.example.demo.eventslot.dto.EventSlotCreateWithoutEventRequestDto;
import com.example.demo.eventslot.dto.EventSlotDoneRequestDto;
import com.example.demo.eventslot.dto.EventSlotRequestDto;
import com.example.demo.eventslot.dto.EventSlotResponseDto;
import com.example.demo.eventslot.dto.EventSlotUpdateRequestDto;
import com.example.demo.eventslot.dto.EventSlotWithEventIdResponseDto;
import com.example.demo.eventslot.entity.EventSlot;
import com.example.demo.eventslot.repository.EventSlotRepository;
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
public class EventSlotService {

    private final EventSlotRepository eventSlotRepository;
    private final EventRepository eventRepository;
    private final CalendarRepository calendarRepository;
    private final UserRepository userRepository;

    public EventSlotService(EventSlotRepository eventSlotRepository,
                            EventRepository eventRepository,
                            CalendarRepository calendarRepository,
                            UserRepository userRepository) {
        this.eventSlotRepository = eventSlotRepository;
        this.eventRepository = eventRepository;
        this.calendarRepository = calendarRepository;
        this.userRepository = userRepository;
    }

    public List<EventSlotResponseDto> createSlots(Event event, List<EventSlotRequestDto> requests) {
        if (requests == null || requests.isEmpty()) {
            return List.of();
        }

        List<EventSlotResponseDto> responses = new ArrayList<>();

        for (EventSlotRequestDto request : requests) {
            EventSlot slot = new EventSlot(
                    UUID.randomUUID(),
                    event,
                    request.getSlotTitle(),
                    request.getSlotIndex(),
                    request.getSlotStartAt(),
                    request.getSlotEndAt(),
                    request.getIsDone()
            );
            EventSlot saved = eventSlotRepository.save(slot);
            responses.add(toResponse(saved));
        }

        return responses;
    }

    public List<EventSlotResponseDto> findByEvent(Event event) {
        List<EventSlot> slots = eventSlotRepository.findByEvent(event);
        List<EventSlotResponseDto> responses = new ArrayList<>();
        for (EventSlot slot : slots) {
            responses.add(toResponse(slot));
        }
        return responses;
    }

    public List<EventSlotResponseDto> replaceSlots(Event event, List<EventSlotRequestDto> requests) {
        // 기존 슬롯 삭제 후 새로 생성
        List<EventSlot> existing = eventSlotRepository.findByEvent(event);
        eventSlotRepository.deleteAll(existing);
        return createSlots(event, requests);
    }

    @Transactional
    public EventSlotResponseDto updateSlot(UUID slotId, EventSlotUpdateRequestDto request) {
        EventSlot slot = eventSlotRepository.findById(slotId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event slot not found"));

        slot.update(
                request.getSlotTitle(),
                null, // slotIndex는 수정하지 않음
                request.getSlotStartAt(),
                request.getSlotEndAt(),
                request.getIsDone()
        );

        EventSlot saved = eventSlotRepository.save(slot);
        return toResponse(saved);
    }

    @Transactional
    public EventSlotResponseDto updateDone(UUID slotId, EventSlotDoneRequestDto request) {
        EventSlot slot = eventSlotRepository.findById(slotId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event slot not found"));

        slot.update(
                null, // slotTitle
                null, // slotIndex
                null, // slotStartAt
                null, // slotEndAt
                request.getIsDone() // isDone만 변경
        );

        EventSlot saved = eventSlotRepository.save(slot);
        return toResponse(saved);
    }

    @Transactional
    public void deleteSlot(UUID slotId) {
        EventSlot slot = eventSlotRepository.findById(slotId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event slot not found"));

        eventSlotRepository.delete(slot);
    }

    @Transactional(readOnly = true)
    public List<EventSlotWithEventIdResponseDto> findByCalendarAndDate(UUID calendarId, LocalDate date) {
        List<EventSlot> slots = eventSlotRepository.findByCalendarIdAndStartDate(calendarId, date);
        List<EventSlotWithEventIdResponseDto> responses = new ArrayList<>();
        for (EventSlot slot : slots) {
            responses.add(toResponseWithEventId(slot));
        }
        return responses;
    }

    @Transactional
    public EventSlotWithEventIdResponseDto createSlotWithoutEvent(EventSlotCreateWithoutEventRequestDto request) {
        User currentUser = getCurrentUser();

        Calendar calendar = calendarRepository.findById(request.getCalendarId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Calendar not found"));

        // 권한 검증: 캘린더 소유자인지 확인
        if (!calendar.getOwner().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No permission on calendar");
        }

        // Event 자동 생성
        Event event = new Event(
                UUID.randomUUID(),
                calendar,
                currentUser,
                EventStatus.PLANNED, // ACTIVE는 PLANNED로 매핑
                request.getSlotStartAt(),
                request.getSlotEndAt(),
                null // color는 기본값 null
        );
        Event savedEvent = eventRepository.save(event);

        // EventSlot 생성
        EventSlot slot = new EventSlot(
                UUID.randomUUID(),
                savedEvent,
                request.getSlotTitle(),
                request.getSlotIndex(),
                request.getSlotStartAt(),
                request.getSlotEndAt(),
                false // is_done 기본값 false
        );
        EventSlot savedSlot = eventSlotRepository.save(slot);

        return toResponseWithEventId(savedSlot);
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

    private EventSlotResponseDto toResponse(EventSlot slot) {
        return new EventSlotResponseDto(
                slot.getId(),
                slot.getSlotStartAt(),
                slot.getSlotEndAt(),
                slot.getSlotIndex(),
                slot.getSlotTitle(),
                slot.getIsDone()
        );
    }

    private EventSlotWithEventIdResponseDto toResponseWithEventId(EventSlot slot) {
        return new EventSlotWithEventIdResponseDto(
                slot.getId(),
                slot.getEvent().getId(),
                slot.getSlotStartAt(),
                slot.getSlotEndAt(),
                slot.getSlotIndex(),
                slot.getSlotTitle(),
                slot.getIsDone()
        );
    }
}


