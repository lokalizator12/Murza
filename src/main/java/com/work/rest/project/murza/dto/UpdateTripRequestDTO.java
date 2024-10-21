package com.work.rest.project.murza.dto;

import com.work.rest.project.murza.entity.Requests.TripRequest;
import lombok.Data;

import java.util.List;

@Data
public class UpdateTripRequestDTO {

    private Long tripId;
    private Long departureLocationId;
    private Long destinationLocationId;
    private double maxWeight;
    private double maxVolume;
    private String departureDate;
    private String destinationDate;
    private List<Long> acceptedItemsId;
    private List<Long> declinedItemsId;
    private String description;
    private Long shippingMethodId;
    private List<Long> intermediateCities;

    public TripRequest toEntity(UpdateTripRequestDTO dto){
        TripRequest tripRequest =  new TripRequest();

    }


}
