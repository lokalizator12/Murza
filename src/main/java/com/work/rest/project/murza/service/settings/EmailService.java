package com.work.rest.project.murza.service.settings;

public interface EmailService {
    void sendEmail(String to, String subject, String body);

    void sendVerificationEmail(String to, String verificationCode);
}
