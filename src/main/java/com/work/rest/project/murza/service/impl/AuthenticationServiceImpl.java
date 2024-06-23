package com.work.rest.project.murza.service.impl;

import com.work.rest.project.murza.dto.AuthenticateResponseDto;
import com.work.rest.project.murza.dto.LoginUserDto;
import com.work.rest.project.murza.dto.RegisterUserDto;
import com.work.rest.project.murza.entity.RoleEnum;
import com.work.rest.project.murza.entity.User;
import com.work.rest.project.murza.repository.RoleRepository;
import com.work.rest.project.murza.repository.UserRepository;
import com.work.rest.project.murza.service.AuthenticationService;
import com.work.rest.project.murza.service.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;


    @Override
    public User signUp(@Valid RegisterUserDto userDto) {
        log.info("Start registration new user with email: {}", userDto.getEmail());
        Optional<User> existingUserByEmail = userRepository.findByEmail(userDto.getEmail());
        if (existingUserByEmail.isPresent()) {
            log.error("Email is already registered: {}", userDto.getEmail());
            throw new IllegalArgumentException("Email is already registered");
        }
        Optional<User> existingUserByPhoneNumber = userRepository.findByPhoneNumber(userDto.getPhoneNumber());
        if (existingUserByPhoneNumber.isPresent()) {
            log.error("Phone number is already registered: {}", userDto.getPhoneNumber());
            throw new IllegalArgumentException("Phone number is already registered");
        }
        User user = new User();
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        log.info("Password hashed for user: {}", userDto.getEmail());
        user.setEmail(userDto.getEmail());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setPhoneNumber(userDto.getPhoneNumber());
        if (userDto.getRole().equals(RoleEnum.SUPER_ADMIN)) {
            user.setRole(roleRepository.findByName(RoleEnum.SUPER_ADMIN).orElseThrow(() -> {
                log.error("Role not found: {}", RoleEnum.SUPER_ADMIN);
                return new IllegalArgumentException("Role not found");
            }));
        } else if (userDto.getRole().equals(RoleEnum.ADMIN)) {
            user.setRole(roleRepository.findByName(RoleEnum.ADMIN).orElseThrow(() -> {
                log.error("Role not found: {}", RoleEnum.ADMIN);
                return new IllegalArgumentException("Role not found");
            }));
        } else {
            user.setRole(roleRepository.findByName(RoleEnum.USER).orElseThrow(() -> {
                log.error("Role not found: {}", RoleEnum.USER);
                return new IllegalArgumentException("Role not found");
            }));
        }

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with email: {}", userDto.getEmail());
        return savedUser;
    }

    @Override
    public AuthenticateResponseDto authenticate(@Valid LoginUserDto input) {
        log.info("Start authentication");
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );
        log.info("Search profile with email: {}", input.getEmail());
        User newUser = userRepository.findByEmail(input.getEmail()).get();
        String jwtToken = jwtService.generateToken(newUser);
        log.info("Token generated");
        return AuthenticateResponseDto.builder()
                .token(jwtToken)
                .expiresIn(jwtService.getJwtExpirationInMs()).build();
    }
}
