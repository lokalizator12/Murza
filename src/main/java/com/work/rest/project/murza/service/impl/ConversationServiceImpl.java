// ConversationServiceImpl.java
package com.work.rest.project.murza.service.impl;

import com.work.rest.project.murza.dto.ConversationDto;
import com.work.rest.project.murza.entity.Conversation;
import com.work.rest.project.murza.entity.Message;
import com.work.rest.project.murza.entity.MessageStatus;
import com.work.rest.project.murza.entity.User;
import com.work.rest.project.murza.repository.ConversationRepository;
import com.work.rest.project.murza.repository.MessageRepository;
import com.work.rest.project.murza.repository.UserRepository;
import com.work.rest.project.murza.service.ConversationService;
import com.work.rest.project.murza.service.EncryptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final EncryptionService encryptionService;

    @Override
    public List<ConversationDto> getUserConversations(String username) {
        User currentUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<Conversation> conversations = conversationRepository.findByUser1IdOrUser2Id(
                currentUser.getId(), currentUser.getId());

        List<ConversationDto> conversationDtos = new ArrayList<>();

        for (Conversation conversation : conversations) {
            String conversationId = conversation.getId();
            log.info(conversationId);
            List<Message> messages = messageRepository.findByConversationIdOrderByTimestampAsc(conversationId);
            log.info(messages.toString());
            if (!messages.isEmpty()) {
                Message lastMessage = messages.get(messages.size() - 1);
                log.info(lastMessage.toString());
                log.info(lastMessage.getContent());
                String decryptedContent = encryptionService.decrypt(lastMessage.getContent());
                lastMessage.setContent(decryptedContent);

                Long interlocutorId = conversation.getUser1Id().equals(currentUser.getId())
                        ? conversation.getUser2Id() : conversation.getUser1Id();

                User interlocutor = userRepository.findById(interlocutorId).orElse(null);
                if (interlocutor != null) {
                    ConversationDto dto = new ConversationDto();
                    dto.setInterlocutorId(interlocutorId);
                    dto.setInterlocutorName(interlocutor.getFirstName() + " " + interlocutor.getLastName());
                    dto.setInterlocutorPhoto(interlocutor.getUserPhoto());
                    dto.setLastMessage(lastMessage.getContent());
                    dto.setLastMessageTimestamp(Timestamp.valueOf(lastMessage.getTimestamp()));

                    int unreadMessages = (int) messages.stream()
                            .filter(msg -> msg.getReceiverId().equals(currentUser.getId()) && msg.getStatus() == MessageStatus.SENT)
                            .count();

                    dto.setUnreadMessages(unreadMessages);
                    conversationDtos.add(dto);
                }
            }
        }

        conversationDtos.sort(Comparator.comparing(ConversationDto::getLastMessageTimestamp).reversed());

        return conversationDtos;
    }
}
