package com.work.rest.project.murza.service.impl.settings;

import com.work.rest.project.murza.exception.EmailSendingException;
import com.work.rest.project.murza.service.settings.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender javaMailSender;

    @Value("${app.email.from}")
    private String fromEmail;

    @Override
    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        try {
            javaMailSender.send(message);
            log.info("Email sent to {}", to);
        } catch (Exception ex) {
            log.error("Failed to send email to {}: {}", to, ex.getMessage());
            throw new EmailSendingException(to);
        }
    }

    @Override
    public void sendVerificationEmail(String to, String verificationCode) {
        String subject = "Verify your email";
        String body = String.format(
                "Dear user,\n\nYour verification code is: %s\n\nPlease enter this code to verify your email address.\n\nBest regards,\nYour Application Team",
                verificationCode);
        sendEmail(to, subject, body);
    }
}
