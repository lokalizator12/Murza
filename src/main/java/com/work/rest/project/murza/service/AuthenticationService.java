package com.work.rest.project.murza.service;

import com.work.rest.project.murza.dto.AuthenticateResponseDto;
import com.work.rest.project.murza.dto.LoginUserDto;
import com.work.rest.project.murza.dto.RegisterUserDto;
import com.work.rest.project.murza.entity.User;

public interface AuthenticationService {

    User signUp(RegisterUserDto userDto);

    AuthenticateResponseDto authenticate(LoginUserDto input);
}
