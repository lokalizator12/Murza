package com.work.rest.project.murza.service;

import com.work.rest.project.murza.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MessageService {


    Message sendMessage(Long senderId, Long receiverId, String content);

    Page<Message> getMessageHistory(Long userId1, Long userId2, Pageable pageable);

    void markMessagesAsRead(Long userId, Long interlocutorId);

    String decryptMessageContent(String content);
}
