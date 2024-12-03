package com.work.rest.project.murza.repository;

import com.work.rest.project.murza.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends MongoRepository<Message, String> {

    /*@Query("SELECT m FROM Message m WHERE (m.sender.id = :userId1 AND m.receiver.id = :userId2) " +
            "OR (m.sender.id = :userId2 AND m.receiver.id = :userId1)")
    Page<Message> findMessagesBetweenUsers(@Param("userId1") Long userId1, @Param("userId2") Long userId2, Pageable pageable);

    @Query("SELECT m FROM Message m WHERE m.sender.id = :userId OR m.receiver.id = :userId")
    List<Message> findUserConversations(@Param("userId") Long userId);

    @Query("SELECT m FROM Message m WHERE m.sender.id = :interlocutorId AND m.receiver.id = :currentUserId AND m.status = 'SENT'")
    List<Message> findUnreadMessages(@Param("currentUserId") Long currentUserId, @Param("interlocutorId") Long interlocutorId);
*/
    List<Message> findByConversationIdOrderByTimestampAsc(String conversationId);
}
