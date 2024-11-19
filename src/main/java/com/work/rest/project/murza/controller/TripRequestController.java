package com.work.rest.project.murza.controller;

import com.work.rest.project.murza.dto.profile.UserTripDto;
import com.work.rest.project.murza.dto.request.trip.CreateTripRequestDTO;
import com.work.rest.project.murza.dto.request.trip.TripRequestMapDTO;
import com.work.rest.project.murza.dto.request.trip.TripRequestMiniSummaryDTO;
import com.work.rest.project.murza.dto.request.trip.UpdateTripRequestDTO;
import com.work.rest.project.murza.entity.Requests.TripRequest;
import com.work.rest.project.murza.service.request.TripRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/api/trip-requests")
public class TripRequestController {

    private final TripRequestService tripRequestService;

    @PostMapping(value = "/create")
    public ResponseEntity<TripRequest> createTripRequest(@RequestBody CreateTripRequestDTO tripRequestDTO) throws IOException {
        log.info("Creating new trip request");
        TripRequest createdRequest = tripRequestService.createTripRequest(tripRequestDTO);
        log.info("Trip request created with ID: {}", createdRequest.getIdTrip());
        return ResponseEntity.ok(createdRequest);
    }

    @GetMapping
    public ResponseEntity<List<TripRequest>> getAllTripRequests() {
        log.info("Getting trip requests");
        return ResponseEntity.ok(tripRequestService.getAllTripRequests());
    }

    @GetMapping("/list-summary")
    public ResponseEntity<Page<TripRequestMiniSummaryDTO>> getAllTripRequestsWithSummary(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "7") int size
    ) {
        log.info("Getting trip requests with summary");
        return ResponseEntity.ok(tripRequestService.getAllTripRequestsWithSummary(page, size));
    }

    @GetMapping("/list-map")
    public ResponseEntity<List<TripRequestMapDTO>> getAllTripRequestsForMap() {
        log.info("Getting trip requests for map");
        return ResponseEntity.ok(tripRequestService.getAllTripRequestsForMap());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TripRequest> getTripRequestById(@PathVariable Long id) {
        log.info("Getting trip request with id:{}", id);
        return ResponseEntity.ok(tripRequestService.getTripRequestById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<UserTripDto>> getTripRequestByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        log.info("Getting trip requests for user with id:{}", userId);
        return ResponseEntity.ok(tripRequestService.findByDriver(userId, page, size));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTripRequest(@PathVariable Long id) {
        log.info("Deleting trip request with id:{}", id);
        tripRequestService.deleteTripRequest(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/update")
    public ResponseEntity<Void> updateTripRequest(@RequestBody UpdateTripRequestDTO dto) {

        tripRequestService.updateTripRequest(dto);
        return ResponseEntity.noContent().build();
    }
}
