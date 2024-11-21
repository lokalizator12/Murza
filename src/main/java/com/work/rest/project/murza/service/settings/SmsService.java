package com.work.rest.project.murza.service.settings;

public interface SmsService {
    void sendSms(String phoneNumber, String message);
    void sendVerificationSms(String phoneNumber, String verificationCode);
}
