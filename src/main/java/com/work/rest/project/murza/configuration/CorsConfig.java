package com.work.rest.project.murza.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Value("${cors.allowed.origins}")
    private List<String> allowedOrigins;

    @Value("${cors.allowed.methods}")
    private List<String> allowedMethods;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        configureApiCors(registry);
        configureWsCors(registry);
    }

    private void configureApiCors(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigins.toArray(new String[0]))
                .allowedMethods(allowedMethods.toArray(new String[0]))
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    private void configureWsCors(CorsRegistry registry) {
        registry.addMapping("/ws/**")
                .allowedOrigins(allowedOrigins.toArray(new String[0]))
                .allowedMethods("GET", "POST")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
