package com.work.rest.project.murza.dto.profile;
import jakarta.validation.constraints.*;
import lombok.*;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSettingsUpdateDto {
    @Email @Size(max = 254) @Pattern(regexp = ".*\\S.*", message = "Email must not be blank")
    private String email;
    @Pattern(regexp = "\\+?[0-9]{10,15}", message = "Use an international phone number with 10–15 digits")
    private String phoneNumber;
    @NotBlank(message = "Enter your current password to confirm changes")
    private String currentPassword;
    @Size(min = 8, max = 72, message = "New password must contain 8–72 characters")
    private String newPassword;
    @AssertTrue(message = "New password must not exceed 72 UTF-8 bytes")
    public boolean isPasswordWithinByteLimit() {
        return newPassword == null || newPassword.getBytes(java.nio.charset.StandardCharsets.UTF_8).length <= 72;
    }
}
