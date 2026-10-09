package com.work.rest.project.murza.dto.request.parcel;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.work.rest.project.murza.entity.Requests.ParcelRequest;
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
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class CreateParcelRequestDTO {
    @NotBlank @Size(max = 3000) private String description;
    @NotBlank @Size(max = 120) private String title;
    private boolean declaration;
    @Positive private double weight;
    @NotBlank @Size(max = 100) private String size;
    @Positive private double price;
    @NotNull private Date pickupDate;
    @NotNull private Date deliveryDate;
    @DecimalMin("-90") @DecimalMax("90") private double pickupLatitude;
    @DecimalMin("-180") @DecimalMax("180") private double pickupLongitude;
    @NotBlank @Size(max = 250) private String pickupAddress;
    @DecimalMin("-90") @DecimalMax("90") private double deliveryLatitude;
    @DecimalMin("-180") @DecimalMax("180") private double deliveryLongitude;
    @NotBlank @Size(max = 250) private String deliveryAddress;
    @Positive private double volume;
    @Positive private double height;
    @Positive private double width;
    @Positive private double length;
    private Double suggestedPrice;

    @AssertTrue(message = "Arrival cannot be earlier than departure")
    public boolean isDateOrderValid() {
        return pickupDate == null || deliveryDate == null || !deliveryDate.before(pickupDate);
    }

    public ParcelRequest toEntity(User sender) {
        ParcelRequest parcelRequest = new ParcelRequest();
        parcelRequest.setDescription(this.description);
        parcelRequest.setTitle(this.title);
        parcelRequest.setIdParcel(UUID.randomUUID());
        parcelRequest.setDeclaration(this.declaration);
        parcelRequest.setWeight(this.weight);
        parcelRequest.setSize(this.size);
        parcelRequest.setPrice(this.price);
        parcelRequest.setPickupDate(this.pickupDate);
        parcelRequest.setDeliveryDate(this.deliveryDate);
        parcelRequest.setSender(sender);
        parcelRequest.setPickupLatitude(this.pickupLatitude);
        parcelRequest.setVolume(this.volume);
        parcelRequest.setHeight(this.height);
        parcelRequest.setWidth(this.width);
        parcelRequest.setLength(this.length);
        parcelRequest.setSuggestedPrice(this.getSuggestedPrice());
        parcelRequest.setPickupLongitude(this.pickupLongitude);
        parcelRequest.setPickupAddress(this.pickupAddress);
        parcelRequest.setDeliveryLatitude(this.deliveryLatitude);
        parcelRequest.setDeliveryLongitude(this.deliveryLongitude);
        parcelRequest.setDeliveryAddress(this.deliveryAddress);
        return parcelRequest;
    }
}
