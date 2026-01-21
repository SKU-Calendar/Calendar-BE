package com.example.demo.notification.ws;

import com.example.demo.config.jwt.JwtTokenProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

@Component
public class NotificationWsHandshakeInterceptor implements HandshakeInterceptor {

    public static final String ATTR_USER_ID = "USER_ID";

    private final JwtTokenProvider jwtTokenProvider;

    public NotificationWsHandshakeInterceptor(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {

        String token = resolveToken(request);
        if (!StringUtils.hasText(token)) return false;

        UUID userId = jwtTokenProvider.getUserId(token);
        if (userId == null) return false;

        attributes.put(ATTR_USER_ID, userId);
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) { }

    private String resolveToken(ServerHttpRequest request) {
        String auth = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(auth) && auth.startsWith("Bearer ")) {
            return auth.substring(7);
        }

        URI uri = request.getURI();
        String query = uri.getQuery();
        if (!StringUtils.hasText(query)) return null;

        for (String part : query.split("&")) {
            String[] kv = part.split("=", 2);
            if (kv.length == 2 && kv[0].equals("token") && StringUtils.hasText(kv[1])) {
                return kv[1];
            }
        }
        return null;
    }
}
