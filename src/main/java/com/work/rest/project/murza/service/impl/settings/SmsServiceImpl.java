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
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmsServiceImpl implements SmsService {

    private final TwilioRestClient twilioRestClient;

    @Value("${twilio.phone-number}")
    private String fromPhoneNumber;

    @Override
    public void sendSms(String phoneNumber, String message) {
        try {
            MessageCreator messageCreator = Message.creator(
                    new PhoneNumber(phoneNumber),
                    new PhoneNumber(fromPhoneNumber),
                    message
            );
            messageCreator.create();
            log.info("SMS sent to {}", phoneNumber);
        } catch (Exception ex) {
            log.error("Failed to send SMS to {}: {}", phoneNumber, ex.getMessage());
            throw new SmsSendingException(phoneNumber);
        }
    }

    @Override
    public void sendVerificationSms(String phoneNumber, String verificationCode) {
        String message = String.format("Your verification code is: %s", verificationCode);
        sendSms(phoneNumber, message);
    }
}
