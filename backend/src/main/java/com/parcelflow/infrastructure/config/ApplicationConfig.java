package com.parcelflow.infrastructure.config;

import com.parcelflow.application.usecases.RetrieveDashboardUseCase;
import com.parcelflow.domain.ports.ParcelRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    public RetrieveDashboardUseCase retrieveDashboardUseCase(ParcelRepositoryPort parcelRepositoryPort) {
        return new RetrieveDashboardUseCase(parcelRepositoryPort);
    }
}