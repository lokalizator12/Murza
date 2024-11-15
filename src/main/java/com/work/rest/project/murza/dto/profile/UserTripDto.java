package com.work.rest.project.murza.dto.profile;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class UserTripDto {
    private Long idTrip;
    private String origin;
    private String destination;
    private Date departureDate;
    private Date arrivalDate;
    private Boolean status;
}