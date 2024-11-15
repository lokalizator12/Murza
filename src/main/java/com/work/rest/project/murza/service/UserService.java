package com.work.rest.project.murza.service;

import com.work.rest.project.murza.dto.auth.RegisterUserDto;
import com.work.rest.project.murza.dto.profile.UserProfileDto;
import com.work.rest.project.murza.dto.profile.UserProfileUpdateDto;
import com.work.rest.project.murza.dto.profile.UserSettingsDto;
import com.work.rest.project.murza.dto.profile.UserSettingsUpdateDto;
import com.work.rest.project.murza.entity.User;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> getAllUsers();

    UserSettingsDto updateUserSettings(Long userId, UserSettingsUpdateDto settingsDto);

    Optional<User> getUserByEmail(String email);

    User getCurrentUser();

    Optional<User> updateUserAdmin();

    Optional<User> updateProfileUser();

    User createProfileAdmin(RegisterUserDto registerUserDto);

    UserProfileDto getUserProfile(Long id, boolean b);

    UserProfileDto updateUserProfile(Long id, @Valid UserProfileUpdateDto userProfileDto);

    String getUserStatus(Long id);

    User getUserById(Long userId);
}
