package com.work.rest.project.murza.dto.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.work.rest.project.murza.entity.RoleEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.ToString;


@Data
@ToString
public class RegisterUserDto {

    @Email(message = "Email should be valid")
    @Size(max = 254)
    @NotBlank(message = "Email is mandatory")
    private String email;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 8, max = 72, message = "Password must contain 8–72 characters")
    @ToString.Exclude
    private String password;

    @NotBlank(message = "First name is mandatory")
    @Size(max = 50, message = "First name should not exceed 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is mandatory")
    @Size(max = 50, message = "Last name should not exceed 50 characters")
    private String lastName;

    @NotBlank(message = "Phone number is mandatory")
    @Pattern(regexp = "\\+\\d{10,15}", message = "Phone number must start with '+' and contain 10–15 digits")
    private String phoneNumber;

    @jakarta.validation.constraints.AssertTrue(message = "Password must not exceed 72 UTF-8 bytes")
    public boolean isPasswordWithinByteLimit() {
        return password == null || password.getBytes(java.nio.charset.StandardCharsets.UTF_8).length <= 72;
    }

    @JsonIgnore
    private RoleEnum role = RoleEnum.USER;
}
