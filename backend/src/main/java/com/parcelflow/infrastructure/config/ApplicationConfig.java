package com.parcelflow.infrastructure.config;

import com.parcelflow.application.usecases.ArchiveParcelUseCase;
import com.parcelflow.application.usecases.ExtractParcelUseCase;
import com.parcelflow.application.usecases.RetrieveDashboardUseCase;
import com.parcelflow.application.usecases.EmailPollingOrchestrator;
import com.parcelflow.domain.ports.MailSourcePort;
import com.parcelflow.domain.ports.ParcelExtractionPort;
import com.parcelflow.domain.ports.ParcelRepositoryPort;
import com.parcelflow.domain.ports.ProviderRegistryPort;
import com.parcelflow.domain.ports.WatermarkRepositoryPort;
import com.parcelflow.domain.service.UrgencyCalculator;
import com.parcelflow.infrastructure.persistence.InMemoryWatermarkRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.Clock;

@Configuration
@EnableScheduling
@Profile("!no-scheduling")
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

    @Bean
    public ArchiveParcelUseCase archiveParcelUseCase(ParcelRepositoryPort parcelRepositoryPort) {
        return new ArchiveParcelUseCase(parcelRepositoryPort);
    }

    @Bean
    public WatermarkRepositoryPort watermarkRepositoryPort() {
        return new InMemoryWatermarkRepository();
    }

    @Bean
    public EmailPollingOrchestrator emailPollingOrchestrator(
            ProviderRegistryPort providerRegistry,
            MailSourcePort mailSourcePort,
            WatermarkRepositoryPort watermarkRepositoryPort,
            ExtractParcelUseCase extractParcelUseCase) {
        return new EmailPollingOrchestrator(providerRegistry, mailSourcePort, watermarkRepositoryPort, extractParcelUseCase);
    }

    @Bean
    public ExtractParcelUseCase extractParcelUseCase(java.util.List<ParcelExtractionPort> extractionPorts, ParcelRepositoryPort repositoryPort) {
        ParcelExtractionPort defaultAdapter = extractionPorts.stream()
                .filter(p -> p instanceof com.parcelflow.infrastructure.extraction.ChronopostPickupExtractionAdapter)
                .findFirst()
                .orElse(extractionPorts.get(0));
        return new ExtractParcelUseCase(defaultAdapter, repositoryPort);
    }
}


