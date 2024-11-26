package com.work.rest.project.murza.service.impl;

import com.work.rest.project.murza.dto.ConversationDto;
import com.work.rest.project.murza.entity.Message;
import com.work.rest.project.murza.entity.MessageStatus;
import com.work.rest.project.murza.entity.User;
import com.work.rest.project.murza.repository.MessageRepository;
import com.work.rest.project.murza.repository.UserRepository;
import com.work.rest.project.murza.service.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// ConversationServiceImpl.java
@Service
@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Override
    public List<ConversationDto> getUserConversations(String username) {
        User currentUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Fetch conversations where the user is either the sender or receiver
        List<Message> messages = messageRepository.findUserConversations(currentUser.getId());

        // Group messages by interlocutor and get the last message in each conversation
        Map<Long, List<Message>> conversationsMap = messages.stream()
                .collect(Collectors.groupingBy(msg -> {
                    if (msg.getSender().getId().equals(currentUser.getId())) {
                        return msg.getReceiver().getId();
                    } else {
                        return msg.getSender().getId();
                    }
                }));

        // Create ConversationDto objects
        List<ConversationDto> conversations = new ArrayList<>();
        for (Map.Entry<Long, List<Message>> entry : conversationsMap.entrySet()) {
            Long interlocutorId = entry.getKey();
            List<Message> conversationMessages = entry.getValue();
            Message lastMessage = conversationMessages.stream()
                    .max(Comparator.comparing(Message::getTimestamp))
                    .orElse(null);

            User interlocutor = userRepository.findById(interlocutorId).orElse(null);
            if (interlocutor != null && lastMessage != null) {
                ConversationDto dto = new ConversationDto();
                dto.setInterlocutorId(interlocutor.getId());
                dto.setInterlocutorName(interlocutor.getFirstName() + " " + interlocutor.getLastName());
                dto.setLastMessage(lastMessage.getContent());
                dto.setLastMessageTimestamp(Timestamp.valueOf(lastMessage.getTimestamp()));

                int unreadMessages = (int) conversationMessages.stream()
                        .filter(msg -> msg.getReceiver().getId().equals(currentUser.getId()) && msg.getStatus() == MessageStatus.SENT)
                        .count();
                dto.setInterlocutorPhoto(interlocutor.getUserPhoto());
                dto.setUnreadMessages(unreadMessages);
                conversations.add(dto);
            }
        }

        conversations.sort(Comparator.comparing(ConversationDto::getLastMessageTimestamp).reversed());

        return conversations;
    }
}
