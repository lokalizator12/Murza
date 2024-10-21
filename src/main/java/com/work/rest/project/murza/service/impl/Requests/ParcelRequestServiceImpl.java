package com.work.rest.project.murza.service.impl.Requests;

import com.work.rest.project.murza.dto.CreateParcelRequestDTO;
import com.work.rest.project.murza.entity.Requests.City;
import com.work.rest.project.murza.entity.Requests.ParcelRequest;
import com.work.rest.project.murza.entity.User;
import com.work.rest.project.murza.exception.ParcelNotFoundException;
import com.work.rest.project.murza.repository.ParcelRequestRepository;
import com.work.rest.project.murza.service.CityService;
import com.work.rest.project.murza.service.ParcelRequestService;
import com.work.rest.project.murza.service.UserService;
import com.work.rest.project.murza.service.Utils.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParcelRequestServiceImpl implements ParcelRequestService {
    private final ParcelRequestRepository parcelRequestRepository;
    private final FileService fileService;
    private final CityService cityService;
    private final UserService userService;

    @Override
    public ParcelRequest createParcelRequest(CreateParcelRequestDTO parcelRequestDTO, List<MultipartFile> files) throws IOException {
        log.info("Start service saving parcel request");
        City pickupLocation = cityService.getCityById(parcelRequestDTO.getPickupLocationId());
        City deliveryLocation = cityService.getCityById(parcelRequestDTO.getDeliveryLocationId());
        User sender = userService.getCurrentUser();
        ParcelRequest parcelRequest = parcelRequestDTO.toEntity(pickupLocation, deliveryLocation, sender);

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
    public void deleteParcelRequest(UUID id) {
        ParcelRequest parcelRequest = getParcelRequestById(id);
        parcelRequestRepository.delete(parcelRequest);
    }

}
