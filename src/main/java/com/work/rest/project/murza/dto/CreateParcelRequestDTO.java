package com.work.rest.project.murza.dto;


import com.work.rest.project.murza.entity.Requests.ParcelRequest;
import com.work.rest.project.murza.entity.User;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
public class CreateParcelRequestDTO {
    private String description;
    private boolean declaration;
    private double weight;
    private String size;
    private double price;
    private Date pickupDate;
    private Date deliveryDate;
    private double pickupLatitude;
    private double pickupLongitude;
    private String pickupAddress;
    private double deliveryLatitude;
    private double deliveryLongitude;
    private String deliveryAddress;
    private double volume;
    private double height;
    private double width;
    private double length;
    private Double suggestedPrice;

    public ParcelRequest toEntity(User sender) {
        ParcelRequest parcelRequest = new ParcelRequest();
        parcelRequest.setDescription(this.description);
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
