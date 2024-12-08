package com.work.rest.project.murza.service.settings;

import com.work.rest.project.murza.entity.User;
import com.work.rest.project.murza.entity.VerificationCode;
import jakarta.validation.constraints.NotEmpty;

import java.util.Map;

public interface VerificationService {

    void sendVerificationCode(Long userId, String type);

    VerificationCode generateVerificationCode(User user, String type);

    void sendActivationLink(User user);

    Boolean activateAccount(String token);

    void verifyCode(Long userId, String code, String type);

    Map<String, Object> getBlockStatus(Long id);

    boolean isCodeValid(@NotEmpty String token);

    void setCodeInvalid(String code);
}
