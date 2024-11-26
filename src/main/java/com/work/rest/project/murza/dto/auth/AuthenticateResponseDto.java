package com.work.rest.project.murza.dto.auth;

import com.work.rest.project.murza.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthenticateResponseDto {
    private String token;
    private int expiresIn;
    private User user;
    private Long userId;
}
