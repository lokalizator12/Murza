package com.work.rest.project.murza.service.impl;

import com.work.rest.project.murza.service.KafkaMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class KafkaMessageListenerImpl implements KafkaMessageListener {


    @KafkaListener(topics = "chat_messages", groupId = "chat_group")
    @Override
    public void listenGroupChat(String message) {
        log.info("Listening group chat message: {}", message);
    }
}
