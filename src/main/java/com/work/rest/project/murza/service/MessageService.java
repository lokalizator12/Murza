package com.work.rest.project.murza.service;

import com.work.rest.project.murza.entity.Message;
import org.springframework.data.domain.Page;

public interface MessageService {

    Message sendMessage(Long senderId, Long receiverId, String content);

    Page<Message> getMessageHistory(Long userId1, Long userId2, int page, int size);

    void markMessagesAsRead(Long id, Long interlocutorId);
}
