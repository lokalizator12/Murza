package com.work.rest.project.murza.dto.profile;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserSettingsDto {
    private String email;
    private String phoneNumber;
    private boolean verificationStatus;
}