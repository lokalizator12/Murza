package com.work.rest.project.murza.service.impl;

import com.work.rest.project.murza.dto.auth.RegisterUserDto;
import com.work.rest.project.murza.dto.profile.UserProfileDto;
import com.work.rest.project.murza.dto.profile.UserProfileUpdateDto;
import com.work.rest.project.murza.dto.profile.UserSettingsDto;
import com.work.rest.project.murza.dto.profile.UserSettingsUpdateDto;
import com.work.rest.project.murza.entity.RoleEnum;
import com.work.rest.project.murza.entity.User;
import com.work.rest.project.murza.exception.CredentialAlreadyExistsException;
import com.work.rest.project.murza.exception.FileServiceException;
import com.work.rest.project.murza.exception.UserNotFoundException;
import com.work.rest.project.murza.mapper.UserMapper;
import com.work.rest.project.murza.repository.UserRepository;
import com.work.rest.project.murza.service.AuthenticationService;
import com.work.rest.project.murza.service.UserService;
import com.work.rest.project.murza.service.utils.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileService fileService;
    private final AuthenticationService authenticationService;

    @Override
    public List<User> getAllUsers() {

        return userRepository.findAll();
    }

    @Override
    @Transactional
    public UserSettingsDto updateUserSettings(Long userId, UserSettingsUpdateDto settingsDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId.toString()));

        if (!passwordEncoder.matches(settingsDto.getCurrentPassword(), user.getPassword())) {
            throw new CredentialAlreadyExistsException("Password uncorrected");
        }

        if (settingsDto.getEmail() != null && !settingsDto.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(settingsDto.getEmail())) {
                throw new CredentialAlreadyExistsException("Email already in use: " + settingsDto.getEmail());
            }
            user.setEmail(settingsDto.getEmail());
        }

        if (settingsDto.getPhoneNumber() != null && !settingsDto.getPhoneNumber().equals(user.getPhoneNumber())) {
            if (userRepository.existsByPhoneNumber(settingsDto.getPhoneNumber())) {
                throw new CredentialAlreadyExistsException("Phone number already in use: " + settingsDto.getPhoneNumber());
            }
            user.setPhoneNumber(settingsDto.getPhoneNumber());
        }

        if (settingsDto.getNewPassword() != null && settingsDto.getNewPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(settingsDto.getNewPassword()));
        }

        user.setVerificationStatus(settingsDto.isVerificationStatus());

        userRepository.save(user);

        return UserMapper.toUserSettingsDto(user);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> updateUserAdmin() {
        return Optional.empty();
    }

    @Override
    public Optional<User> updateProfileUser() {
        return Optional.empty();
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName;

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            currentUserName = userDetails.getUsername();
        } else {
            currentUserName = null;
        }

        if (currentUserName != null) {
            return userRepository.findByEmail(currentUserName)
                    .orElseThrow(() -> new UserNotFoundException(currentUserName));
        }

        throw new UserNotFoundException("No authenticated user found");
    }

    @Override
    public User createProfileAdmin(RegisterUserDto registerUserDto) {
        log.info("Start creating a new admin profile for email: {}", registerUserDto.getEmail());
        registerUserDto.setRole(RoleEnum.ADMIN);
        registerUserDto.setPassword(passwordEncoder.encode(registerUserDto.getPassword()));
        return authenticationService.signUp(registerUserDto);
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileDto getUserProfile(Long id, boolean b) {
        return userRepository.findById(id)
                .map(UserMapper::toUserProfileDto)
                .orElseThrow(() -> new UserNotFoundException(id.toString()));
    }

    @Override
    @Transactional
    public UserProfileDto updateUserProfile(Long id, UserProfileUpdateDto userProfileDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id.toString()));

        user.setFirstName(userProfileDto.getFirstName());
        user.setLastName(userProfileDto.getLastName());

        // Проверяем, передан ли файл для фотографии
        if (userProfileDto.getUserPhoto() != null && !userProfileDto.getUserPhoto().isEmpty()) {
            try {
                user.setUserPhoto(fileService.saveProfilePicture(userProfileDto.getUserPhoto(), id));
            } catch (IOException e) {
                throw new FileServiceException(e.getMessage());
            }
        }

        userRepository.save(user);

        return UserMapper.toUserProfileDto(user);
    }


    @Override
    public String getUserStatus(Long id) {
        return "";
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
    }

    @Override
    @Transactional(readOnly = true)
    public UserSettingsDto getUserSettings(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id.toString()));

        return UserMapper.toUserSettingsDto(user);
    }
}
