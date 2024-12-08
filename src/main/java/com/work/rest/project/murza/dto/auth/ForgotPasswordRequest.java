package com.work.rest.project.murza.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ForgotPasswordRequest {
    @NotEmpty
    @Email
    private String email;
}