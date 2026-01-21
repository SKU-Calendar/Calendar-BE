package com.example.demo.notification.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Set;
import java.util.UUID;

import static com.example.demo.notification.ws.NotificationWsHandshakeInterceptor.ATTR_USER_ID;

@Component
public class NotificationWebSocketHandler extends TextWebSocketHandler {

    private final NotificationWsSessionRegistry registry;
    private final ObjectMapper objectMapper;

    public NotificationWebSocketHandler(NotificationWsSessionRegistry registry,
                                        ObjectMapper objectMapper) {
        this.registry = registry;
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        UUID userId = (UUID) session.getAttributes().get(ATTR_USER_ID);
        if (userId == null) {
            closeQuietly(session);
            return;
        }
        registry.add(userId, session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        UUID userId = (UUID) session.getAttributes().get(ATTR_USER_ID);
        if (userId != null) registry.remove(userId, session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        // push-only
    }

    public void pushToUser(UUID userId, NotificationWsMessage msg) {
        Set<WebSocketSession> sessions = registry.get(userId);
        if (sessions.isEmpty()) return;

        final String json;
        try {
            json = objectMapper.writeValueAsString(msg);
        } catch (Exception e) {
            return;
        }

        TextMessage tm = new TextMessage(json);
        for (WebSocketSession s : sessions) {
            if (!s.isOpen()) continue;
            try { s.sendMessage(tm); } catch (Exception ignored) {}
        }
    }

    private void closeQuietly(WebSocketSession session) {
        try { session.close(); } catch (Exception ignored) {}
    }
}
