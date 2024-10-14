package com.work.rest.project.murza.service.impl;

import com.work.rest.project.murza.service.Utils.FileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileServiceImpl implements FileService {

    private final String uploadDir = "/uploads/";

    public List<String> saveFiles(List<MultipartFile> files) throws IOException {
        List<String> filePaths = new ArrayList<>();

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String fileName = STR."\{System.currentTimeMillis()}_\{file.getOriginalFilename()}";
                Path filePath = Paths.get(uploadDir, fileName);
                Files.write(filePath, file.getBytes());
                filePaths.add(filePath.toString());
            }
        }

        return filePaths;
    }
}

