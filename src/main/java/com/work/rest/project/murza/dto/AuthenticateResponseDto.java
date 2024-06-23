package com.work.rest.project.murza.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthenticateResponseDto {
    private String token;
    private int expiresIn;
}
