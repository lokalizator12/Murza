package com.work.rest.project.murza.service;

import com.work.rest.project.murza.dto.RegisterUserDto;
import com.work.rest.project.murza.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> getAllUsers();

    Optional<User> getUserByEmail(String email);

    Optional<User> updateUserAdmin();

    Optional<User> updateProfileUser();

    User createProfileAdmin(RegisterUserDto registerUserDto);

}
