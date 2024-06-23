package com.work.rest.project.murza.service.impl;

import com.work.rest.project.murza.entity.User;
import com.work.rest.project.murza.repository.UserRepository;
import com.work.rest.project.murza.service.UserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
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
            throw new UsernameNotFoundException(STR."User not found with email : \{email}");
        }
        log.info("build user credentials for boot");
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.get().getPhoneNumber())
                .password(user.get().getPassword())
                .authorities(getAuthorities(user.get().getId()))
                .build();
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Long roleId) {
        if (roleId.equals(3L)) {
            return Collections.singletonList(new SimpleGrantedAuthority("ROLE_SUPER_ADMIN"));
        } else if (roleId.equals(2L)) {
            return Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else {
            return Collections.emptyList();
        }
    }
}
