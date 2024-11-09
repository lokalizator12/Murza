package com.work.rest.project.murza.mapper;

import com.work.rest.project.murza.dto.TripRequestMapDTO;
import com.work.rest.project.murza.dto.TripRequestMiniSummaryDTO;
import com.work.rest.project.murza.entity.Requests.TripRequest;

public class TripMapper {

    public static TripRequestMiniSummaryDTO tripRequestToTripRequestMiniSummaryDto(TripRequest tripRequest) {
        TripRequestMiniSummaryDTO dto = new TripRequestMiniSummaryDTO();

        dto.setIdTrip(tripRequest.getIdTrip());

        if (tripRequest.getDriver() != null) {
            dto.setDriverId(tripRequest.getDriver().getId());
            dto.setDriverPhoto(tripRequest.getDriver().getUserPhoto());
        }

        dto.setDepartureAddress(tripRequest.getDepartureAddress());
        dto.setDestinationAddress(tripRequest.getDestinationAddress());
        dto.setDepartureDate(tripRequest.getDepartureDate());
        dto.setDestinationDate(tripRequest.getDestinationDate());


        return dto;
    }


    public static TripRequestMapDTO tripRequestToTripMapDto(TripRequest tripRequest) {
        TripRequestMapDTO dto = new TripRequestMapDTO();
        dto.setIdTrip(tripRequest.getIdTrip());
        if (tripRequest.getDriver() != null) {
            dto.setDriverPhoto(tripRequest.getDriver().getUserPhoto());
            dto.setDriverFirstName(tripRequest.getDriver().getFirstName());
        }

        dto.setDepartureAddress(tripRequest.getDepartureAddress());
        dto.setDestinationAddress(tripRequest.getDestinationAddress());
        dto.setDepartureLatitude(tripRequest.getDepartureLatitude());
        dto.setDepartureLongitude(tripRequest.getDepartureLongitude());
        dto.setDestinationLatitude(tripRequest.getDestinationLatitude());
        dto.setDestinationLongitude(tripRequest.getDestinationLongitude());
        dto.setShippingMethod(tripRequest.getShippingMethod());
        dto.setIntermediateLocations(tripRequest.getIntermediateLocations());

        return dto;
    }
}
