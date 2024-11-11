package com.work.rest.project.murza.service;

import com.work.rest.project.murza.dto.CreateTripRequestDTO;
import com.work.rest.project.murza.dto.TripRequestMapDTO;
import com.work.rest.project.murza.dto.TripRequestMiniSummaryDTO;
import com.work.rest.project.murza.dto.UpdateTripRequestDTO;
import com.work.rest.project.murza.entity.Requests.TripRequest;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.util.List;

public interface TripRequestService {
    TripRequest createTripRequest(CreateTripRequestDTO tripRequest) throws IOException;

    List<TripRequest> getAllTripRequests();

    List<TripRequestMapDTO> getAllTripRequestsForMap();

    Page<TripRequestMiniSummaryDTO> getAllTripRequestsWithSummary(int page, int size);

    TripRequest getTripRequestById(Long id);

    void deleteTripRequest(Long id);

    void updateTripRequest(UpdateTripRequestDTO dto);
}