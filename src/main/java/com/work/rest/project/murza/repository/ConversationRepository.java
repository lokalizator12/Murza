// ConversationRepository.java
package com.work.rest.project.murza.repository;

import com.work.rest.project.murza.entity.Conversation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends MongoRepository<Conversation, String> {

    Optional<Conversation> findByUser1IdAndUser2Id(Long user1Id, Long user2Id);


    default Optional<Conversation> findByUsers(Long user1Id, Long user2Id) {
        return findByUser1IdAndUser2Id(user1Id, user2Id)
                .or(() -> findByUser1IdAndUser2Id(user2Id, user1Id));
    }

    List<Conversation> findByUser1IdOrUser2Id(Long userId1, Long userId2);
}
