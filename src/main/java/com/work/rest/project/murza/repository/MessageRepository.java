package com.work.rest.project.murza.repository;

import com.work.rest.project.murza.entity.Message;
import com.work.rest.project.murza.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderOrReceiverOrderByTimestampDesc(User sender, User receiver);
}
