package com.work.rest.project.murza.controller;

import com.work.rest.project.murza.dto.MessageRequest;
import com.work.rest.project.murza.entity.Message;
import com.work.rest.project.murza.entity.User;
import com.work.rest.project.murza.repository.UserRepository;
import com.work.rest.project.murza.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;
    private final UserRepository userRepository;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload MessageRequest chatMessage, Principal principal) {
        if (principal == null) {
            log.error("Principal is null");
            return;
        }
        String username = principal.getName();
        log.info("Principal: {}", username);

        // Get sender
        User sender = userRepository.findByEmail(username).orElse(null);
        if (sender == null) {
            log.error("Sender not found in database");
            return;
        }

        // Get receiver
        User receiver = userRepository.findById(chatMessage.getReceiverId()).orElse(null);
        if (receiver == null) {
            log.error("Receiver not found");
            return;
        }

        // Save message
        Message message = messageService.sendMessage(
                sender.getId(),
                receiver.getId(),
                chatMessage.getContent()
        );

        // Send message to the receiver
        messagingTemplate.convertAndSendToUser(
                receiver.getEmail(), // Use receiver's email
                "/queue/messages",
                message
        );

        // Send message to the sender
        messagingTemplate.convertAndSendToUser(
                sender.getEmail(), // Use sender's email
                "/queue/messages",
                message
        );
    }

    @MessageMapping("/chat.readReceipt")
    public void readReceipt(@Payload Long senderId, Principal principal) {
        String username = principal.getName();
        User currentUser = userRepository.findByEmail(username).orElse(null);
        if (currentUser == null) {
            return;
        }

        // Mark messages as read
        messageService.markMessagesAsRead(currentUser.getId(), senderId);

        // Notify the sender that messages have been read
        messagingTemplate.convertAndSendToUser(
                userRepository.findById(senderId).get().getEmail(),
                "/queue/read-receipts",
                currentUser.getId()
        );
    }

    @MessageMapping("/chat.typing")
    public void typing(@Payload Map<String, Long> typingNotification, Principal principal) {
        String username = principal.getName();
        User sender = userRepository.findByEmail(username).orElse(null);
        if (sender == null) {
            return;
        }

        Long receiverId = typingNotification.get("receiverId");
        User receiver = userRepository.findById(receiverId).orElse(null);
        if (receiver == null) {
            return;
        }

        Map<String, Long> notification = new HashMap<>();
        notification.put("senderId", sender.getId());

        messagingTemplate.convertAndSendToUser(
                receiver.getEmail(),
                "/queue/typing",
                notification
        );
    }


}
