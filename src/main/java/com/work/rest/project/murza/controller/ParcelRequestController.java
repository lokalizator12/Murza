package com.work.rest.project.murza.controller;


import com.work.rest.project.murza.dto.CreateParcelRequestDTO;
import com.work.rest.project.murza.entity.Requests.ParcelRequest;
import com.work.rest.project.murza.service.ParcelRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    @GetMapping("/{id}")
    public ResponseEntity<ParcelRequest> getParcelRequestById(@PathVariable UUID id) {
        log.info("Get parcel with id: {}", id);
        return ResponseEntity.ok(parcelRequestService.getParcelRequestById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParcelRequest(@PathVariable UUID id) {
        log.info("Delete parcel with id: {}", id);
        parcelRequestService.deleteParcelRequest(id);
        log.info("Deleted");
        return ResponseEntity.noContent().build();
    }
}

