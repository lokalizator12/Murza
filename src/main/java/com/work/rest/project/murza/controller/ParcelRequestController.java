package com.work.rest.project.murza.controller;


import com.work.rest.project.murza.dto.profile.UserParcelDto;
import com.work.rest.project.murza.dto.request.parcel.CreateParcelRequestDTO;
import com.work.rest.project.murza.dto.request.parcel.ParcelRequestMapDTO;
import com.work.rest.project.murza.dto.request.parcel.ParcelRequestMiniSummaryDTO;
import com.work.rest.project.murza.entity.Requests.ParcelRequest;
import com.work.rest.project.murza.service.request.ParcelRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/parcel-requests")
public class ParcelRequestController {

    private final ParcelRequestService parcelRequestService;


    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ParcelRequest> createParcelRequest(
            @RequestPart("parcelRequest") CreateParcelRequestDTO parcelRequestDTO,
            @RequestPart(name = "files", required = false) List<MultipartFile> files) throws IOException {
        log.info("Creating new parcel request");
        ParcelRequest createdRequest = parcelRequestService.createParcelRequest(parcelRequestDTO, files);
        log.info("Parcel request created with ID: {}", createdRequest.getIdParcel());
        return ResponseEntity.ok(createdRequest);
    }


    @GetMapping
    public ResponseEntity<List<ParcelRequest>> getAllParcelRequests() {
        log.info("Get all parcels");
        return ResponseEntity.ok(parcelRequestService.getAllParcelRequests());
    }

    @GetMapping("/list-map")
    public ResponseEntity<List<ParcelRequestMapDTO>> getAllParcelMapRequests() {
        log.info("Get all parcels for map");
        return ResponseEntity.ok(parcelRequestService.getAllParcelRequestsForMap());
    }

    @GetMapping("/list-summary")
    public ResponseEntity<Page<ParcelRequestMiniSummaryDTO>> getAllParcelRequestsSummary(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "7") int size,
            @RequestParam(required = false) String pickupAddress,
            @RequestParam(required = false) String deliveryAddress,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(required = false) Double priceMin,
            @RequestParam(required = false) Double priceMax,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection
    ) {
        log.info("Get all parcels summary with filters and sorting");
        Page<ParcelRequestMiniSummaryDTO> result = parcelRequestService.getAllParcelRequestsWithMiniSummary(
                page, size, pickupAddress, deliveryAddress, dateFrom, dateTo, priceMin, priceMax, sortBy, sortDirection);
        return ResponseEntity.ok(result);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ParcelRequest> getParcelRequestById(@PathVariable UUID id) {
        log.info("Get parcel with id: {}", id);
        return ResponseEntity.ok(parcelRequestService.getParcelRequestById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<UserParcelDto>> getParcelRequestsByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        log.info("Get parcels for user with id: {}", userId);
        return ResponseEntity.ok(parcelRequestService.findBySender(userId, page, size));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParcelRequest(@PathVariable UUID id) {
        log.info("Delete parcel with id: {}", id);
        parcelRequestService.deleteParcelRequest(id);
        log.info("Deleted");
        return ResponseEntity.noContent().build();
    }
}

