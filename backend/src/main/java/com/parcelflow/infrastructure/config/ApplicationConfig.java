package com.parcelflow.infrastructure.config;

import com.parcelflow.application.usecases.RetrieveDashboardUseCase;

import com.parcelflow.domain.ports.ParcelRepositoryPort;

import com.parcelflow.domain.service.UrgencyCalculator;

import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;



import java.time.Clock;



@Configuration

public class ApplicationConfig {



    @Bean

    public Clock clock() {

        return Clock.systemDefaultZone();

    }



    @Bean

    public UrgencyCalculator urgencyCalculator(Clock clock) {

        return new UrgencyCalculator(clock);

    }





    @Bean

    public RetrieveDashboardUseCase retrieveDashboardUseCase(ParcelRepositoryPort parcelRepositoryPort, UrgencyCalculator urgencyCalculator) {

        return new RetrieveDashboardUseCase(parcelRepositoryPort, urgencyCalculator);

    }

}
