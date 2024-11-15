package com.work.rest.project.murza.dto.profile;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class UserSettingsUpdateDto {
    @Email
    private String email;

    @Pattern(regexp = "\\+?[0-9]{10,15}", message = "Invalid phone number format")
    private String phoneNumber;

    @NotBlank
    private String currentPassword;

    @Size(min = 6, message = "New password must be at least 6 characters long")
    private String newPassword;

    private boolean verificationStatus;
}