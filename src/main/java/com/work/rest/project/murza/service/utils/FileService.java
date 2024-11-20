package com.work.rest.project.murza.service.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileService {
    List<String> saveFiles(List<MultipartFile> files, String prefix, String entityId) throws IOException;

    String saveProfilePicture(MultipartFile file, Long userId) throws IOException;

    List<String> saveParcelPictures(List<MultipartFile> files, String parcelId) throws IOException;
}
