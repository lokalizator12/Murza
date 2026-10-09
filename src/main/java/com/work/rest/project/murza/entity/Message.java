// Message.java
package com.work.rest.project.murza.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Document(collection = "messages")
@Builder
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class Message {

    @Id
    private String id;

    private Long senderId;

    private Long receiverId;

    private String conversationId;

    private String content;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant timestamp;

    private MessageStatus status;
}
