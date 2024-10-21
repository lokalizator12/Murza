package com.work.rest.project.murza.dto;

import com.work.rest.project.murza.entity.Requests.City;
import com.work.rest.project.murza.entity.Requests.ShippingMethod;
import com.work.rest.project.murza.entity.Requests.TripRequest;
import com.work.rest.project.murza.entity.User;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class CreateTripRequestDTO {
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

    public TripRequest toEntity(City pickupLocation, City deliveryLocation, ShippingMethod shippingMethod, User user) {
        TripRequest tripRequest = new TripRequest();
        tripRequest.setDriver(user);
        tripRequest.setDepartureLocation(pickupLocation);
        tripRequest.setDestinationLocation(deliveryLocation);
        tripRequest.setMaxWeight(this.getMaxWeight());
        tripRequest.setMaxVolume(this.getMaxVolume());
        tripRequest.setDepartureDate(Timestamp.valueOf(this.getDepartureDate()));
        tripRequest.setDestinationDate(Timestamp.valueOf(this.getDestinationDate()));
        tripRequest.setDescription(this.getDescription());
        tripRequest.setShippingMethod(shippingMethod);
        tripRequest.setRealized(false);
        return tripRequest;
    }
}



