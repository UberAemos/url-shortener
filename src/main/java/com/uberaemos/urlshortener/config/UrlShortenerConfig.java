package com.uberaemos.urlshortener.config;

import com.uberaemos.urlshortener.service.IdGeneratorService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UrlShortenerConfig {

    @Value("${url-shortener.datacenter-id}")
    private long datacenterId;

    @Value("${url-shortener.machine-id}")
    private long machineId;

    @Bean
    public IdGeneratorService idGeneratorService() {
        return new IdGeneratorService(datacenterId, machineId);
    }
}
