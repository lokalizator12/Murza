package com.work.rest.project.murza.service.request;

import com.work.rest.project.murza.dto.profile.UserTripDto;
import com.work.rest.project.murza.dto.request.trip.CreateTripRequestDTO;
import com.work.rest.project.murza.dto.request.trip.TripRequestMapDTO;
import com.work.rest.project.murza.dto.request.trip.TripRequestMiniSummaryDTO;
import com.work.rest.project.murza.dto.request.trip.UpdateTripRequestDTO;
import com.work.rest.project.murza.entity.Requests.TripRequest;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.util.List;

public interface TripRequestService {
    TripRequest createTripRequest(CreateTripRequestDTO tripRequest) throws IOException;

    List<UserTripDto> findByDriver(Long userId);

    List<TripRequest> getAllTripRequests();

    List<TripRequestMapDTO> getAllTripRequestsForMap();

    Page<TripRequestMiniSummaryDTO> getAllTripRequestsWithSummary(int page, int size);

    TripRequest getTripRequestById(Long id);

    void deleteTripRequest(Long id);

    void updateTripRequest(UpdateTripRequestDTO dto);
}