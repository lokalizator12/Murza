package com.work.rest.project.murza.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ApiErrorResponse {
    private int status;
    private String message;
    private List<ErrorDetail> errors;

    @Data
    @AllArgsConstructor
    public static class ErrorDetail {
        private String field;
        private String message;
    }
}
