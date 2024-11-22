package com.work.rest.project.murza.service.settings;

import java.util.Map;

public interface VerificationService {

    void sendVerificationCode(Long userId, String type);

    void verifyCode(Long userId, String code, String type);

    Map<String, Object> getBlockStatus(Long id);

    void verifyCaptcha(String captchaResponse);
}
