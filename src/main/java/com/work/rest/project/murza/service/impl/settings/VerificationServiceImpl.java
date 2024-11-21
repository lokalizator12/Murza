package com.work.rest.project.murza.service.impl.settings;

import com.work.rest.project.murza.entity.User;
import com.work.rest.project.murza.entity.VerificationCode;
import com.work.rest.project.murza.exception.UserNotFoundException;
import com.work.rest.project.murza.exception.VerificationCodeExpiredException;
import com.work.rest.project.murza.exception.VerificationCodeNotFoundException;
import com.work.rest.project.murza.repository.UserRepository;
import com.work.rest.project.murza.repository.VerificationCodeRepository;
import com.work.rest.project.murza.service.settings.EmailService;
import com.work.rest.project.murza.service.settings.SmsService;
import com.work.rest.project.murza.service.settings.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class VerificationServiceImpl implements VerificationService {

    private final VerificationCodeRepository codeRepository;
    private final UserRepository userRepository;

    private final EmailService emailService;
    private final SmsService smsService;

    @Value("${verification-status.inspiration-code}")
    private int CODE_EXPIRATION_MINUTES;

    @Override
    public void sendVerificationCode(Long userId, String type) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId.toString()));

        String code = String.valueOf((int) (Math.random() * 900000) + 100000);
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setCode(code);
        verificationCode.setType(type);
        verificationCode.setUser(user);
        verificationCode.setExpirationTime(new Date(System.currentTimeMillis() + (long) CODE_EXPIRATION_MINUTES * 60 * 1000));
        codeRepository.save(verificationCode);

        if ("email".equalsIgnoreCase(type)) {
            emailService.sendVerificationEmail(user.getEmail(), code);
        } else if ("phone".equalsIgnoreCase(type)) {
            smsService.sendVerificationSms(user.getPhoneNumber(), code);
        }
    }

    @Override
    public boolean verifyCode(Long userId, String code, String type) {
        VerificationCode verificationCode = codeRepository.findLatestUnverifiedCodeWithinTimeframeNative(userId, type)
                .orElseThrow(() -> new VerificationCodeNotFoundException("No valid code found"));

        if (verificationCode.getExpirationTime().before(new Date())) {
            throw new VerificationCodeExpiredException(code);
        }

        if (!verificationCode.getCode().equals(code)) {
            throw new VerificationCodeNotFoundException("Invalid verification code");
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
        return true;
    }
}
