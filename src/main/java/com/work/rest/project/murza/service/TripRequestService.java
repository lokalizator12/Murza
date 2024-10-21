package com.work.rest.project.murza.service;

import com.work.rest.project.murza.dto.CreateTripRequestDTO;
import com.work.rest.project.murza.dto.UpdateTripRequestDTO;
import com.work.rest.project.murza.entity.Requests.TripRequest;

import java.io.IOException;
import java.util.List;

public interface TripRequestService {
     TripRequest createTripRequest(CreateTripRequestDTO tripRequest) throws IOException;

     List<TripRequest> getAllTripRequests();

     TripRequest getTripRequestById(Long id);

     void deleteTripRequest(Long id);

     void updateTripRequest(UpdateTripRequestDTO dto);
}