package com.work.rest.project.murza.mapper;

import com.work.rest.project.murza.dto.profile.UserTripDto;
import com.work.rest.project.murza.dto.request.trip.TripRequestMapDTO;
import com.work.rest.project.murza.dto.request.trip.TripRequestMiniSummaryDTO;
import com.work.rest.project.murza.entity.Requests.TripRequest;

public class TripMapper {

    public static TripRequestMiniSummaryDTO tripRequestToTripRequestMiniSummaryDto(TripRequest tripRequest) {
        TripRequestMiniSummaryDTO dto = new TripRequestMiniSummaryDTO();

        dto.setIdTrip(tripRequest.getIdTrip());

        if (tripRequest.getDriver() != null) {
            dto.setDriverId(tripRequest.getDriver().getId());
            dto.setPreviewPhoto(tripRequest.getDriver().getUserPhoto());
        }

        dto.setDepartureAddress(tripRequest.getDepartureAddress());
        dto.setDestinationAddress(tripRequest.getDestinationAddress());
        dto.setDepartureDate(tripRequest.getDepartureDate());
        dto.setDestinationDate(tripRequest.getDestinationDate());
        dto.setDriverFirstName(tripRequest.getDriver().getFirstName());
        return dto;
    }

    public static UserTripDto tripRequestToUserTripDto(TripRequest tripRequest) {
        return UserTripDto.builder()
                .idTrip(tripRequest.getIdTrip())
                .status(tripRequest.isRealized())
                .origin(tripRequest.getDepartureAddress())
                .createdAt(tripRequest.getCreatedAt())
                .realizedAt(tripRequest.getRealizedAt())
                .destination(tripRequest.getDestinationAddress())
                .departureDate(tripRequest.getDepartureDate())
                .arrivalDate(tripRequest.getDestinationDate())
                .build();
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
