package com.work.rest.project.murza.service.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileService {
    public List<String> saveFiles(List<MultipartFile> files, String prefix, String entityId) throws IOException;
    public String saveProfilePicture(MultipartFile file, Long userId) throws IOException;
    public List<String> saveParcelPictures(List<MultipartFile> files, String parcelId) throws IOException;
}
