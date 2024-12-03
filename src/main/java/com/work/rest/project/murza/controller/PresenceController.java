package com.work.rest.project.murza.controller;

import com.work.rest.project.murza.dto.PresenceStatus;
import com.work.rest.project.murza.entity.User;
import com.work.rest.project.murza.exception.UserNotFoundException;
import com.work.rest.project.murza.repository.UserRepository;
import com.work.rest.project.murza.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Slf4j
@Controller
@RequiredArgsConstructor
public class PresenceController {

    private final SimpMessagingTemplate messagingTemplate;
    private final UserService userService;
    private final UserRepository userRepository;

    @MessageMapping("/presence/online")
    public void setOnlineStatus(Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new UserNotFoundException(username));
        log.info("User is online: {}", user.getId());
        messagingTemplate.convertAndSend("/topic/presence/" + user.getId(), new PresenceStatus(user.getId().toString(), true));
    }

    @MessageMapping("/presence/offline")
    public void setOfflineStatus(Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new UserNotFoundException(username));
        userService.updateLastSeen(username);
        log.info("User is offline: {}", username);
        messagingTemplate.convertAndSend("/topic/presence/" + user.getId(), new PresenceStatus(username, false));
    }
}
