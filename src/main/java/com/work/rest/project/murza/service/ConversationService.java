package com.work.rest.project.murza.service;

import com.work.rest.project.murza.dto.ConversationDto;

import java.util.List;

public interface ConversationService {
    List<ConversationDto> getUserConversations(String username);
}