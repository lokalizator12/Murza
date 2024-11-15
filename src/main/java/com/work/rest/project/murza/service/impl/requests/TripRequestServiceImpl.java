package com.work.rest.project.murza.service.impl.requests;

import com.work.rest.project.murza.dto.profile.UserTripDto;
import com.work.rest.project.murza.dto.request.trip.CreateTripRequestDTO;
import com.work.rest.project.murza.dto.request.trip.TripRequestMapDTO;
import com.work.rest.project.murza.dto.request.trip.TripRequestMiniSummaryDTO;
import com.work.rest.project.murza.dto.request.trip.UpdateTripRequestDTO;
import com.work.rest.project.murza.entity.ItemsDelivery;
import com.work.rest.project.murza.entity.Requests.IntermediateLocation;
import com.work.rest.project.murza.entity.Requests.ShippingMethod;
import com.work.rest.project.murza.entity.Requests.TripRequest;
import com.work.rest.project.murza.entity.User;
import com.work.rest.project.murza.exception.ShippingMethodNotFoundException;
import com.work.rest.project.murza.exception.TripRequestNotFoundException;
import com.work.rest.project.murza.exception.UserNotFoundException;
import com.work.rest.project.murza.mapper.TripMapper;
import com.work.rest.project.murza.repository.ItemsDeliveryRepository;
import com.work.rest.project.murza.repository.ShippingMethodRepository;
import com.work.rest.project.murza.repository.TripRequestRepository;
import com.work.rest.project.murza.repository.UserRepository;
import com.work.rest.project.murza.service.UserService;
import com.work.rest.project.murza.service.request.TripRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TripRequestServiceImpl implements TripRequestService {
    private final ShippingMethodRepository shippingMethodRepository;
    private final TripRequestRepository tripRequestRepository;
    private final UserRepository userRepository;
    private final ItemsDeliveryRepository itemsDeliveryRepository;
    private final UserService userService;

    @Override
    public TripRequest createTripRequest(CreateTripRequestDTO tripRequestDTO) {
        log.info("Start service saving trip request");

        User driver = userService.getCurrentUser();
        ShippingMethod shippingMethod = shippingMethodRepository.findById(tripRequestDTO.getShippingMethodId())
                .orElseThrow(() -> new ShippingMethodNotFoundException(tripRequestDTO.getShippingMethodId().toString()));

        TripRequest tripRequest = tripRequestDTO.toEntity(shippingMethod, driver);


        List<IntermediateLocation> intermediateLocations = tripRequestDTO.getIntermediateLocations();
        if (intermediateLocations != null) {
            intermediateLocations.forEach(location -> location.setTripRequest(tripRequest));
        }
        tripRequest.setIntermediateLocations(intermediateLocations);

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
    public List<UserTripDto> findByDriver(Long userid) {
        User driver = userRepository.findById(userid).orElseThrow(() -> new UserNotFoundException(userid.toString()));
        return tripRequestRepository.findAllByDriver(driver)
                .stream()
                .map(TripMapper::tripRequestToUserTripDto)
                .toList();
    }

    @Override
    public List<TripRequest> getAllTripRequests() {
        return tripRequestRepository.findAll();
    }

    @Override
    public List<TripRequestMapDTO> getAllTripRequestsForMap() {
        return tripRequestRepository.findAllByRealized(false)
                .stream()
                .map(TripMapper::tripRequestToTripMapDto)
                .toList();
    }

    @Override
    public Page<TripRequestMiniSummaryDTO> getAllTripRequestsWithSummary(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return tripRequestRepository.findAllByIsRealized(false, pageable)
                .map(TripMapper::tripRequestToTripRequestMiniSummaryDto);
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
