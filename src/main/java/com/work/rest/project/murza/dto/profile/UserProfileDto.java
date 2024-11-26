package com.work.rest.project.murza.dto.profile;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class UserProfileDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String userPhoto;
    private Date lastActivityDate;
    private Date dateRegistered;
    private boolean isOnline;
    private int followersCount;
    private boolean phoneVerified;
    private boolean emailVerified;
    private double averageRating;
}
