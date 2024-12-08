package com.work.rest.project.murza.service.impl;

import com.work.rest.project.murza.dto.auth.*;
import com.work.rest.project.murza.entity.RoleEnum;
import com.work.rest.project.murza.entity.User;
import com.work.rest.project.murza.entity.VerificationCode;
import com.work.rest.project.murza.exception.UserNotFoundException;
import com.work.rest.project.murza.repository.RoleRepository;
import com.work.rest.project.murza.repository.UserRepository;
import com.work.rest.project.murza.repository.VerificationCodeRepository;
import com.work.rest.project.murza.service.AuthenticationService;
import com.work.rest.project.murza.service.BlacklistJwtService;
import com.work.rest.project.murza.service.UserDetailsService;
import com.work.rest.project.murza.service.security.JwtService;
import com.work.rest.project.murza.service.settings.EmailService;
import com.work.rest.project.murza.service.settings.VerificationService;
import com.work.rest.project.murza.service.utils.TinyUrlService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final VerificationCodeRepository verificationCodeRepository;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final BlacklistJwtService blacklistService;
    private final VerificationService verificationService;
    private final TinyUrlService tinyUrlService;
    private final EmailService emailService;

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
        verificationService.sendActivationLink(savedUser);
        log.info("User registered successfully with email: {}", userDto.getEmail());
        return savedUser;
    }

    @Override
    public void logoutJwt(HttpServletRequest request, HttpServletResponse response) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid or missing Authorization header");
        }

        String jwtToken = authorizationHeader.substring(7);

        if (blacklistService.isTokenBlacklisted(jwtToken)) {
            throw new IllegalStateException("Token is already blacklisted");
        }

        blacklistService.addTokenToBlacklist(jwtToken);

        Cookie refreshCookie = new Cookie("refresh-token", null);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true); //TODO DELETE THIS LINE WHEN PROJECT WAS DEPLOY TO HOST!!!!!! HTTP
        refreshCookie.setPath("/api/auth/refresh");
        refreshCookie.setMaxAge(0);
        response.addCookie(refreshCookie);
    }


    @Override
    public AuthenticateResponseDto authenticate(@Valid LoginUserDto input, HttpServletResponse response) {
        log.info("Start authentication");
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );
        log.info("Search profile with email: {}", input.getEmail());
        User newUser = userRepository.findByEmail(input.getEmail()).get();
        String accessToken = jwtService.generateToken(newUser);
        String refreshToken = jwtService.generateRefreshToken(newUser);
        log.info("Token generated");

        Cookie refreshCookie = new Cookie("refresh-token", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/api/auth/refresh");
        refreshCookie.setMaxAge(jwtService.getRefreshExpirationInMs() / 1000);
        response.addCookie(refreshCookie);

        return AuthenticateResponseDto.builder()
                .token(accessToken)
                .expiresIn(jwtService.getJwtExpirationInMs())
                .userId(newUser.getId())
                .build();
    }

    @Override
    public AuthenticateResponseDto refreshJwt(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refresh-token".equals(cookie.getName())) {
                    String refreshToken = cookie.getValue();
                    String username = jwtService.extractUsername(refreshToken);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    if (jwtService.isRefreshTokenValid(refreshToken, userDetails)) {
                        String newAccessToken = jwtService.generateToken(userDetails);
                        return
                                AuthenticateResponseDto.builder()
                                        .token(newAccessToken)
                                        .expiresIn(jwtService.getJwtExpirationInMs())
                                        .build();

                    }
                }
            }
        }
        return null;
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException(request.getEmail());
        }
        User user = userOptional.get();
        VerificationCode token = verificationService.generateVerificationCode(user, "email");
        String resetLink = String.format("http://localhost:3000/reset-password?token=%s", token.getCode());
        String shortLink = tinyUrlService.shortenUrl(resetLink);
        emailService.sendEmail(user.getEmail(), "Reset password", "Your link for reset password: " + shortLink);
        verificationCodeRepository.save(token);
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        String code = request.getToken();
        if (!verificationService.isCodeValid(code)) {
            log.error("Code is not valid");
        }
        Long userId = verificationCodeRepository.findUserIdByCode(code).orElse(null);
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId.toString()));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        verificationService.setCodeInvalid(code);
    }

}
