package com.work.rest.project.murza.mapper;

import com.work.rest.project.murza.dto.profile.UserProfileDto;
import com.work.rest.project.murza.dto.profile.UserSettingsDto;
import com.work.rest.project.murza.dto.profile.UserSubscribeDto;
import com.work.rest.project.murza.entity.User;

import java.util.Date;

public class UserMapper {

    public static UserProfileDto toUserProfileDto(User user) {
        //TODO AVERAGE RATING, FOLLOWERS COUNT, LAST ACTIVITY
        return UserProfileDto.builder()
                .id(user.getId())
                .userPhoto(user.getUserPhoto())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .verificationStatus(user.isVerificationStatusEmail() || user.isVerificationStatusPhone())
                .averageRating(5f)
                .dateRegistered(user.getRegisteredAt())
                .followersCount(99)
                .lastActivityDate(new Date())
                .build();
    }

    public static UserSubscribeDto toUserSubscribeDto(User user) {
        return UserSubscribeDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .avatar(user.getUserPhoto())
                .build();
    }

    public static UserSettingsDto toUserSettingsDto(User user) {
        return UserSettingsDto.builder()
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .emailVerified(user.isVerificationStatusEmail())
                .phoneVerified(user.isVerificationStatusPhone())
                .build();

    }
}
