package com.work.rest.project.murza.dto.profile;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserSubscribeDto {

    private Long id;
    private String firstName;
    private String lastName;
}
