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
import com.work.rest.project.murza.util.filters.TripRequestSpecifications;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
    public Page<UserTripDto> findByDriver(Long userid, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        User driver = userRepository.findById(userid).orElseThrow(() -> new UserNotFoundException(userid.toString()));
        return tripRequestRepository.findAllByDriver(driver, pageable)
                .map(TripMapper::tripRequestToUserTripDto);

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

    // TripRequestServiceImpl.java

    @Override
    public Page<TripRequestMiniSummaryDTO> getAllTripRequestsWithSummary(
            int page, int size,
            String departureAddress, String destinationAddress,
            LocalDate dateFrom, LocalDate dateTo,
            String sortBy, String sortDirection
    ) {
        Sort sort = Sort.by(Sort.Direction.ASC, "departureDate"); // Сортировка по умолчанию
        if (sortBy != null && !sortBy.isEmpty() && sortDirection != null && !sortDirection.isEmpty()) {
            try {
                Sort.Direction direction = Sort.Direction.fromString(sortDirection);
                sort = Sort.by(direction, sortBy);
            } catch (IllegalArgumentException e) {
                log.warn("Invalid sort direction '{}', defaulting to ASC", sortDirection);
                sort = Sort.by(Sort.Direction.ASC, sortBy);
            }
        }

        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<TripRequest> spec = Specification.where(TripRequestSpecifications.isNotRealized());

        if (departureAddress != null && !departureAddress.isEmpty()) {
            spec = spec.and(TripRequestSpecifications.hasDepartureAddress(departureAddress));
        }
        if (destinationAddress != null && !destinationAddress.isEmpty()) {
            spec = spec.and(TripRequestSpecifications.hasDestinationAddress(destinationAddress));
        }
        if (dateFrom != null) {
            spec = spec.and(TripRequestSpecifications.hasDepartureDateAfter(dateFrom));
        }
        if (dateTo != null) {
            spec = spec.and(TripRequestSpecifications.hasDepartureDateBefore(dateTo));
        }

        return tripRequestRepository.findAll(spec, pageable)
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
