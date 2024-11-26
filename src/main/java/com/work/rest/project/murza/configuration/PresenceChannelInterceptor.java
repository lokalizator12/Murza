// PresenceChannelInterceptor.java

package com.work.rest.project.murza.configuration;

import com.work.rest.project.murza.entity.User;
import com.work.rest.project.murza.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class PresenceChannelInterceptor implements ChannelInterceptor {

    private final UserRepository userRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String username = accessor.getUser().getName();
            User user = userRepository.findByEmail(username).orElse(null);
            if (user != null) {
                user.setOnline(true);
                userRepository.save(user);
            }
        } else if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
            String username = accessor.getUser().getName();
            User user = userRepository.findByEmail(username).orElse(null);
            if (user != null) {
                user.setOnline(false);
                user.setLastSeen(new Date());
                userRepository.save(user);
            }
        }

        return message;
    }
}
