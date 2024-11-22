package com.work.rest.project.murza.configuration;

import com.twilio.Twilio;
import com.twilio.http.TwilioRestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@Configuration
public class TwilioConfig {

    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Bean
    public TwilioRestClient twilioRestClient() {
        Twilio.init(accountSid, authToken);
        return new TwilioRestClient.Builder(accountSid, authToken).build();
    }
}