package com.work.rest.project.murza.mapper;

import com.work.rest.project.murza.dto.profile.UserParcelDto;
import com.work.rest.project.murza.dto.request.parcel.ParcelRequestMapDTO;
import com.work.rest.project.murza.dto.request.parcel.ParcelRequestMiniSummaryDTO;
import com.work.rest.project.murza.entity.Requests.ParcelRequest;
import com.work.rest.project.murza.entity.User;

import java.util.List;

public class ParcelMapper {

    public static ParcelRequestMiniSummaryDTO parcelRequestToParcelRequestMiniSummaryDto(ParcelRequest parcelRequest) {
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

    public static UserParcelDto parcelRequestToUserParcelDto(ParcelRequest parcelRequest) {
        return UserParcelDto.builder()
                .title(parcelRequest.getTitle())
                .origin(parcelRequest.getPickupAddress())
                .destination(parcelRequest.getDeliveryAddress())
                .arrivalDate(parcelRequest.getPickupDate())
                .departureDate(parcelRequest.getDeliveryDate())
                .status(parcelRequest.isRealized())
                .idParcel(parcelRequest.getIdParcel())
                .build();
    }


    public static ParcelRequestMapDTO parcelRequestToParcelRequestMapDto(ParcelRequest parcelRequest) {
        ParcelRequestMapDTO dto = new ParcelRequestMapDTO();
        dto.setIdParcel(parcelRequest.getIdParcel());

        List<String> photos = parcelRequest.getPhotos();
        if (photos != null && !photos.isEmpty()) {
            dto.setPreviewPhoto(photos.getFirst());
        }

        dto.setTitle(parcelRequest.getTitle());
        dto.setSize(parcelRequest.getSize());
        dto.setPrice(parcelRequest.getPrice());
        dto.setDeliveryLatitude(parcelRequest.getDeliveryLatitude());
        dto.setDeliveryLongitude(parcelRequest.getDeliveryLongitude());
        dto.setPickupLatitude(parcelRequest.getPickupLatitude());
        dto.setPickupLongitude(parcelRequest.getPickupLongitude());
        dto.setDepartureAddress(parcelRequest.getPickupAddress());
        dto.setDestinationAddress(parcelRequest.getDeliveryAddress());
        return dto;
    }

}
