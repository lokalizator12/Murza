package com.work.rest.project.murza.service;

import com.work.rest.project.murza.dto.MessageRequest;
import com.work.rest.project.murza.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.handler.annotation.Payload;

import java.security.Principal;

public interface MessageService {



    public Message sendMessage(Long senderId, Long receiverId, String content);

    public Page<Message> getMessageHistory(Long userId1, Long userId2, Pageable pageable);

    public void markMessagesAsRead(Long userId, Long interlocutorId) ;

    String decryptMessageContent(String content);
}
