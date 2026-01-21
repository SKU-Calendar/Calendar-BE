package com.example.demo.notification.ws;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class NotificationWebSocketConfig implements WebSocketConfigurer {

    private final NotificationWebSocketHandler handler;
    private final NotificationWsHandshakeInterceptor interceptor;

    public NotificationWebSocketConfig(NotificationWebSocketHandler handler,
                                       NotificationWsHandshakeInterceptor interceptor) {
        this.handler = handler;
        this.interceptor = interceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, "/api/notifications")
                .addInterceptors(interceptor)
                .setAllowedOrigins("*"); // 운영에서는 도메인 제한 권장
    }
}
