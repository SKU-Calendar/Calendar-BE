package com.example.demo.chat.service;

import com.example.demo.calendar.entity.Calendar;
import com.example.demo.calendar.repository.CalendarRepository;
import com.example.demo.chat.dto.AiEventResponseDto;
import com.example.demo.chat.dto.ChatRequestDto;
import com.example.demo.chat.dto.ChatResponseDto;
import com.example.demo.chat.entity.ChatMessage;
import com.example.demo.chat.entity.ChatSession;
import com.example.demo.chat.repository.ChatMessageRepository;
import com.example.demo.chat.repository.ChatSessionRepository;
import com.example.demo.event.entity.Event;
import com.example.demo.event.repository.EventRepository;
import com.example.demo.eventslot.entity.EventSlot;
import com.example.demo.eventslot.repository.EventSlotRepository;
import com.example.demo.user.entity.User;
import com.example.demo.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatService.class);

    private final ChatMessageRepository chatMessageRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final AiService aiService;
    private final CalendarRepository calendarRepository;
    private final EventRepository eventRepository;
    private final EventSlotRepository eventSlotRepository;
    private final UserRepository userRepository;

    public ChatService(ChatMessageRepository chatMessageRepository,
                       ChatSessionRepository chatSessionRepository,
                       AiService aiService,
                       CalendarRepository calendarRepository,
                       EventRepository eventRepository,
                       EventSlotRepository eventSlotRepository,
                       UserRepository userRepository) {
        this.chatMessageRepository = chatMessageRepository;
        this.chatSessionRepository = chatSessionRepository;
        this.aiService = aiService;
        this.calendarRepository = calendarRepository;
        this.eventRepository = eventRepository;
        this.eventSlotRepository = eventSlotRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ChatResponseDto sendMessage(UUID chatId, ChatRequestDto request) {
        User currentUser = getCurrentUser();

        ChatSession session = chatSessionRepository.findById(chatId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chat session not found"));

        // ✅ 권한/매핑 체크 로그
        log.info("Chat send check: sessionId={}, sessionUserId={}, currentUserId={}, sessionCalendarId={}, requestCalendarId={}",
                chatId, session.getUserId(), currentUser.getId(), session.getCalendarId(), request.getCalendarId());

        if (!session.getUserId().equals(currentUser.getId())) {
            log.warn("Chat send forbidden: sessionId={}, sessionUserId={}, currentUserId={}",
                    chatId, session.getUserId(), currentUser.getId());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No permission on chat session");
        }

        if (!session.getCalendarId().equals(request.getCalendarId())) {
            log.warn("Chat send forbidden: sessionId={}, sessionCalendarId={}, requestCalendarId={}",
                    chatId, session.getCalendarId(), request.getCalendarId());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Chat session does not belong to calendar");
        }

        Calendar calendar = calendarRepository.findById(session.getCalendarId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Calendar not found"));

        // ✅ 단계 로그 1
        log.info("Chat send step: calendar loaded. sessionId={}", chatId);

        ChatMessage userMessage = new ChatMessage(
                UUID.randomUUID(),
                chatId,
                ChatMessage.MessageRole.USER,
                request.getMessage()
        );
        chatMessageRepository.save(userMessage);

        // ✅ 단계 로그 2
        log.info("Chat send step: user message saved. sessionId={}, messageId={}", chatId, userMessage.getId());

        try {
            AiEventResponseDto aiResponse = aiService.generateEvent(request.getMessage());

            // ✅ 단계 로그 3
            log.info("Chat send step: AI response ok. sessionId={}", chatId);

            String aiResponseJson = convertToJson(aiResponse);
            ChatMessage aiMessage = new ChatMessage(
                    UUID.randomUUID(),
                    chatId,
                    ChatMessage.MessageRole.ASSISTANT,
                    aiResponseJson
            );
            chatMessageRepository.save(aiMessage);

            Event event = new Event(
                    UUID.randomUUID(),
                    calendar,
                    currentUser,
                    "ACTIVE",
                    aiResponse.getStartAt(),
                    aiResponse.getEndAt(),
                    null // color는 기본값 null
            );
            Event savedEvent = eventRepository.save(event);

            if (aiResponse.getSlots() != null) {
                int slotIndex = 0;
                for (AiEventResponseDto.AiSlotDto slotDto : aiResponse.getSlots()) {
                    EventSlot slot = new EventSlot(
                            UUID.randomUUID(),
                            savedEvent,
                            slotDto.getSlotTitle() != null ? slotDto.getSlotTitle() : "",
                            slotIndex++,
                            slotDto.getSlotStartAt(),
                            slotDto.getSlotEndAt(),
                            false
                    );
                    eventSlotRepository.save(slot);
                }
            }

            return new ChatResponseDto(
                    aiMessage.getId(),
                    aiMessage.getSessionId(),
                    mapRoleToString(aiMessage.getRole()),
                    aiMessage.getContent(),
                    aiMessage.getCreatedAt()
            );
        } catch (RuntimeException e) {
            // ✅ “403이 진짜 어디서 났는지” 확정 로그
            if (e instanceof WebClientResponseException w) {
                log.error("Chat send failed by WebClient: status={}, body={}",
                        w.getStatusCode(), w.getResponseBodyAsString());
            } else {
                log.error("Chat send failed: type={}, msg={}",
                        e.getClass().getName(), e.getMessage(), e);
            }

            String errorMessage = "일정 생성 실패: " + e.getMessage();
            ChatMessage errorMessageEntity = new ChatMessage(
                    UUID.randomUUID(),
                    chatId,
                    ChatMessage.MessageRole.ASSISTANT,
                    errorMessage
            );
            chatMessageRepository.save(errorMessageEntity);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage, e);
        }
    }

    @Transactional(readOnly = true)
    public List<ChatResponseDto> getMessages(UUID chatId) {
        User currentUser = getCurrentUser();

        ChatSession session = chatSessionRepository.findById(chatId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chat session not found"));

        if (!session.getUserId().equals(currentUser.getId())) {
            log.warn("Chat read forbidden: sessionId={}, sessionUserId={}, currentUserId={}",
                    chatId, session.getUserId(), currentUser.getId());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No permission on chat session");
        }

        List<ChatMessage> messages = chatMessageRepository.findBySessionIdOrderByCreatedAtAsc(chatId);
        List<ChatResponseDto> responses = new ArrayList<>();
        for (ChatMessage message : messages) {
            responses.add(new ChatResponseDto(
                    message.getId(),
                    message.getSessionId(),
                    mapRoleToString(message.getRole()),
                    message.getContent(),
                    message.getCreatedAt()
            ));
        }
        return responses;
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

    /**
     * MessageRole enum을 String으로 변환합니다.
     * DB 제약조건에 맞게 USER, ASSISTANT, SYSTEM만 반환합니다.
     */
    private String mapRoleToString(ChatMessage.MessageRole role) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
        return role.name();
    }

    /**
     * 외부에서 입력받은 role 문자열을 MessageRole enum으로 변환합니다.
     * "AI"는 "ASSISTANT"로 매핑됩니다.
     * 허용되지 않는 값은 IllegalArgumentException을 발생시킵니다.
     */
    public static ChatMessage.MessageRole parseRole(String roleStr) {
        if (roleStr == null || roleStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Role cannot be null or empty");
        }
        
        String normalized = roleStr.trim().toUpperCase();
        
        // "AI"를 "ASSISTANT"로 매핑
        if ("AI".equals(normalized)) {
            normalized = "ASSISTANT";
        }
        
        try {
            ChatMessage.MessageRole role = ChatMessage.MessageRole.valueOf(normalized);
            // USER, ASSISTANT, SYSTEM만 허용
            if (role != ChatMessage.MessageRole.USER 
                    && role != ChatMessage.MessageRole.ASSISTANT 
                    && role != ChatMessage.MessageRole.SYSTEM) {
                throw new IllegalArgumentException(
                        "Invalid role: " + roleStr + ". Allowed values: USER, ASSISTANT, SYSTEM (or AI which maps to ASSISTANT)");
            }
            return role;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid role: " + roleStr + ". Allowed values: USER, ASSISTANT, SYSTEM (or AI which maps to ASSISTANT)", e);
        }
    }

    private String convertToJson(AiEventResponseDto dto) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            mapper.findAndRegisterModules();
            return mapper.writeValueAsString(dto);
        } catch (Exception e) {
            return "{\"error\": \"JSON 변환 실패\"}";
        }
    }
}