package com.work.rest.project.murza.controller;

import com.work.rest.project.murza.entity.User;
import com.work.rest.project.murza.service.settings.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/verification")
@RequiredArgsConstructor
public class VerificationController {
    private final VerificationService verificationService;

    @PostMapping("/send")
    public ResponseEntity<String> sendVerificationCode(@AuthenticationPrincipal User currentUser,
                                                       @RequestParam String type) {
        verificationService.sendVerificationCode(currentUser.getId(), type);
        return ResponseEntity.ok("Verification code sent");
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyCode(@AuthenticationPrincipal User currentUser,
                                             @RequestParam String code,
                                             @RequestParam String type) {
        verificationService.verifyCode(currentUser.getId(), code, type);
        return ResponseEntity.ok("Verification successful");
    }
}
