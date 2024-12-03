// ChatController.java
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

        log.info("Sender: {}", sender);
        User receiver = userRepository.findById(chatMessage.getReceiverId()).orElse(null);
        if (receiver == null) {
            log.error("Receiver not found");
            return;
        }

        // Save message
        log.info("Receiver: {}", receiver);
        Message message = messageService.sendMessage(
                sender.getId(),
                receiver.getId(),
                chatMessage.getContent()
        );

        // Decrypt content before sending to clients
        String decryptedContent = messageService.decryptMessageContent(message.getContent());
        message.setContent(decryptedContent);

        // Include sender and receiver details
        message.setSenderId(sender.getId());
        message.setReceiverId(receiver.getId());

        // Send message to the receiver
        messagingTemplate.convertAndSendToUser(
                receiver.getEmail(),
                "/queue/messages",
                message
        );

        // Send message to the sender
        messagingTemplate.convertAndSendToUser(
                sender.getEmail(),
                "/queue/messages",
                message
        );
    }

    @MessageMapping("/chat.readReceipt")
    public void readReceipt(@Payload Long senderId, Principal principal) {
        String username = principal.getName();
        User currentUser = userRepository.findByEmail(username).orElse(null);
        if (currentUser == null) {
            log.error("Current user not found");
            return;
        }

        log.info("Marking messages as read for senderId: {} by userId: {}", senderId, currentUser.getId());

        // Помечаем сообщения как прочитанные
        messageService.markMessagesAsRead(currentUser.getId(), senderId);

        // Отправляем уведомление отправителю
        User sender = userRepository.findById(senderId).orElse(null);
        if (sender != null) {
            messagingTemplate.convertAndSendToUser(
                    sender.getEmail(),
                    "/queue/read-receipts",
                    currentUser.getId()
            );
            log.info("Read receipt sent to sender: {}", sender.getEmail());
        } else {
            log.error("Sender not found for senderId: {}", senderId);
        }
    }

}
