package com.work.rest.project.murza.service.impl;

import com.work.rest.project.murza.dto.RegisterUserDto;
import com.work.rest.project.murza.entity.RoleEnum;
import com.work.rest.project.murza.entity.User;
import com.work.rest.project.murza.exception.UserNotFoundException;
import com.work.rest.project.murza.repository.UserRepository;
import com.work.rest.project.murza.service.AuthenticationService;
import com.work.rest.project.murza.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationService authenticationService;

    @Override
    public List<User> getAllUsers() {

        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> updateUserAdmin() {
        return Optional.empty();
    }

    @Override
    public Optional<User> updateProfileUser() {
        return Optional.empty();
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName;

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            currentUserName = userDetails.getUsername();
        } else {
            currentUserName = null;
        }

        if (currentUserName != null) {
            return userRepository.findByEmail(currentUserName)
                    .orElseThrow(() -> new UserNotFoundException(currentUserName));
        }

        throw new RuntimeException("No authenticated user found");
    }

    @Override
    public User createProfileAdmin(RegisterUserDto registerUserDto) {
        log.info("Start creating a new admin profile for email: {}", registerUserDto.getEmail());
        registerUserDto.setRole(RoleEnum.ADMIN);
        registerUserDto.setPassword(passwordEncoder.encode(registerUserDto.getPassword()));
        return authenticationService.signUp(registerUserDto);
    }
}
