package com.work.rest.project.murza.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class ConversationDto {
    private Long interlocutorId;
    private String interlocutorName;
    private String interlocutorPhoto;
    private String lastMessage;
    private Timestamp lastMessageTimestamp;
    private int unreadMessages;
}
