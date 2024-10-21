package com.work.rest.project.murza.exception;

public class TripRequestNotFoundException extends RuntimeException {
    public TripRequestNotFoundException(String message) {
        super(message);
    }
}
