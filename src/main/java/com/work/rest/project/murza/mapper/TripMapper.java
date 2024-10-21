package com.work.rest.project.murza.mapper;

import com.work.rest.project.murza.dto.UpdateTripRequestDTO;
import com.work.rest.project.murza.entity.Requests.City;
import com.work.rest.project.murza.entity.Requests.ShippingMethod;
import com.work.rest.project.murza.entity.Requests.TripRequest;
import com.work.rest.project.murza.exception.ShippingMethodNotFoundException;
import com.work.rest.project.murza.repository.ShippingMethodRepository;
import com.work.rest.project.murza.service.CityService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {CityService.class, ShippingMethodRepository.class})
public interface TripRequestMapper {

    @Mapping(target = "departureLocation", source = "departureLocationId")
    @Mapping(target = "destinationLocation", source = "destinationLocationId")
    @Mapping(target = "shippingMethod", source = "shippingMethodId")
    @Mapping(target = "acceptedItems", source = "acceptedItemsId")
    @Mapping(target = "declinedItems", source = "declinedItemsId")
    TripRequest updateDtoToEntity(UpdateTripRequestDTO dto, @MappingTarget TripRequest tripRequest);

    default City mapCity(Long id) {
        return cityService.getCityById(id);
    }

    default ShippingMethod mapShippingMethod(Long id) {
        return shippingMethodRepository.findById(id)
                .orElseThrow(() -> new ShippingMethodNotFoundException(id.toString()));
    }
}
