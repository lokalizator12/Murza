package com.work.rest.project.murza.service.impl;

import com.work.rest.project.murza.entity.Message;
import com.work.rest.project.murza.entity.MessageStatus;
import com.work.rest.project.murza.entity.User;
import com.work.rest.project.murza.exception.UserNotFoundException;
import com.work.rest.project.murza.repository.MessageRepository;
import com.work.rest.project.murza.repository.UserRepository;
import com.work.rest.project.murza.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private static final String TOPIC = "chat_messages";
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public Message sendMessage(Long senderId, Long receiverId, String content) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new UserNotFoundException(senderId.toString()));

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new UserNotFoundException(receiverId.toString()));

        Message message = Message.builder()
                .sender(sender)
                .receiver(receiver)
                .content(content)
                .status(MessageStatus.SENT)
                .timestamp(LocalDateTime.now())
                .build();

        messageRepository.save(message);

        kafkaTemplate.send(TOPIC, String.valueOf(senderId), content);

        return message;
    }
}
