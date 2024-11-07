package com.work.rest.project.murza.mapper;

import com.work.rest.project.murza.dto.ParcelRequestMiniSummaryDTO;
import com.work.rest.project.murza.entity.Requests.ParcelRequest;
import com.work.rest.project.murza.entity.User;

import java.util.List;

public class ParcelRequestMiniSummaryMapper {

    public static ParcelRequestMiniSummaryDTO toDto(ParcelRequest parcelRequest) {
        ParcelRequestMiniSummaryDTO dto = new ParcelRequestMiniSummaryDTO();
        dto.setIdParcel(parcelRequest.getIdParcel());
        User sender = parcelRequest.getSender();
        if (sender != null) {
            dto.setSenderId(sender.getId());
        }
        List<String> photos = parcelRequest.getPhotos();
        if (photos != null && !photos.isEmpty()) {
            dto.setPreviewPhoto(photos.getFirst());
        }

        dto.setTitle(parcelRequest.getTitle());
        dto.setSize(parcelRequest.getSize());
        dto.setPrice(parcelRequest.getPrice());
        dto.setPickupDate(parcelRequest.getPickupDate());
        dto.setDeliveryDate(parcelRequest.getDeliveryDate());
        dto.setPickupAddress(parcelRequest.getPickupAddress());
        dto.setDeliveryAddress(parcelRequest.getDeliveryAddress());
        return dto;
    }
}
