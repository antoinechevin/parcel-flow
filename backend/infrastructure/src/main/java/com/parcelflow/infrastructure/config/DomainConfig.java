package com.parcelflow.infrastructure.config;

import com.parcelflow.application.service.ParcelService;
import com.parcelflow.domain.port.ParcelRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainConfig {

    @Bean
    public ParcelService parcelService(ParcelRepository parcelRepository) {
        return new ParcelService(parcelRepository);
    }
}
