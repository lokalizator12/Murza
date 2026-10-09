package com.work.rest.project.murza.dto.request.trip;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.work.rest.project.murza.entity.Requests.IntermediateLocation;
import com.work.rest.project.murza.entity.Requests.ShippingMethod;
import com.work.rest.project.murza.entity.Requests.TripRequest;
import com.work.rest.project.murza.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;
import lombok.Data;

import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class CreateTripRequestDTO {
    @NotBlank @Size(max = 120) private String title;
    @DecimalMin("-90") @DecimalMax("90") private double departureLatitude;
    @DecimalMin("-180") @DecimalMax("180") private double departureLongitude;
    @NotBlank @Size(max = 250) private String departureAddress;
    @DecimalMin("-90") @DecimalMax("90") private double destinationLatitude;
    @DecimalMin("-180") @DecimalMax("180") private double destinationLongitude;
    @NotBlank @Size(max = 250) private String destinationAddress;
    @Positive private double maxWeight;
    @Positive private double maxVolume;
    @NotNull private Date departureDate;
    @NotNull private Date destinationDate;
    @Size(max = 50) private List<@Positive Long> acceptedItemsId;
    @Size(max = 50) private List<@Positive Long> declinedItemsId;
    @NotBlank @Size(max = 3000) private String description;
    @NotNull @Positive private Long shippingMethodId;
    @Size(max = 10) private List<IntermediateLocation> intermediateLocations;
    @Positive private double maxHeight;
    @Positive private double maxWidth;
    @Positive private double maxLength;

    @AssertTrue(message = "Arrival cannot be earlier than departure")
    public boolean isDateOrderValid() {
        return departureDate == null || destinationDate == null || !destinationDate.before(departureDate);
    }

    public TripRequest toEntity(ShippingMethod shippingMethod, User user) {
        TripRequest tripRequest = new TripRequest();
        tripRequest.setDriver(user);
        tripRequest.setTitle(title.trim());
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


