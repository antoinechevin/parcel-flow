package com.parcelflow.steps;

import com.parcelflow.BackendApplication;
import com.parcelflow.application.usecases.ExtractParcelUseCase;
import com.parcelflow.infrastructure.persistence.InMemoryParcelRepository;
import com.parcelflow.domain.ports.MailSourcePort;
import com.parcelflow.domain.ports.ParcelExtractionPort;
import com.parcelflow.domain.ports.ParcelRepositoryPort;
import com.parcelflow.domain.ports.ProviderRegistryPort;
import com.parcelflow.domain.model.MailFetchResult;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.ZonedDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@CucumberContextConfiguration
@SpringBootTest(classes = {BackendApplication.class, CucumberConfiguration.TestConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@org.springframework.test.context.ActiveProfiles("test")
public class CucumberConfiguration {

    @Configuration
    static class TestConfig {
        @Bean
        @Primary
        public MailSourcePort mailSourcePort() {
            MailSourcePort mock = mock(MailSourcePort.class);
            when(mock.fetchEmails(any(), any())).thenReturn(new MailFetchResult(Collections.emptyList(), ZonedDateTime.now()));
            return mock;
        }

        @Bean
        @Primary
        public ParcelExtractionPort parcelExtractionPort() {
            return mock(ParcelExtractionPort.class);
        }

        @Bean
        @Primary
        public ExtractParcelUseCase extractParcelUseCase(ParcelExtractionPort parcelExtractionPort, ParcelRepositoryPort repositoryPort) {
            return new ExtractParcelUseCase(parcelExtractionPort, repositoryPort);
        }

        @Bean
        @Primary
        public ProviderRegistryPort providerRegistryPort() {
            return mock(ProviderRegistryPort.class);
        }
    }
}
