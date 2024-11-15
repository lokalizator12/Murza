package com.work.rest.project.murza.dto.profile;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class UserProfileUpdateDto {
    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private MultipartFile userPhoto;
}