package com.work.rest.project.murza.service.impl;

import com.work.rest.project.murza.service.Utils.FileService;
import com.work.rest.project.murza.util.constants.S3Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    public List<String> saveFiles(List<MultipartFile> files, String prefix, String entityId) throws IOException {
        List<String> fileUrls = new ArrayList<>();
        int i = 0;
        for (MultipartFile file : files) {

            if (!file.isEmpty()) {
                String uniqueFileName = generateUniqueFileName(prefix, i++);

                PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(uniqueFileName)
                        .build();

                s3Client.putObject(putObjectRequest, fromBytes(file.getBytes()));

                String fileUrl = generateFileUrl(uniqueFileName);
                fileUrls.add(fileUrl);
            }
        }

        return fileUrls;
    }

    @Override
    public List<String> saveProfilePictures(List<MultipartFile> files, String userId) throws IOException {
        String prefix = S3Constants.PROFILE_PICTURES_PREFIX + userId + "/";
        return saveFiles(files, prefix, userId);
    }

    @Override
    public List<String> saveParcelPictures(List<MultipartFile> files, String parcelId) throws IOException {
        String prefix = S3Constants.PARCEL_REQUESTS_PREFIX + parcelId + "/";
        return saveFiles(files, prefix, parcelId);
    }

    @Override
    public List<String> saveTripPictures(List<MultipartFile> files, String tripId) throws IOException {
        String prefix = S3Constants.TRIP_REQUESTS_PREFIX + tripId + "/";
        return saveFiles(files, prefix, tripId);
    }

    private String generateUniqueFileName(String prefix, int i) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return prefix + timestamp + "-" + i + ".jpg";
    }

    private String generateFileUrl(String fileName) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, fileName);
    }
}

