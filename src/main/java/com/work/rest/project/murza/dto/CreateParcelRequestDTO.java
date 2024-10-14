package com.work.rest.project.murza.dto;


import com.work.rest.project.murza.entity.Requests.City;
import com.work.rest.project.murza.entity.Requests.ParcelRequest;
import com.work.rest.project.murza.entity.User;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class CreateParcelRequestDTO {

    private String description;
    private boolean declaration;
    private double weight;
    private String size;
    private double price;
    private Date pickupDate;
    private Date deliveryDate;
    private Long pickupLocationId;
    private Long deliveryLocationId;
    private List<String> photos;


    public ParcelRequest toEntity(City pickupLocation, City deliveryLocation, User sender) {
        ParcelRequest parcelRequest = new ParcelRequest();
        parcelRequest.setDescription(this.description);
        parcelRequest.setDeclaration(this.declaration);
        parcelRequest.setWeight(this.weight);
        parcelRequest.setSize(this.size);
        parcelRequest.setPrice(this.price);
        parcelRequest.setPickupDate(this.pickupDate);
        parcelRequest.setDeliveryDate(this.deliveryDate);
        parcelRequest.setPickupLocation(pickupLocation);
        parcelRequest.setDeliveryLocation(deliveryLocation);
        parcelRequest.setSender(sender);
        return parcelRequest;
    }
}
