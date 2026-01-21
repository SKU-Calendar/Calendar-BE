package com.example.demo.notification.ws;

import com.example.demo.notification.support.NotificationAuthResolver;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    private final NotificationAuthResolver authResolver;

    @Override
    public boolean beforeHandshake(
            org.springframework.http.server.ServerHttpRequest request,
            org.springframework.http.server.ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) {
        String token = null;

        // 1) Authorization: Bearer
        if (request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            String auth = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (auth != null && auth.startsWith("Bearer ")) token = auth.substring(7);
        }

        // 2) ?token=... (옵션)
        if (token == null && request instanceof org.springframework.http.server.ServletServerHttpRequest servletReq) {
            HttpServletRequest r = servletReq.getServletRequest();
            token = r.getParameter("token");
        }

        // token으로 userId 확보 -> attributes에 저장
        UUID userId = authResolver.resolveUserIdFromTokenOnly(token);
        attributes.put("userId", userId.toString());
        return true;
    }

    @Override
    public void afterHandshake(
            org.springframework.http.server.ServerHttpRequest request,
            org.springframework.http.server.ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception
    ) { }
}
