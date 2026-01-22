package com.parcelflow.infrastructure.config;

import com.parcelflow.application.RetrieveDashboardUseCase;
import com.parcelflow.domain.ParcelRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    public RetrieveDashboardUseCase retrieveDashboardUseCase(ParcelRepositoryPort parcelRepositoryPort) {
        return new RetrieveDashboardUseCase(parcelRepositoryPort);
    }
}
