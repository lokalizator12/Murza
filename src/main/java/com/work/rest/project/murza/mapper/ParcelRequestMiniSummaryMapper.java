package com.work.rest.project.murza.mapper;


import com.work.rest.project.murza.dto.ParcelRequestMiniSummaryDTO;
import com.work.rest.project.murza.entity.Requests.ParcelRequest;
import com.work.rest.project.murza.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ParcelRequestMiniSummaryMapper {

    ParcelRequestMiniSummaryMapper INSTANCE = Mappers.getMapper(ParcelRequestMiniSummaryMapper.class);

    @Mapping(target = "senderId", source = "parcelRequest.sender", qualifiedByName = "extractSenderId")
    @Mapping(target = "previewPhoto", source = "parcelRequest.photos", qualifiedByName = "firstPhoto")
    ParcelRequestMiniSummaryDTO toDto(ParcelRequest parcelRequest);

    @Named("firstPhoto")
    default String firstPhoto(List<String> photos) {
        return (photos != null && !photos.isEmpty()) ? photos.getFirst() : null;
    }

    @Named("extractSenderId")
    default Long extractSenderId(User sender) {
        return sender != null ? sender.getId() : null;
    }
}
