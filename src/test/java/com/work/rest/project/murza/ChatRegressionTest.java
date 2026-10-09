package com.work.rest.project.murza;

import com.work.rest.project.murza.configuration.ChatChannelInterceptor;
import com.work.rest.project.murza.dto.MessageRequest;
import com.work.rest.project.murza.entity.Message;
import com.work.rest.project.murza.entity.MessageStatus;
import com.work.rest.project.murza.entity.User;
import com.work.rest.project.murza.repository.UserRepository;
import com.work.rest.project.murza.service.ChatDeliveryService;
import com.work.rest.project.murza.service.MessageService;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChatRegressionTest {
    private final MessageService messages = mock(MessageService.class);
    private final UserRepository users = mock(UserRepository.class);
    private final SimpMessagingTemplate broker = mock(SimpMessagingTemplate.class);
    private final StringRedisTemplate redis = mock(StringRedisTemplate.class);
    private final ChatDeliveryService delivery = new ChatDeliveryService(messages, users, broker, redis);

    @Test
    void onlyPersonalQueuesCanBeSubscribedTo() {
        assertThrows(AccessDeniedException.class, () -> frame(StompCommand.SUBSCRIBE, "/queue/messages-user-other", true));
        assertThrows(AccessDeniedException.class, () -> frame(StompCommand.SUBSCRIBE, "/user/other/queue/messages", true));
        assertThrows(AccessDeniedException.class, () -> frame(StompCommand.SUBSCRIBE, "/topic/messages", true));
        assertDoesNotThrow(() -> frame(StompCommand.SUBSCRIBE, "/user/queue/messages", true));
        assertDoesNotThrow(() -> frame(StompCommand.SUBSCRIBE, "/user/queue/read-receipts", true));
    }

    @Test
    void clientsCannotPublishToBrokerQueuesOrForgePresenceTopics() {
        assertThrows(AccessDeniedException.class, () -> frame(StompCommand.SEND, "/queue/messages", true));
        assertThrows(AccessDeniedException.class, () -> frame(StompCommand.SEND, "/topic/presence/2", true));
        assertThrows(AccessDeniedException.class, () -> frame(StompCommand.SEND, "/app/chat.readReceipt", false));
        assertDoesNotThrow(() -> frame(StompCommand.SEND, "/app/chat.readReceipt", true));
    }

    @Test
    void messagePayloadRejectsMissingRecipientAndOversizedContent() {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            var validator = factory.getValidator();
            MessageRequest request = new MessageRequest();
            request.setContent(" ");
            assertEquals(2, validator.validate(request).size());
            request.setReceiverId(2L);
            request.setContent("x".repeat(2001));
            assertEquals(1, validator.validate(request).size());
        }
    }

    @Test
    void blankOversizedAndSelfMessagesNeverReachStorage() {
        assertThrows(ResponseStatusException.class, () -> delivery.send(user(1L), 2L, " "));
        assertThrows(ResponseStatusException.class, () -> delivery.send(user(1L), 2L, "x".repeat(2001)));
        assertThrows(ResponseStatusException.class, () -> delivery.send(user(1L), 1L, "hello"));
        verifyNoInteractions(messages, redis, broker, users);
    }

    @Test
    void sendingLimitStopsStorageAndBroadcast() {
        when(users.findById(2L)).thenReturn(Optional.of(user(2L)));
        when(redis.execute(any(RedisScript.class), anyList())).thenReturn(31L);
        var error = assertThrows(ResponseStatusException.class, () -> delivery.send(user(1L), 2L, "hello"));
        assertEquals(429, error.getStatusCode().value());
        verifyNoInteractions(messages, broker);
    }

    @Test
    void confirmedMessageIsReturnedAndEncryptedObjectIsNotMutated() {
        User receiver = user(2L);
        receiver.setEmail("receiver@example.test");
        User sender = user(1L);
        sender.setEmail("sender@example.test");
        when(users.findById(2L)).thenReturn(Optional.of(receiver));
        when(redis.execute(any(RedisScript.class), anyList())).thenReturn(1L);
        Message encrypted = Message.builder().id("message-1").content("encrypted").timestamp(Instant.now())
                .conversationId("conversation-1").status(MessageStatus.SENT).build();
        when(messages.sendMessage(1L, 2L, "hello")).thenReturn(encrypted);
        when(messages.decryptMessageContent("encrypted")).thenReturn("hello");
        Message response = delivery.send(sender, 2L, "  hello  ");
        assertEquals("hello", response.getContent());
        assertEquals("encrypted", encrypted.getContent());
        verify(broker).convertAndSendToUser("sender@example.test", "/queue/messages", response);
        verify(broker).convertAndSendToUser("receiver@example.test", "/queue/messages", response);
    }

    @Test
    void readReceiptOnlyUpdatesIncomingUnreadMessages() {
        var repository = mock(com.work.rest.project.murza.repository.MessageRepository.class);
        var conversations = mock(com.work.rest.project.murza.repository.ConversationRepository.class);
        var service = new com.work.rest.project.murza.service.impl.MessageServiceImpl(
                repository, conversations, users, mock(com.work.rest.project.murza.service.EncryptionService.class));
        when(conversations.findByUsers(1L, 2L)).thenReturn(Optional.of(
                com.work.rest.project.murza.entity.Conversation.builder().id("conversation").build()));
        Message incoming = Message.builder().id("incoming").receiverId(1L).status(MessageStatus.SENT).build();
        when(repository.findByConversationIdAndReceiverIdAndStatus("conversation", 1L, MessageStatus.SENT))
                .thenReturn(java.util.List.of(incoming));
        service.markMessagesAsRead(1L, 2L);
        assertEquals(MessageStatus.READ, incoming.getStatus());
        verify(repository).saveAll(java.util.List.of(incoming));
        verify(repository, never()).findByConversationIdOrderByTimestampDesc(anyString());
    }

    @Test
    void revocationAlsoStopsAnAlreadyConnectedClient() {
        var jwt = mock(com.work.rest.project.murza.service.security.JwtService.class);
        var blacklist = mock(com.work.rest.project.murza.service.BlacklistJwtService.class);
        when(blacklist.isTokenBlacklisted("revoked")).thenReturn(true);
        var headers = StompHeaderAccessor.create(StompCommand.SEND);
        headers.setUser(() -> "member@example.test");
        headers.setDestination("/app/chat.sendMessage");
        headers.setSessionAttributes(java.util.Map.of("accessToken", "revoked", "authenticatedUser", user(1L)));
        headers.setLeaveMutable(true);
        var frame = MessageBuilder.createMessage(new byte[0], headers.getMessageHeaders());
        assertThrows(AccessDeniedException.class, () -> new ChatChannelInterceptor(jwt, blacklist, mock(UserRepository.class)).preSend(frame, null));
        verifyNoInteractions(jwt);
    }

    @Test
    void messageTimeIncludesTheUtcOffsetForEveryClient() throws Exception {
        Message message = Message.builder().timestamp(Instant.parse("2026-10-09T21:00:00Z")).build();
        var mapper = new com.fasterxml.jackson.databind.ObjectMapper()
                .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        assertTrue(mapper.writeValueAsString(message).contains("2026-10-09T21:00:00Z"));
    }

    private void frame(StompCommand command, String destination, boolean authenticated) {
        var headers = StompHeaderAccessor.create(command);
        headers.setDestination(destination);
        if (authenticated) headers.setUser(() -> "member@example.test");
        var jwt = mock(com.work.rest.project.murza.service.security.JwtService.class);
        var blacklist = mock(com.work.rest.project.murza.service.BlacklistJwtService.class);
        User member = user(1L);
        headers.setSessionAttributes(java.util.Map.of("accessToken", "valid", "authenticatedUser", member));
        when(jwt.isTokenValid("valid", member)).thenReturn(true);
        headers.setLeaveMutable(true);
        var users = mock(UserRepository.class);
        when(users.findById(1L)).thenReturn(Optional.of(member));
        new ChatChannelInterceptor(jwt, blacklist, users).preSend(MessageBuilder.createMessage(new byte[0], headers.getMessageHeaders()), null);
    }

    private User user(Long id) { User user = new User(); user.setId(id); return user; }
}
