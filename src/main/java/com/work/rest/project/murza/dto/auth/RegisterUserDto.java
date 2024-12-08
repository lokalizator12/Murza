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
    @NotBlank(message = "Email is mandatory")
    private String email;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 8, message = "Password should be at least 6 characters long")
    private String password;

    @NotBlank(message = "First name is mandatory")
    @Size(max = 50, message = "First name should not exceed 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is mandatory")
    @Size(max = 50, message = "Last name should not exceed 50 characters")
    private String lastName;

    @NotBlank(message = "Phone number is mandatory")
    @Pattern(regexp = "\\+\\d{1,15}", message = "Phone number must start with '+' and contain up to 15 digits")
    private String phoneNumber;

    @JsonIgnore
    private RoleEnum role = RoleEnum.USER;
}
