package com.work.rest.project.murza.dto;


import lombok.Data;

import java.util.Date;

@Data
public class TripRequestMiniSummaryDTO {

    private Long idTrip;
    private Long driverId;
    private String driverPhoto;
    private String departureAddress;
    private String destinationAddress;
    private Date departureDate;
    private Date destinationDate;

}
