package com.work.rest.project.murza.service.impl.Requests;

import com.work.rest.project.murza.dto.CreateTripRequestDTO;
import com.work.rest.project.murza.dto.UpdateTripRequestDTO;
import com.work.rest.project.murza.entity.ItemsDelivery;
import com.work.rest.project.murza.entity.Requests.City;
import com.work.rest.project.murza.entity.Requests.ShippingMethod;
import com.work.rest.project.murza.entity.Requests.TripIntermediateCity;
import com.work.rest.project.murza.entity.Requests.TripRequest;
import com.work.rest.project.murza.exception.ShippingMethodNotFoundException;
import com.work.rest.project.murza.exception.TripRequestNotFoundException;
import com.work.rest.project.murza.repository.ItemsDeliveryRepository;
import com.work.rest.project.murza.repository.ShippingMethodRepository;
import com.work.rest.project.murza.repository.TripRequestRepository;
import com.work.rest.project.murza.service.CityService;
import com.work.rest.project.murza.service.TripRequestService;
import com.work.rest.project.murza.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TripRequestServiceImpl implements TripRequestService {
    private final ShippingMethodRepository shippingMethodRepository;
    private final TripRequestRepository tripRequestRepository;
    private final ItemsDeliveryRepository itemsDeliveryRepository;
    private final CityService cityService;
    private final UserService userService;

    @Override
    public TripRequest createTripRequest(CreateTripRequestDTO tripRequestDTO) {

        log.info("Start service saving trip request");
        City pickupLocation = cityService.getCityById(tripRequestDTO.getDepartureLocationId());
        City deliveryLocation = cityService.getCityById(tripRequestDTO.getDestinationLocationId());

        ShippingMethod shippingMethod = shippingMethodRepository.findById(tripRequestDTO.getShippingMethodId())
                .orElseThrow(() -> new ShippingMethodNotFoundException(tripRequestDTO.getShippingMethodId().toString()));

        List<City> cities = cityService.getCitiesByIds(tripRequestDTO.getIntermediateCities());

        TripRequest tripRequest = tripRequestDTO.toEntity(pickupLocation, deliveryLocation, shippingMethod, userService.getCurrentUser());
        List<TripIntermediateCity> tripIntermediateCities = cities.stream()
                .map(city -> {
                    TripIntermediateCity tripIntermediateCity = new TripIntermediateCity();
                    tripIntermediateCity.setCity(city);
                    tripIntermediateCity.setTripRequest(tripRequest);
                    return tripIntermediateCity;
                })
                .toList();
        tripRequest.setIntermediateCities(tripIntermediateCities);
        List<ItemsDelivery> acceptedItems = itemsDeliveryRepository.findAllById(tripRequestDTO.getAcceptedItemsId());
        log.info("Accepted Items: {}", acceptedItems);

        List<ItemsDelivery> declinedItems = itemsDeliveryRepository.findAllById(tripRequestDTO.getDeclinedItemsId());
        log.info("Declined Items: {}", declinedItems);

        tripRequest.setAcceptedItems(acceptedItems);
        tripRequest.setDeclinedItems(declinedItems);
        TripRequest savedTripRequest = tripRequestRepository.save(tripRequest);
        log.info("Trip request created: {}", savedTripRequest);

        return savedTripRequest;
    }

    @Override
    public List<TripRequest> getAllTripRequests() {
        return tripRequestRepository.findAll();
    }

    @Override
    public TripRequest getTripRequestById(Long id) {
        return tripRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Trip request with ID " + id + " not found."));
    }

    @Override
    public void deleteTripRequest(Long id) {
        if (!tripRequestRepository.existsById(id)) {
            throw new IllegalArgumentException("Trip request with ID " + id + " not found.");
        }
        tripRequestRepository.deleteById(id);
        log.info("Trip request with ID {} deleted", id);
    }

    @Override
    public void updateTripRequest(UpdateTripRequestDTO dto) {
        TripRequest existingTripRequest = tripRequestRepository.findById(dto.getTripId())
                .orElseThrow(() -> new TripRequestNotFoundException(dto.getTripId().toString()));
        log.info("Update Trip request with id: {}", dto.getTripId());

        tripRequestRepository.save(existingTripRequest);
    }
}
