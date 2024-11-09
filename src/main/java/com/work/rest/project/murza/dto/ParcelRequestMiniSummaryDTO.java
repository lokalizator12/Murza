package com.work.rest.project.murza.dto;

import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class ParcelRequestMiniSummaryDTO {

    private UUID idParcel;
    private Long senderId;
    private String previewPhoto;
    private String title;
    private String size;
    private double price;
    private Date pickupDate;
    private Date deliveryDate;
    private String pickupAddress;
    private String deliveryAddress;
}
