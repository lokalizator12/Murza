package com.work.rest.project.murza.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.work.rest.project.murza.entity.Message;
import com.work.rest.project.murza.entity.User;
import com.work.rest.project.murza.repository.UserRepository;
import com.work.rest.project.murza.service.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final Map<Long, WebSocketSession> userSessions = new ConcurrentHashMap<>();


    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Long userId = extractUserIdFromSession(session);
        if (userId != null) {
            userSessions.put(userId, session);
            log.info("User with ID {} connected.", userId);
        } else {
            try {
                session.close(CloseStatus.BAD_DATA);
            } catch (Exception e) {
                log.error("Failed to close session", e);
            }
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Long senderId = extractUserIdFromSession(session);
        if (senderId != null) {
            log.info("Received message from user {}: {}", senderId, message.getPayload());
            // Дополнительная логика может быть добавлена при необходимости
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Long userId = extractUserIdFromSession(session);
        if (userId != null) {
            userSessions.remove(userId);
            log.info("User with ID {} disconnected.", userId);
        }
    }

    public void sendMessageToUser(Long userId, Message message) {
        log.info("{}: {}", userId, message);
        WebSocketSession session = userSessions.get(userId);
        log.info(session.toString());
        log.info(session.getId());
        log.info(String.valueOf(session.isOpen()));
        if (session != null && session.isOpen()) {
            try {
                String messageJson = new ObjectMapper()
                        .registerModule(new JavaTimeModule())
                        .writeValueAsString(message);
                session.sendMessage(new TextMessage(messageJson));
                log.info("Sent message to user {}: {}", userId, messageJson);
            } catch (Exception e) {
                log.error("Failed to send message to user {}", userId, e);
            }
        } else {
            log.info("Failed to send message. User session not available or closed for user: {}", userId);
        }
    }

    private Long extractUserIdFromSession(WebSocketSession session) {
        // Извлекаем токен из параметров URL
        String query = session.getUri().getQuery();
        if (query != null && query.startsWith("token=")) {
            try {
                String token = query.split("=")[1];
                String username = jwtService.extractUsername(token);
                if (username != null) {
                    Optional<User> user = userRepository.findByEmail(username);
                    return user.map(User::getId).orElse(null);
                }
            } catch (Exception e) {
                log.error("Failed to extract userId from WebSocket connection", e);
            }
        }
        return null;
    }
}
