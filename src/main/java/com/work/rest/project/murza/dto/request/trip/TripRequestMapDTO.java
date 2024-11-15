package com.work.rest.project.murza.dto.request.trip;

import com.work.rest.project.murza.entity.Requests.IntermediateLocation;
import com.work.rest.project.murza.entity.Requests.ShippingMethod;
import lombok.Data;

import java.util.List;

@Data
public class TripRequestMapDTO {

    private Long idTrip;
    private String driverFirstName;
    private String driverPhoto;
    private double departureLatitude;
    private double departureLongitude;
    private String departureAddress;
    private double destinationLatitude;
    private double destinationLongitude;
    private String destinationAddress;
    private ShippingMethod shippingMethod;
    private List<IntermediateLocation> intermediateLocations;

}
