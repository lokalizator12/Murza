package com.work.rest.project.murza.service;

import com.work.rest.project.murza.dto.auth.*;
import com.work.rest.project.murza.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

public interface AuthenticationService {

    User signUp(RegisterUserDto userDto);

    void logoutJwt(HttpServletRequest request, HttpServletResponse response);

    AuthenticateResponseDto authenticate(LoginUserDto input, HttpServletResponse response);

    AuthenticateResponseDto refreshJwt(HttpServletRequest request, HttpServletResponse response);

    void forgotPassword(@Valid ForgotPasswordRequest request);

    void resetPassword(@Valid ResetPasswordRequest request);
}
