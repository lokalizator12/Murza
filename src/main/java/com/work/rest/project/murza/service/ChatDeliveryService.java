package com.work.rest.project.murza.service;

import com.work.rest.project.murza.entity.Message;
import com.work.rest.project.murza.entity.User;
import com.work.rest.project.murza.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatDeliveryService {
    private final MessageService messages;
    private final UserRepository users;
    private final SimpMessagingTemplate broker;
    private final StringRedisTemplate redis;
    private static final DefaultRedisScript<Long> SEND_LIMIT = new DefaultRedisScript<>(
            "local n = redis.call('INCR', KEYS[1]); if n == 1 then redis.call('EXPIRE', KEYS[1], 60) end; return n", Long.class);

    public Message send(User sender, Long receiverId, String content) {
        if (receiverId == null || receiverId <= 0 || receiverId.equals(sender.getId()) ||
                content == null || content.isBlank() || content.length() > 2000) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Choose another person and enter 1–2000 characters");
        }
        User receiver = users.findById(receiverId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "This person is no longer available"));
        Long count = redis.execute(SEND_LIMIT, List.of("chat:send-limit:" + sender.getId()));
        if (count == null || count > 30) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Sending limit reached. Wait one minute and try again");
        }
        Message saved = messages.sendMessage(sender.getId(), receiverId, content.strip());
        // Keep encrypted storage separate from the object returned to the browser.
        Message delivered = Message.builder().id(saved.getId()).conversationId(saved.getConversationId())
                .senderId(sender.getId()).receiverId(receiverId).timestamp(saved.getTimestamp())
                .status(saved.getStatus()).content(messages.decryptMessageContent(saved.getContent())).build();
        broker.convertAndSendToUser(receiver.getEmail(), "/queue/messages", delivered);
        broker.convertAndSendToUser(sender.getEmail(), "/queue/messages", delivered);
        return delivered;
    }
}
