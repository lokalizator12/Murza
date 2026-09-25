package com.work.rest.project.murza.configuration;

import com.work.rest.project.murza.entity.Requests.ShippingMethod;
import com.work.rest.project.murza.repository.ShippingMethodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ShippingMethodInitializer implements ApplicationRunner {
    private final ShippingMethodRepository repository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        for (String name : new String[]{"Walking", "Bicycle", "Car", "Bus", "Train"}) {
            if (repository.findByMethodNameIgnoreCase(name).isEmpty()) {
                ShippingMethod method = new ShippingMethod();
                method.setMethodName(name);
                repository.save(method);
            }
        }
    }
}
