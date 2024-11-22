package com.work.rest.project.murza.service.impl.settings;

import com.twilio.http.TwilioRestClient;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;
import com.work.rest.project.murza.exception.SmsSendingException;
import com.work.rest.project.murza.service.settings.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmsServiceImpl implements SmsService {

    private final TwilioRestClient twilioRestClient;

    @Value("${twilio.phone-number}")
    private String fromPhoneNumber;

    @Value("${sms.verification.message}")
    private String verificationMessageTemplate;

    @Async
    @Override
    public void sendSms(String phoneNumber, String message) {
        validateInput(phoneNumber, message);
        try {
            log.info("Attempting to send SMS to {} with message: {}", phoneNumber, message);
            MessageCreator messageCreator = Message.creator(
                    new PhoneNumber(phoneNumber),
                    new PhoneNumber(fromPhoneNumber),
                    message
            );
            messageCreator.create(twilioRestClient);
            log.info("SMS successfully sent to {} at {}", phoneNumber, System.currentTimeMillis());
        } catch (Exception ex) {
            log.error("Failed to send SMS to {} at {}: {}", phoneNumber, System.currentTimeMillis(), ex.getMessage());
            throw new SmsSendingException(phoneNumber);
        }
    }

    @Override
    public void sendVerificationSms(String phoneNumber, String verificationCode) {
        String message = String.format(verificationMessageTemplate, verificationCode);
        sendSms(phoneNumber, message);
    }

    private void validateInput(String phoneNumber, String message) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number must not be null or empty.");
        }
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Message must not be null or empty.");
        }
    }
}
