// Conversation.java
package com.work.rest.project.murza.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "conversations")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Conversation {

    @Id
    private String id;

    private Long user1Id;

    private Long user2Id;
}
