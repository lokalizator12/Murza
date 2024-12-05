package com.work.rest.project.murza.dto.profile;


import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
@Builder
public class UserParcelDto {
    private UUID idParcel;
    private String title;
    private String origin;
    private String destination;
    private Date departureDate;
    private Date arrivalDate;
    private Boolean status;
    private Date createdAt;
    private Date realizedAt;
}