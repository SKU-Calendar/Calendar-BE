package com.example.demo.notification.ws;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketAuthInterceptor webSocketAuthInterceptor;
    private final UserIdHandshakeHandler userIdHandshakeHandler;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // ✅ 너가 원한 WS 엔드포인트: /api/notifications
        registry.addEndpoint("/api/notifications")
                .addInterceptors(webSocketAuthInterceptor)
                .setHandshakeHandler(userIdHandshakeHandler)
                .setAllowedOriginPatterns("*");
        // 필요하면 .withSockJS() 추가 가능
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 서버 -> 클라 push: /user/queue/notifications
        registry.enableSimpleBroker("/queue");
        registry.setUserDestinationPrefix("/user");
        // (클라 -> 서버 메시지용 prefix는 지금 사용 안 하니까 생략 가능)
    }
}
