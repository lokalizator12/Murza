package com.work.rest.project.murza.dto.request.trip;


import com.work.rest.project.murza.entity.Requests.IntermediateLocation;
import com.work.rest.project.murza.entity.Requests.ShippingMethod;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class TripRequestMiniSummaryDTO {

    private Long idTrip;
    private Long driverId;
    private String driverFirstName;
    private String previewPhoto;
    private String departureAddress;
    private String destinationAddress;
    private Date departureDate;
    private Date destinationDate;
    private double departureLatitude;
    private double departureLongitude;
    private double destinationLatitude;
    private double destinationLongitude;
    private ShippingMethod shippingMethod;
    private List<IntermediateLocation> intermediateLocations;
}
