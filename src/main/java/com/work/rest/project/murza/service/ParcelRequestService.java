package com.work.rest.project.murza.service;

import com.work.rest.project.murza.dto.CreateParcelRequestDTO;
import com.work.rest.project.murza.entity.Requests.ParcelRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ParcelRequestService {
    public ParcelRequest createParcelRequest(CreateParcelRequestDTO parcelRequest, List<MultipartFile> files) throws IOException;


    public List<ParcelRequest> getAllParcelRequests();


    public ParcelRequest getParcelRequestById(Long id);


    public void deleteParcelRequest(Long id);

}
