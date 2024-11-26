package com.work.rest.project.murza.service;

public interface KafkaMessageListener {

    void listenGroupChat(String message);
}
