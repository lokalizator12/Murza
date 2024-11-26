package com.work.rest.project.murza.service;

import com.work.rest.project.murza.entity.Message;

public interface MessageService {

    public Message sendMessage(Long senderId, Long receiverId, String content);

}
