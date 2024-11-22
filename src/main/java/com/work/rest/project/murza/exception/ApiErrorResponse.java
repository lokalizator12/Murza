package com.work.rest.project.murza.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ApiErrorResponse {
    private int status;
    private String message;
    private List<ErrorDetail> errors;
    private Map<String, Object> additionalData;

    public ApiErrorResponse(int status, String message, List<ErrorDetail> errors) {
        this.status = status;
        this.message = message;
        this.errors = errors;
    }

    public ApiErrorResponse(int status, String message, List<ErrorDetail> errors, Map<String, Object> additionalData) {
        this.status = status;
        this.message = message;
        this.errors = errors;
        this.additionalData = additionalData;
    }


    @Data
    @AllArgsConstructor
    public static class ErrorDetail {
        private String field;
        private String message;
    }
}
