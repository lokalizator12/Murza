package com.work.rest.project.murza.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class VerificationBlockedException extends RuntimeException {
    private Map<String, Object> additionalData;

    public VerificationBlockedException(String message) {
        super(message);
    }

    public VerificationBlockedException(Map<String, Object> data) {
        this.additionalData = data;
    }

}
