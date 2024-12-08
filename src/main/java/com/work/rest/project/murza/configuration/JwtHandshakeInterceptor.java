package com.work.rest.project.murza.configuration;

import com.work.rest.project.murza.entity.User;
import com.work.rest.project.murza.repository.UserRepository;
import com.work.rest.project.murza.service.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request, ServerHttpResponse response,
            WebSocketHandler wsHandler, Map<String, Object> attributes) {

        String token = extractTokenFromQuery(request);
        if (token == null) {
            log.error("Missing or invalid token in WebSocket handshake");
            return false;
        }

        String username = extractUsernameFromToken(token);
        if (username == null) {
            log.error("Invalid token provided for WebSocket handshake");
            return false;
        }

        Optional<User> userOptional = userRepository.findByEmail(username);
        if (userOptional.isPresent()) {
            attributes.put("username", userOptional.get().getEmail());
            log.info("WebSocket handshake successful for user: {}", username);
            return true;
        } else {
            log.error("User not found for username: {}", username);
            return false;
        }
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request, ServerHttpResponse response,
            WebSocketHandler wsHandler, Exception exception) {
        if (exception != null) {
            log.error("Error during WebSocket handshake: {}", exception.getMessage());
        } else {
            log.info("WebSocket handshake completed successfully");
        }
    }

    private String extractTokenFromQuery(ServerHttpRequest request) {
        String query = request.getURI().getQuery();
        if (query == null || !query.contains("token=")) {
            return null;
        }

        for (String param : query.split("&")) {
            if (param.startsWith("token=")) {
                return param.substring("token=".length());
            }
        }

        return null;
    }

    private String extractUsernameFromToken(String token) {
        try {
            return jwtService.extractUsername(token);
        } catch (Exception e) {
            log.error("Failed to extract username from token: {}", e.getMessage());
            return null;
        }
    }
}
