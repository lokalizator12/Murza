package com.work.rest.project.murza.service.Utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileService {
    public List<String> saveFiles(List<MultipartFile> files, String prefix, String entityId) throws IOException;
    public List<String> saveProfilePictures(List<MultipartFile> files, String userId) throws IOException;
    public List<String> saveParcelPictures(List<MultipartFile> files, String parcelId) throws IOException;
    public List<String> saveTripPictures(List<MultipartFile> files, String tripId) throws IOException;

}
