package com.work.rest.project.murza.controller;

import com.work.rest.project.murza.dto.MessageRequest;
import com.work.rest.project.murza.entity.Message;
import com.work.rest.project.murza.entity.User;
import com.work.rest.project.murza.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/send")
    public ResponseEntity<Message> sendMessage(
            @AuthenticationPrincipal User currentUser,
            @RequestBody MessageRequest messageRequest) {

        Long senderId = currentUser.getId();
        Message message = messageService.sendMessage(senderId, messageRequest.getReceiverId(), messageRequest.getContent());
        return ResponseEntity.ok(message);
    }

    @GetMapping("/{receiverId}")
    public ResponseEntity<Page<Message>> getMessages(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long receiverId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<Message> messages = messageService.getMessageHistory(currentUser.getId(), receiverId, page, size);
        return ResponseEntity.ok(messages);
    }


    @PostMapping("/markAsRead/{receiverId}")
    public ResponseEntity<Void> markMessagesAsRead(@AuthenticationPrincipal User currentUser, @PathVariable Long receiverId) {
        log.info("Mark messages as read");
        messageService.markMessagesAsRead(currentUser.getId(), receiverId);
        return ResponseEntity.ok().build();
    }


}
