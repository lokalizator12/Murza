package com.work.rest.project.murza.dto.request.trip;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.work.rest.project.murza.entity.Requests.IntermediateLocation;
import com.work.rest.project.murza.entity.Requests.ShippingMethod;
import com.work.rest.project.murza.entity.Requests.TripRequest;
import com.work.rest.project.murza.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class CreateTripRequestDTO {
    private double departureLatitude;
    private double departureLongitude;
    @NotBlank private String departureAddress;
    private double destinationLatitude;
    private double destinationLongitude;
    @NotBlank private String destinationAddress;
    @Positive private double maxWeight;
    @Positive private double maxVolume;
    @NotNull private Date departureDate;
    @NotNull private Date destinationDate;
    private List<Long> acceptedItemsId;
    private List<Long> declinedItemsId;
    @NotBlank private String description;
    @NotNull private Long shippingMethodId;
    private List<IntermediateLocation> intermediateLocations;
    @Positive private double maxHeight;
    @Positive private double maxWidth;
    @Positive private double maxLength;

    public TripRequest toEntity(ShippingMethod shippingMethod, User user) {
        TripRequest tripRequest = new TripRequest();
        tripRequest.setDriver(user);
        tripRequest.setDepartureLatitude(this.departureLatitude);
        tripRequest.setDepartureLongitude(this.departureLongitude);
        tripRequest.setDepartureAddress(this.departureAddress);
        tripRequest.setDestinationLatitude(this.destinationLatitude);
        tripRequest.setDestinationLongitude(this.destinationLongitude);
        tripRequest.setDestinationAddress(this.destinationAddress);
        tripRequest.setMaxWeight(this.maxWeight);
        tripRequest.setMaxVolume(this.maxVolume);
        tripRequest.setMaxHeight(this.maxHeight);
        tripRequest.setMaxWidth(this.maxWidth);
        tripRequest.setMaxLength(this.maxLength);
        tripRequest.setDepartureDate(this.departureDate);
        tripRequest.setDestinationDate(this.destinationDate);
        tripRequest.setDescription(this.description);
        tripRequest.setShippingMethod(shippingMethod);
        tripRequest.setRealized(false);
        return tripRequest;
    }
}


