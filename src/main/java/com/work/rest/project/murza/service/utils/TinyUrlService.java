package com.work.rest.project.murza.service.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class TinyUrlService {

    @Value("${tinyurl.api.token}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String shortenUrl(String longUrl) {
        String apiUrl = "https://api.tinyurl.com/create";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("url", longUrl);
        requestBody.put("domain", "tiny.one");

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        Map<String, Object> response = restTemplate.postForObject(apiUrl, request, Map.class);

        if (response != null && response.containsKey("data")) {
            Map<String, Object> data = (Map<String, Object>) response.get("data");
            return data.get("tiny_url").toString(); // Возвращаем короткую ссылку
        }

        throw new RuntimeException("Failed to shorten URL");
    }
}
