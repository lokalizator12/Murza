package com.work.rest.project.murza.service.impl;

import com.work.rest.project.murza.entity.User;
import com.work.rest.project.murza.repository.UserRepository;
import com.work.rest.project.murza.service.UserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("load user to spring");
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            log.error("User not found in DB: {}", email);
            throw new UsernameNotFoundException("User not found with email : " + email);
        }
        log.info("build user credentials for boot");
        return user.get();
    }

}
