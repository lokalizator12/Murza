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

        String query = request.getURI().getQuery();
        if (query == null || !query.contains("=")) {
            log.error("Invalid query string in WebSocket request");
            return false;
        }

        String[] parts = query.split("=");
        if (parts.length < 2) {
            log.error("Unable to extract token from query string");
            return false;
        }

        String token = parts[1];
        String username = jwtService.extractUsername(token);
        log.info("Extracted username: {}", username);

        User user = userRepository.findByEmail(username).orElse(null);
        if (user != null) {
            attributes.put("username", user.getEmail());
            return true;
        }

        log.error("User not found for username: {}", username);
        return false;
    }


    @Override
    public void afterHandshake(
            ServerHttpRequest request, ServerHttpResponse response,
            WebSocketHandler wsHandler, Exception exception) {
    }
}
