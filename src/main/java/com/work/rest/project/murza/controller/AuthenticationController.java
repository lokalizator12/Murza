package com.work.rest.project.murza.controller;

import com.work.rest.project.murza.dto.AuthenticateResponseDto;
import com.work.rest.project.murza.dto.LoginUserDto;
import com.work.rest.project.murza.dto.RegisterUserDto;
import com.work.rest.project.murza.entity.User;
import com.work.rest.project.murza.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;


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
        log.info("Start logout service");
        authenticationService.logoutJwt(request, response);
        log.info("Logout successful ");
        return ResponseEntity.ok("Token deleted. User is successfully logout.");
    }
}
