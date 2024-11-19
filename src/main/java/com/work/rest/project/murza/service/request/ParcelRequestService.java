package com.work.rest.project.murza.service.request;

import com.work.rest.project.murza.dto.profile.UserParcelDto;
import com.work.rest.project.murza.dto.request.parcel.CreateParcelRequestDTO;
import com.work.rest.project.murza.dto.request.parcel.ParcelRequestMapDTO;
import com.work.rest.project.murza.dto.request.parcel.ParcelRequestMiniSummaryDTO;
import com.work.rest.project.murza.entity.Requests.ParcelRequest;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface ParcelRequestService {
    public ParcelRequest createParcelRequest(CreateParcelRequestDTO parcelRequest, List<MultipartFile> files) throws IOException;


    public List<ParcelRequest> getAllParcelRequests();

    public List<ParcelRequestMapDTO> getAllParcelRequestsForMap();

    public Page<ParcelRequestMiniSummaryDTO> getAllParcelRequestsWithMiniSummary(int page, int size);

    public ParcelRequest getParcelRequestById(UUID id);

    public Page<UserParcelDto> findBySender(Long sender, int page, int size);

    public void deleteParcelRequest(UUID id);

}
