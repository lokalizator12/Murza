package com.work.rest.project.murza.exception;

public class VerificationCodeAlreadyActiveException extends RuntimeException {
    public VerificationCodeAlreadyActiveException(String message) {
        super(message);
    }
}
