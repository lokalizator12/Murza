// MessageServiceImpl.java
package com.work.rest.project.murza.service.impl;

import com.work.rest.project.murza.entity.Conversation;
import com.work.rest.project.murza.entity.Message;
import com.work.rest.project.murza.entity.MessageStatus;
import com.work.rest.project.murza.entity.User;
import com.work.rest.project.murza.repository.ConversationRepository;
import com.work.rest.project.murza.repository.MessageRepository;
import com.work.rest.project.murza.repository.UserRepository;
import com.work.rest.project.murza.service.EncryptionService;
import com.work.rest.project.murza.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final EncryptionService encryptionService;

    @Override
    public Message sendMessage(Long senderId, Long receiverId, String content) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid sender ID"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid receiver ID"));

        Conversation conversation = conversationRepository.findByUsers(senderId, receiverId)
                .orElseGet(() -> {
                    Conversation newConversation = Conversation.builder()
                            .user1Id(senderId)
                            .user2Id(receiverId)
                            .build();
                    return conversationRepository.save(newConversation);
                });

        String encryptedContent = encryptionService.encrypt(content);

        Message message = Message.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .conversationId(conversation.getId())
                .content(encryptedContent)
                .timestamp(LocalDateTime.now())
                .status(MessageStatus.SENT)
                .build();

        return messageRepository.save(message);
    }

    @Override
    public Page<Message> getMessageHistory(Long userId1, Long userId2, Pageable pageable) {
        // Ищем разговор между пользователями
        Optional<Conversation> conversationOpt = conversationRepository.findByUsers(userId1, userId2);

        if (conversationOpt.isPresent()) {
            String conversationId = conversationOpt.get().getId();

            // Получаем все сообщения, упорядоченные по времени
            List<Message> messages = messageRepository.findByConversationIdOrderByTimestampAsc(conversationId);

            // Расшифровываем сообщения
            List<Message> decryptedMessages = messages.stream()
                    .map(msg -> {
                        String decryptedContent = encryptionService.decrypt(msg.getContent());
                        msg.setContent(decryptedContent);
                        return msg;
                    })
                    .collect(Collectors.toList());

            int totalMessages = decryptedMessages.size();

            // Рассчитываем start и end индексы для текущей страницы
            int start = (int) pageable.getOffset();
            int end = Math.min(start + pageable.getPageSize(), totalMessages);

            // Проверяем индексы
            if (start > totalMessages) {
                return Page.empty(pageable);
            }

            List<Message> pageContent = decryptedMessages.subList(start, end);

            // Возвращаем страницу
            return new PageImpl<>(pageContent, pageable, totalMessages);
        } else {
            return Page.empty();
        }
    }


    @Override
    public void markMessagesAsRead(Long userId, Long interlocutorId) {
        Optional<Conversation> conversationOpt = conversationRepository.findByUsers(userId, interlocutorId);

        if (conversationOpt.isPresent()) {
            String conversationId = conversationOpt.get().getId();
            List<Message> messages = messageRepository.findByConversationIdOrderByTimestampAsc(conversationId);
            for (Message message : messages) {
                if (message.getReceiverId().equals(userId) && message.getStatus() != MessageStatus.READ) {
                    message.setStatus(MessageStatus.READ);
                    messageRepository.save(message);
                }
            }
        }
    }

    public String decryptMessageContent(String encryptedContent) {
        log.info("encrypted to decr: {}", encryptedContent);
        return encryptionService.decrypt(encryptedContent);
    }
}
