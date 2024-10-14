package com.work.rest.project.murza.service.Utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileService {
    public List<String> saveFiles(List<MultipartFile> files) throws IOException;
}
