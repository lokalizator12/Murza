// ChatController.java
package com.work.rest.project.murza.controller;

import com.work.rest.project.murza.dto.MessageRequest;
import com.work.rest.project.murza.service.ChatDeliveryService;
import jakarta.validation.Valid;
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
    private final ChatDeliveryService delivery;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Valid @Payload MessageRequest request, Principal principal) {
        if (principal == null) return;
        User sender = userRepository.findByEmail(principal.getName()).orElse(null);
        if (sender != null) delivery.send(sender, request.getReceiverId(), request.getContent());
    }

    @MessageMapping("/chat.readReceipt")
    public void readReceipt(@Payload Long senderId, Principal principal) {
        if (principal == null || principal.getName() == null) {
            log.error("Principal is null or unauthenticated");
            return;
        }

        String username = principal.getName();
        User currentUser = userRepository.findByEmail(username).orElse(null);
        if (currentUser == null) {
            log.error("Current user not found for username: {}", username);
            return;
        }

        log.info("Marking messages as read for senderId: {} by userId: {}", senderId, currentUser.getId());
        messageService.markMessagesAsRead(currentUser.getId(), senderId);

        User sender = userRepository.findById(senderId).orElse(null);
        if (sender != null) {
            messagingTemplate.convertAndSendToUser(
                    sender.getEmail(),
                    "/queue/read-receipts",
                    currentUser.getId()
            );
        } else {
            log.error("Sender not found for senderId: {}", senderId);
        }
    }


}
