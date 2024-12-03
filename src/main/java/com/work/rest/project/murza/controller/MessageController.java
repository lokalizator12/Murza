// MessageController.java
package com.work.rest.project.murza.controller;

import com.work.rest.project.murza.entity.Message;
import com.work.rest.project.murza.entity.User;
import com.work.rest.project.murza.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/{receiverId}")
    public ResponseEntity<Page<Message>> getMessages(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long receiverId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("get mesagges for: " + receiverId + ", user: " + currentUser);
        Pageable pageable = PageRequest.of(page, size);
        Page<Message> messages = messageService.getMessageHistory(currentUser.getId(), receiverId, pageable);

        return ResponseEntity.ok(messages);
    }

    @PostMapping("/markAsRead/{receiverId}")
    public ResponseEntity<Void> markMessagesAsRead(@AuthenticationPrincipal User currentUser, @PathVariable Long receiverId) {
        log.info("Mark messages as read");
        messageService.markMessagesAsRead(currentUser.getId(), receiverId);
        return ResponseEntity.ok().build();
    }
}
