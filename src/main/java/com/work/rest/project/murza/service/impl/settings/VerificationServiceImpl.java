package com.work.rest.project.murza.service.impl.settings;

import com.work.rest.project.murza.entity.User;
import com.work.rest.project.murza.entity.VerificationCode;
import com.work.rest.project.murza.exception.*;
import com.work.rest.project.murza.repository.UserRepository;
import com.work.rest.project.murza.repository.VerificationCodeRepository;
import com.work.rest.project.murza.service.settings.EmailService;
import com.work.rest.project.murza.service.settings.SmsService;
import com.work.rest.project.murza.service.settings.VerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationServiceImpl implements VerificationService {

    private final VerificationCodeRepository codeRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final SmsService smsService;
    private static final java.security.SecureRandom RANDOM = new java.security.SecureRandom();

    @Value("${verification-status.expiration-code}")
    private int CODE_EXPIRATION_MINUTES;


    @Value("${verification-status.block-duration-minutes}")
    private int BLOCK_DURATION_MINUTES;

    @Value("${frontend.base-url}")
    private String frontendBaseUrl;

    @Override
    @Transactional
    public void sendVerificationCode(Long userId, String type) {
        requireContactType(type);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId.toString()));

        Optional<VerificationCode> latestCodeOpt = codeRepository.findLatestCode(userId, type);
        if (latestCodeOpt.isPresent()) {
            VerificationCode latestCode = latestCodeOpt.get();
            if (latestCode.getBlockedUntil() != null) {
                if (latestCode.getBlockedUntil().after(new Date())) {
                    throw new VerificationBlockedException("");
                } else {
                    latestCode.setInvalidAttempts(0);
                    latestCode.setBlockedUntil(null);
                    codeRepository.save(latestCode);
                }
            }
        }

        Optional<VerificationCode> existingCode = codeRepository.findActiveCode(userId, type);
        if (existingCode.isPresent() && existingCode.get().getCode().matches("[0-9]{6}") && existingCode.get().getExpirationTime().after(new Date())) {
            throw new VerificationCodeAlreadyActiveException("A code has already been sent. Check your inbox or SMS");
        }

        Date tenMinutesAgo = new Date(System.currentTimeMillis() - 10 * 60 * 1000);
        long recentAttempts = codeRepository.countRecentAttempts(userId, type, tenMinutesAgo);
        if (recentAttempts >= 3) {
            throw new VerificationBlockedException("");
        }

        VerificationCode verificationCode = generateVerificationCode(user, type);
        codeRepository.save(verificationCode);

        if ("email".equalsIgnoreCase(type)) {
            emailService.sendVerificationEmail(user.getEmail(), verificationCode.getCode());
        } else if ("phone".equalsIgnoreCase(type)) {
            smsService.sendVerificationSms(user.getPhoneNumber(), verificationCode.getCode());
        }
    }

    @Override
    public VerificationCode generateVerificationCode(User user, String type) {
        String code = String.valueOf(RANDOM.nextInt(900000) + 100000);
        return VerificationCode.builder()
                .code(code)
                .expirationTime(new Date(System.currentTimeMillis() + (long) CODE_EXPIRATION_MINUTES * 60 * 1000))
                .type(type)
                .user(user)
                .build();
    }

    @Transactional
    @Override
    public void sendActivationLink(User user) {
        String token = UUID.randomUUID().toString();

        VerificationCode verificationCode = generateVerificationCode(user, "email");
        verificationCode.setCode(token);
        verificationCode.setExpirationTime(new Date(System.currentTimeMillis() + 60L * 60 * 1000));
        verificationCode.setVerified(false);

        codeRepository.save(verificationCode);

        String activationLink = frontendBaseUrl + "/activate?token=" + token;
        emailService.sendEmail(user.getEmail(), "Activate your MURZA account",
                "Open this link to activate your account: " + activationLink);
    }

    @Override
    public Boolean activateAccount(String token) {
        VerificationCode verificationCode = codeRepository.findByCodeAndType(token, "email")
                .orElseThrow(() -> new VerificationCodeNotFoundException("Invalid or expired activation token"));
        if (verificationCode.getExpirationTime().before(new Date())) {
            log.error("Activation token has expired");
            return false;
        }

        if (verificationCode.isVerified()) {
            log.error("Account is already activated");
            return true;
        }

        User user = verificationCode.getUser();
        user.setVerificationStatusEmail(true);
        userRepository.save(user);

        verificationCode.setVerified(true);
        codeRepository.save(verificationCode);
        return true;
    }


    @Override
    @Transactional(noRollbackFor = {VerificationCodeNotFoundException.class, VerificationBlockedException.class})
    public void verifyCode(Long userId, String code, String type) {
        requireContactType(type);
        VerificationCode verificationCode = codeRepository.findLatestUnverifiedCodeWithinTimeframeNative(userId, type)
                .orElseThrow(() -> new VerificationCodeNotFoundException("No valid code found"));

        if (verificationCode.getBlockedUntil() != null && verificationCode.getBlockedUntil().after(new Date())) {
            throw new VerificationBlockedException("Verification is temporarily blocked");
        }
        if (verificationCode.getExpirationTime().before(new Date())) {
            throw new VerificationCodeExpiredException("The verification code has expired");
        }

        if (!verificationCode.getCode().equals(code)) {
            verificationCode.setInvalidAttempts(verificationCode.getInvalidAttempts() + 1);
            codeRepository.save(verificationCode);

            if (verificationCode.getInvalidAttempts() >= 3) {
                verificationCode.setBlockedUntil(new Date(System.currentTimeMillis() + (long) BLOCK_DURATION_MINUTES * 60 * 1000)); // Block for 10 minutes
                codeRepository.save(verificationCode);
                Map<String, Object> data = new HashMap<>();
                data.put("blockedUntil", verificationCode.getBlockedUntil());
                throw new VerificationBlockedException(data);
            }
            Map<String, Object> data = new HashMap<>();
            data.put("attemptsLeft", 3 - verificationCode.getInvalidAttempts());
            throw new VerificationCodeNotFoundException("Invalid verification code", data);
        }

        verificationCode.setVerified(true);
        codeRepository.save(verificationCode);

        User user = verificationCode.getUser();
        if ("phone".equalsIgnoreCase(type)) {
            user.setVerificationStatusPhone(true);
        } else if ("email".equalsIgnoreCase(type)) {
            user.setVerificationStatusEmail(true);
        }

        userRepository.save(user);
    }

    private void requireContactType(String type) {
        if (!"email".equals(type) && !"phone".equals(type)) throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.BAD_REQUEST, "Choose email or phone verification");
    }

    @Override
    public Map<String, Object> getBlockStatus(Long userId) {
        Map<String, Object> blockStatus = new HashMap<>();

        for (String type : Arrays.asList("email", "phone")) {
            Optional<VerificationCode> latestCodeOpt = codeRepository.findLatestCode(userId, type);
            if (latestCodeOpt.isPresent()) {
                VerificationCode latestCode = latestCodeOpt.get();
                boolean isBlocked = latestCode.getBlockedUntil() != null && latestCode.getBlockedUntil().after(new Date());
                Date blockedUntil = isBlocked ? latestCode.getBlockedUntil() : null;

                Map<String, Object> statusMap = new HashMap<>();
                statusMap.put("isBlocked", isBlocked);
                statusMap.put("blockedUntil", blockedUntil);

                blockStatus.put(type, statusMap);
            } else {
                Map<String, Object> statusMap = new HashMap<>();
                statusMap.put("isBlocked", false);
                statusMap.put("blockedUntil", null);

                blockStatus.put(type, statusMap);
            }
        }

        return blockStatus;
    }

    @Override
    public boolean isCodeValid(String token) {
        return codeRepository.findByCodeAndType(token, "password-reset")
                .filter(t -> !t.isVerified() && t.getExpirationTime().after(new Date()))
                .isPresent();
    }

    @Override
    public void setCodeInvalid(String code) {
        if (isCodeValid(code)) {
            log.info("Code is valid");
            VerificationCode verificationCode = codeRepository.findByCodeAndType(code, "password-reset")
                    .orElseThrow(() -> new VerificationCodeNotFoundException("Invalid verification code" + code));
            verificationCode.setVerified(true);
            codeRepository.save(verificationCode);
        }
    }
}
