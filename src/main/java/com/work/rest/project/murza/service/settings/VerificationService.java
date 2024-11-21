package com.work.rest.project.murza.service.settings;

public interface VerificationService {

    public void sendVerificationCode(Long userId, String type);
    public boolean verifyCode(Long userId, String code, String type);
}
