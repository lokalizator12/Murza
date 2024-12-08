package com.work.rest.project.murza.controller;

import com.work.rest.project.murza.dto.auth.*;
import com.work.rest.project.murza.dto.profile.UserProfileDto;
import com.work.rest.project.murza.entity.User;
import com.work.rest.project.murza.mapper.UserMapper;
import com.work.rest.project.murza.service.AuthenticationService;
import com.work.rest.project.murza.service.UserService;
import com.work.rest.project.murza.service.settings.VerificationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final VerificationService verificationService;

    @PostMapping("/signup")
    private ResponseEntity<User> registration(@Valid @RequestBody RegisterUserDto registerUserDto) {
        log.info("Start registration");
        User registerUser = authenticationService.signUp(registerUserDto);
        log.info("Cancel registration");
        return ResponseEntity.ok(registerUser);
    }

    @PostMapping("/login")
    private ResponseEntity<?> authenticate(@RequestBody LoginUserDto loginUserDto, HttpServletResponse response) {
        log.info("Login attempt for email: {}", loginUserDto.getEmail());
        try {
            AuthenticateResponseDto responseDto = authenticationService.authenticate(loginUserDto, response);
            log.info("Login successful for email: {}", loginUserDto.getEmail());
            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            log.error("Login failed for email: {}: {}", loginUserDto.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed");
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        log.info("Logout initiated");
        try {
            authenticationService.logoutJwt(request, response);
            log.info("Logout successful");
            return ResponseEntity.ok("Logout successful");
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.warn("Logout failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthenticateResponseDto> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        AuthenticateResponseDto authenticateResponseDto = authenticationService.refreshJwt(request, response);
        return (authenticateResponseDto != null) ?
                ResponseEntity.ok(authenticateResponseDto)
                :
                ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileDto> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        UserProfileDto user = userService.getUserByEmail(userDetails.getUsername())
                .map(UserMapper::toUserProfileDto)
                .orElseThrow(() -> new UsernameNotFoundException(userDetails.getUsername()));
        return ResponseEntity.ok(user);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        log.info("Forgot password request: {}", request);
        authenticationService.forgotPassword(request);
        log.info("Forgot password successful");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        log.info("Reset password request: {}", request);
        authenticationService.resetPassword(request);
        log.info("Reset password successful");
        return ResponseEntity.ok("Password successfully reset.");
    }

    @GetMapping("/validate-token")
    public ResponseEntity<String> validateToken(@RequestParam("token") String token) {
        if (verificationService.isCodeValid(token)) {
            return ResponseEntity.ok("Token is valid.");
        } else {
            return ResponseEntity.badRequest().body("Invalid or expired token.");
        }
    }
}
