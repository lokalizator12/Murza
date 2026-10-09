package com.work.rest.project.murza.controller;

import com.work.rest.project.murza.entity.User;
import com.work.rest.project.murza.service.settings.VerificationService;
import com.work.rest.project.murza.service.utils.CaptchaService;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/v1/verification")
@RequiredArgsConstructor
public class VerificationController {

    private final VerificationService verificationService;
    private final CaptchaService captchaService;
    private final org.springframework.data.redis.core.StringRedisTemplate redis;

    public record ContactVerification(String type, String code) {}

    @PostMapping(value = "/send", consumes = "application/json")
    public ResponseEntity<String> sendContactCode(@AuthenticationPrincipal User currentUser, @RequestBody ContactVerification body) {
        requireContactType(body.type());
        String key = "murza:verification-send:" + currentUser.getId() + ":" + body.type();
        if (!Boolean.TRUE.equals(redis.opsForValue().setIfAbsent(key, "1", java.time.Duration.ofMinutes(2)))) {
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Wait two minutes before requesting another code");
        }
        verificationService.sendVerificationCode(currentUser.getId(), body.type());
        return ResponseEntity.ok("Verification code sent");
    }

    @PostMapping(value = "/verify", consumes = "application/json")
    public ResponseEntity<String> verifyContactCode(@AuthenticationPrincipal User currentUser, @RequestBody ContactVerification body) {
        requireContactType(body.type());
        if (body.code() == null || !body.code().matches("[0-9]{6}")) throw new org.springframework.web.server.ResponseStatusException(HttpStatus.BAD_REQUEST, "Enter the six-digit code");
        verificationService.verifyCode(currentUser.getId(), body.code(), body.type());
        return ResponseEntity.ok("Verification successful");
    }

    private void requireContactType(String type) {
        if (!"email".equals(type) && !"phone".equals(type)) throw new org.springframework.web.server.ResponseStatusException(HttpStatus.BAD_REQUEST, "Choose email or phone verification");
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendVerificationCode(@AuthenticationPrincipal User currentUser,
                                                       @RequestParam @NotNull @Pattern(regexp = "email|phone") String type,
                                                       @RequestParam String captchaResponse) {
        requireContactType(type);
        log.info("Start send verification code");
        if (!captchaService.validateCaptcha(captchaResponse)) {
            log.info("Captcha validation failed");
            return ResponseEntity.badRequest().body("Invalid CAPTCHA. Please try again.");
        }
        verificationService.sendVerificationCode(currentUser.getId(), type);
        log.info("End send verification code");
        return ResponseEntity.ok("Verification code sent");
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyCode(@AuthenticationPrincipal User currentUser,
                                             @RequestParam String code,
                                             @RequestParam String type) {
        requireContactType(type);
        if (!code.matches("[0-9]{6}")) throw new org.springframework.web.server.ResponseStatusException(HttpStatus.BAD_REQUEST, "Enter the six-digit code");
        log.info("Start verify code");
        verificationService.verifyCode(currentUser.getId(), code, type);
        log.info("End verify code");
        return ResponseEntity.ok("Verification successful");
    }

    @GetMapping("/block-status")
    public ResponseEntity<Map<String, Object>> getBlockStatus(@AuthenticationPrincipal User currentUser) {
        Map<String, Object> blockStatus = verificationService.getBlockStatus(currentUser.getId());
        return ResponseEntity.ok(blockStatus);
    }

    @GetMapping("/activate")
    public ResponseEntity<Boolean> activateAccount(@RequestParam("token") String token) {
        log.info("Start activate account");
        return verificationService.activateAccount(token) ?
                ResponseEntity.ok(true) : ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
