package com.work.rest.project.murza.exception;

public class CaptchaVerificationException extends RuntimeException {
    public CaptchaVerificationException(String message) {
        super(message);
    }
}
