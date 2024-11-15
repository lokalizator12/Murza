package com.work.rest.project.murza;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;


@EnableCaching
@SpringBootApplication
public class MurzaApplication {

    public static void main(String[] args) {
        SpringApplication.run(MurzaApplication.class, args);
    }

}
