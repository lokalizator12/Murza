package com.work.rest.project.murza.service.impl;

import com.work.rest.project.murza.service.Utils.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static software.amazon.awssdk.core.sync.RequestBody.fromBytes;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final S3Client s3Client;

    @Value("${s3.bucket.name}")
    private String bucketName;
    @Value("${cloud.aws.region}")
    private String region;


    @Override
    public List<String> saveFiles(List<MultipartFile> files) throws IOException {
        List<String> fileUrls = new ArrayList<>();

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String fileName = STR."\{System.currentTimeMillis()}_\{file.getOriginalFilename()}";


                PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(fileName)
                        .build();


                s3Client.putObject(putObjectRequest, fromBytes(file.getBytes()));

                String fileUrl = STR."https://\{bucketName}.s3.\{region}.amazonaws.com/\{fileName}";
                fileUrls.add(fileUrl);
            }
        }

        return fileUrls;
    }
}
