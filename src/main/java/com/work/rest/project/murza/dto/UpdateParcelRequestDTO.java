package com.work.rest.project.murza.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class UpdateParcelRequestDTO {

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

}
