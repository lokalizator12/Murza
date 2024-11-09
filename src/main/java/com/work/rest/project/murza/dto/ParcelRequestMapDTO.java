package com.work.rest.project.murza.dto;


import lombok.Data;

import java.util.UUID;

@Data
public class ParcelRequestMapDTO {

    private UUID idParcel;
    private String previewPhoto;
    private String size;
    private String title;
    private double price;
    private double pickupLatitude;
    private double pickupLongitude;
    private double deliveryLatitude;
    private double deliveryLongitude;

}
