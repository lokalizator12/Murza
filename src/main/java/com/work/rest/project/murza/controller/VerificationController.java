package com.work.rest.project.murza.controller;

import com.work.rest.project.murza.entity.User;
import com.work.rest.project.murza.service.settings.VerificationService;
import com.work.rest.project.murza.service.utils.CaptchaService;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @PostMapping("/send")
    public ResponseEntity<String> sendVerificationCode(@AuthenticationPrincipal User currentUser,
                                                       @RequestParam @NotNull @Pattern(regexp = "email|phone") String type,
                                                       @RequestParam String captchaResponse) {
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

}
