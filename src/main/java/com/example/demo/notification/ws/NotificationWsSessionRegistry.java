package com.example.demo.notification.ws;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class NotificationWsSessionRegistry {

    private final ConcurrentHashMap<UUID, Set<WebSocketSession>> sessionsByUser = new ConcurrentHashMap<>();

    public void add(UUID userId, WebSocketSession session) {
        sessionsByUser.compute(userId, (k, set) -> {
            if (set == null) set = ConcurrentHashMap.newKeySet();
            set.add(session);
            return set;
        });
    }

    public void remove(UUID userId, WebSocketSession session) {
        Set<WebSocketSession> set = sessionsByUser.get(userId);
        if (set == null) return;
        set.remove(session);
        if (set.isEmpty()) sessionsByUser.remove(userId);
    }

    public Set<WebSocketSession> get(UUID userId) {
        return sessionsByUser.getOrDefault(userId, Set.of());
    }
}
