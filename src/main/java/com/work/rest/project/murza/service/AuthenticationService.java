package com.work.rest.project.murza.service;

import com.work.rest.project.murza.dto.auth.AuthenticateResponseDto;
import com.work.rest.project.murza.dto.auth.LoginUserDto;
import com.work.rest.project.murza.dto.auth.RegisterUserDto;
import com.work.rest.project.murza.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthenticationService {

    User signUp(RegisterUserDto userDto);
    void logoutJwt(HttpServletRequest request, HttpServletResponse response);
    AuthenticateResponseDto authenticate(LoginUserDto input, HttpServletResponse response);
}
