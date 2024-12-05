package com.work.rest.project.murza.service.impl;

import com.work.rest.project.murza.exception.CaptchaVerificationException;
import com.work.rest.project.murza.service.utils.CaptchaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
public class CaptchaServiceImpl implements CaptchaService {
    @Value("${recaptcha.secret}")
    private String recaptchaSecret;

    @Value("${recaptcha.verify.url}")
    private String recaptchaVerifyUrl;

    @Override
    public boolean validateCaptcha(String captchaResponse) {
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("secret", recaptchaSecret);
        params.add("response", captchaResponse);
        log.info(captchaResponse);
        ResponseEntity<Map> response = restTemplate.postForEntity(recaptchaVerifyUrl, params, Map.class);
        Map<String, Object> body = response.getBody();
        if (body == null || !(Boolean) body.get("success")) {
            throw new CaptchaVerificationException("Invalid Captcha");
        }
        Double score = (Double) body.get("score");
        if (score == null || score < 0.5) {
            throw new CaptchaVerificationException("Suspicious activity detected (low score)");
        }

        return true;
    }
}
