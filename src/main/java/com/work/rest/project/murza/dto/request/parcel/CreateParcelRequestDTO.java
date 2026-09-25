package com.work.rest.project.murza.dto.request.parcel;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.work.rest.project.murza.entity.Requests.ParcelRequest;
import com.work.rest.project.murza.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class CreateParcelRequestDTO {
    @NotBlank private String description;
    @NotBlank private String title;
    private boolean declaration;
    @Positive private double weight;
    @NotBlank private String size;
    @Positive private double price;
    @NotNull private Date pickupDate;
    @NotNull private Date deliveryDate;
    private double pickupLatitude;
    private double pickupLongitude;
    @NotBlank private String pickupAddress;
    private double deliveryLatitude;
    private double deliveryLongitude;
    @NotBlank private String deliveryAddress;
    @Positive private double volume;
    @Positive private double height;
    @Positive private double width;
    @Positive private double length;
    private Double suggestedPrice;

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
