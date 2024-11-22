package com.work.rest.project.murza.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class VerificationCodeNotFoundException extends RuntimeException {
    private Map<String, Object> additionalData;

    public VerificationCodeNotFoundException(String message) {
        super(message);
    }

    public VerificationCodeNotFoundException(String message, Map<String, Object> additionalData) {
        super(message);
        this.additionalData = additionalData;
    }

}
