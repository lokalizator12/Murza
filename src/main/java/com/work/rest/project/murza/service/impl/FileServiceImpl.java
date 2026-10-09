package com.work.rest.project.murza.service.impl;

import com.work.rest.project.murza.service.utils.FileService;
import com.work.rest.project.murza.util.constants.S3Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import javax.imageio.ImageIO;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import static software.amazon.awssdk.core.sync.RequestBody.fromBytes;

@lombok.extern.slf4j.Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final S3Client s3Client;
    @Value("${s3.bucket.name}") private String bucketName;
    @Value("${cloud.aws.region}") private String region;
    private record Photo(byte[] bytes, String extension, String contentType) {}

    @Override
    public void deleteStoredPhotos(List<String> urls, String prefix) {
        if (urls == null || urls.isEmpty()) return;
        List<String> snapshot = new ArrayList<>(urls);
        Runnable cleanup = () -> {
            for (String url : snapshot) {
                if (url == null) continue;
                try {
                    java.net.URI uri = java.net.URI.create(url);
                    String expectedHost = bucketName + ".s3." + region + ".amazonaws.com";
                    if (!"https".equals(uri.getScheme()) || !expectedHost.equalsIgnoreCase(uri.getHost())) continue;
                    String key = uri.getPath().substring(1);
                    if (!key.startsWith(prefix) || !key.substring(prefix.length()).matches("[A-Za-z0-9-]+\\.(jpg|jpeg|png)")) continue;
                    s3Client.deleteObject(software.amazon.awssdk.services.s3.model.DeleteObjectRequest.builder().bucket(bucketName).key(key).build());
                } catch (RuntimeException exception) {
                    log.warn("A stored photo could not be removed after its database reference was deleted");
                }
            }
        };
        if (org.springframework.transaction.support.TransactionSynchronizationManager.isSynchronizationActive()) {
            org.springframework.transaction.support.TransactionSynchronizationManager.registerSynchronization(
                    new org.springframework.transaction.support.TransactionSynchronization() {
                        @Override public void afterCommit() { cleanup.run(); }
                    });
        } else cleanup.run();
    }

    @Override
    public List<String> saveFiles(List<MultipartFile> files, String prefix, String entityId) throws IOException {
        if (files == null || files.isEmpty()) return List.of();
        if (files.size() > 6) throw invalid("Upload up to six photos");
        // Validate the entire selection before writing any objects to S3.
        List<Photo> photos = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file != null && !file.isEmpty()) photos.add(readPhoto(file));
        }
        List<String> urls = new ArrayList<>();
        for (Photo photo : photos) {
            String key = prefix + UUID.randomUUID() + "." + photo.extension();
            try {
                s3Client.putObject(PutObjectRequest.builder().bucket(bucketName).key(key)
                        .contentType(photo.contentType()).cacheControl("public, max-age=31536000, immutable").build(), fromBytes(photo.bytes()));
            } catch (software.amazon.awssdk.core.exception.SdkException exception) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Photo storage is temporarily unavailable. Please try again.");
            }
            urls.add(String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, key));
        }
        return urls;
    }
    private Photo readPhoto(MultipartFile file) throws IOException {
        if (file.getSize() > 5L * 1024 * 1024) throw invalid("Each photo must be at most 5 MB");
        try (var input = ImageIO.createImageInputStream(file.getInputStream())) {
            if (input == null) throw invalid("Choose a JPG or PNG photo");
            var readers = ImageIO.getImageReaders(input);
            if (!readers.hasNext()) throw invalid("Choose a valid JPG or PNG photo");
            var reader = readers.next();
            try {
                String format = reader.getFormatName().toLowerCase(Locale.ROOT);
                if (!Set.of("jpeg", "jpg", "png").contains(format)) throw invalid("Choose a JPG or PNG photo");
                reader.setInput(input, true, true);
                int width = reader.getWidth(0), height = reader.getHeight(0);
                if (width <= 0 || height <= 0 || (long) width * height > 20_000_000) throw invalid("Photo dimensions are too large (maximum 20 megapixels)");
                BufferedImage source = reader.read(0);
                double scale = Math.min(1, 1600.0 / Math.max(width, height));
                boolean png = format.equals("png");
                BufferedImage clean = new BufferedImage(Math.max(1, (int) (width * scale)), Math.max(1, (int) (height * scale)), png ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
                var graphics = clean.createGraphics();
                try { graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC); graphics.drawImage(source, 0, 0, clean.getWidth(), clean.getHeight(), null); }
                finally { graphics.dispose(); source.flush(); }
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                ImageIO.write(clean, png ? "png" : "jpg", bytes); clean.flush();
                return new Photo(bytes.toByteArray(), png ? "png" : "jpg", png ? "image/png" : "image/jpeg");
            } finally { reader.dispose(); }
        } catch (IOException | IllegalArgumentException exception) { throw invalid("The photo could not be read. Choose a valid JPG or PNG image."); }
    }
    private ResponseStatusException invalid(String message) { return new ResponseStatusException(HttpStatus.BAD_REQUEST, message); }
    @Override public String saveProfilePicture(MultipartFile file, Long userId) throws IOException {
        List<String> urls = saveFiles(Collections.singletonList(file), S3Constants.PROFILE_PICTURES_PREFIX + userId + "/", userId.toString());
        if (urls.isEmpty()) throw invalid("Choose a photo to upload");
        return urls.getFirst();
    }
    @Override public List<String> saveParcelPictures(List<MultipartFile> files, String parcelId) throws IOException {
        return saveFiles(files, S3Constants.PARCEL_REQUESTS_PREFIX + parcelId + "/", parcelId);
    }
}
