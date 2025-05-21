package com.example.demo.config;

import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class WebMVCConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedMethods("*")
                .exposedHeaders(HttpHeaders.LOCATION)
                .allowedHeaders("*")
                .allowCredentials(true)
                .allowedOriginPatterns("*")
                .maxAge(3600);
    }
}
