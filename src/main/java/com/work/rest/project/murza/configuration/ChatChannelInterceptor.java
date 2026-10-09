package com.work.rest.project.murza.configuration;

import com.work.rest.project.murza.entity.User;
import com.work.rest.project.murza.service.BlacklistJwtService;
import com.work.rest.project.murza.service.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class ChatChannelInterceptor implements ChannelInterceptor {
    private final JwtService jwt;
    private final BlacklistJwtService blacklist;
    private static final Set<String> SEND_DESTINATIONS = Set.of(
            "/app/chat.sendMessage", "/app/chat.readReceipt", "/app/presence/online", "/app/presence/offline");
    private static final Set<String> PRIVATE_SUBSCRIPTIONS = Set.of(
            "/user/queue/messages", "/user/queue/read-receipts");

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headers = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (headers == null || headers.getCommand() == null) return message;
        StompCommand command = headers.getCommand();
        if (command == StompCommand.CONNECT || command == StompCommand.SEND || command == StompCommand.SUBSCRIBE) {
            var session = headers.getSessionAttributes();
            if (session == null || !(session.get("accessToken") instanceof String token) ||
                    !(session.get("authenticatedUser") instanceof User user)) throw denied();
            try {
                if (blacklist.isTokenBlacklisted(token) || !jwt.isTokenValid(token, user)) throw denied();
            } catch (RuntimeException exception) {
                throw denied();
            }
        }
        if (command == StompCommand.CONNECT) {
            Object username = headers.getSessionAttributes() == null ? null : headers.getSessionAttributes().get("username");
            if (!(username instanceof String name) || name.isBlank()) throw denied();
            headers.setUser(new UsernamePasswordAuthenticationToken(name, null, java.util.List.of()));
        } else if (command == StompCommand.SEND || command == StompCommand.SUBSCRIBE) {
            if (headers.getUser() == null) throw denied();
            String destination = headers.getDestination();
            if (command == StompCommand.SEND && (destination == null || !SEND_DESTINATIONS.contains(destination))) throw denied();
            if (command == StompCommand.SUBSCRIBE && (destination == null || (!PRIVATE_SUBSCRIPTIONS.contains(destination) &&
                    !destination.matches("/topic/presence/[1-9][0-9]*")))) throw denied();
        }
        return message;
    }

    private AccessDeniedException denied() {
        return new AccessDeniedException("This messaging destination is not available");
    }
}
