package com.work.rest.project.murza.mapper;

import com.work.rest.project.murza.dto.TripRequestMiniSummaryDTO;
import com.work.rest.project.murza.entity.Requests.TripRequest;

public class TripRequestMiniSummaryMapper {

    public static TripRequestMiniSummaryDTO toDto(TripRequest tripRequest) {
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
}
