package com.work.rest.project.murza.service.impl.requests;

import com.work.rest.project.murza.dto.profile.UserParcelDto;
import com.work.rest.project.murza.dto.request.parcel.CreateParcelRequestDTO;
import com.work.rest.project.murza.dto.request.parcel.ParcelRequestMapDTO;
import com.work.rest.project.murza.dto.request.parcel.ParcelRequestMiniSummaryDTO;
import com.work.rest.project.murza.entity.Requests.ParcelRequest;
import com.work.rest.project.murza.entity.User;
import com.work.rest.project.murza.exception.*;
import com.work.rest.project.murza.mapper.ParcelMapper;
import com.work.rest.project.murza.repository.ParcelRequestRepository;
import com.work.rest.project.murza.repository.UserRepository;
import com.work.rest.project.murza.service.UserService;
import com.work.rest.project.murza.service.request.ParcelRequestService;
import com.work.rest.project.murza.service.utils.FileService;
import com.work.rest.project.murza.util.filters.ParcelRequestSpecifications;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParcelRequestServiceImpl implements ParcelRequestService {
    private final ParcelRequestRepository parcelRequestRepository;
    private final UserRepository userRepository;
    private final FileService fileService;
    private final UserService userService;

    @Override
    public List<ParcelRequestMapDTO> getAllParcelRequestsForMap() {
        return parcelRequestRepository.findAllByRealized(false)
                .stream()
                .map(ParcelMapper::parcelRequestToParcelRequestMapDto)
                .toList();
    }


    @Override
    public Page<ParcelRequestMiniSummaryDTO> getAllParcelRequestsWithMiniSummary(
            int page, int size,
            String pickupAddress, String deliveryAddress,
            LocalDate dateFrom, LocalDate dateTo,
            Double priceMin, Double priceMax,
            String sortBy, String sortDirection
    ) {
        Sort sort = Sort.by(Sort.Direction.ASC, "pickupDate"); // Сортировка по умолчанию
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

        Specification<ParcelRequest> spec = Specification.where(ParcelRequestSpecifications.isNotRealized());

        if (pickupAddress != null && !pickupAddress.isEmpty()) {
            spec = spec.and(ParcelRequestSpecifications.hasPickupAddress(pickupAddress));
        }
        if (deliveryAddress != null && !deliveryAddress.isEmpty()) {
            spec = spec.and(ParcelRequestSpecifications.hasDeliveryAddress(deliveryAddress));
        }
        if (dateFrom != null) {
            spec = spec.and(ParcelRequestSpecifications.hasPickupDateAfter(dateFrom));
        }
        if (dateTo != null) {
            spec = spec.and(ParcelRequestSpecifications.hasPickupDateBefore(dateTo));
        }
        if (priceMin != null) {
            spec = spec.and(ParcelRequestSpecifications.hasPriceGreaterThanOrEqual(priceMin));
        }
        if (priceMax != null) {
            spec = spec.and(ParcelRequestSpecifications.hasPriceLessThanOrEqual(priceMax));
        }
        try {
            return parcelRequestRepository.findAll(spec, pageable)
                    .map(ParcelMapper::parcelRequestToParcelRequestMiniSummaryDto);
        } catch (Exception e) {
            log.error("Error fetching parcel requests with summary", e);
            throw e;
        }
    }

    @Override
    public ParcelRequest createParcelRequest(CreateParcelRequestDTO parcelRequestDTO, List<MultipartFile> files) throws IOException {
        log.info("Start service saving parcel request");

        User sender = userService.getCurrentUser();
        ParcelRequest parcelRequest = parcelRequestDTO.toEntity(sender);

        List<String> filePaths = fileService.saveParcelPictures(files, parcelRequest.getIdParcel().toString());
        parcelRequest.setPhotos(filePaths);
        return parcelRequestRepository.save(parcelRequest);
    }

    @Override
    public List<ParcelRequest> getAllParcelRequests() {
        log.info("Start searching all parcel requests");
        return parcelRequestRepository.findAll();
    }

    @Override
    public ParcelRequest getParcelRequestById(UUID id) {
        return parcelRequestRepository.findById(id).orElseThrow(() -> new ParcelNotFoundException(id.toString()));
    }

    @Override
    public Page<UserParcelDto> findBySender(Long id, int page, int size) {
        User sender = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id.toString()));
        Pageable pageable = PageRequest.of(page, size);
        return parcelRequestRepository
                .findAllBySender(sender, pageable)
                .map(ParcelMapper::parcelRequestToUserParcelDto);
    }

    @Override
    public void deleteParcelRequest(UUID id) {
        if (!parcelRequestRepository.existsById(id)) throw new TripRequestNotFoundException(id.toString());
        parcelRequestRepository.deleteById(id);
        log.info("Trip request with ID {} deleted", id);
    }

    @Override
    public void markAsRealized(UUID id) {
        ParcelRequest parcelRequest = parcelRequestRepository.findById(id)
                .orElseThrow(() -> new ParcelRequestNotFoundException(id.toString()));
        if (parcelRequest.isRealized()) {
            throw new ParcelRequestAlreadyRealizedException(id.toString());
        }
        parcelRequest.setRealized(true);
        parcelRequest.setRealizedAt(new Date());
        parcelRequestRepository.save(parcelRequest);
    }
}
