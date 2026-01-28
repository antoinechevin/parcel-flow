package com.parcelflow.steps;

import com.parcelflow.infrastructure.config.ApplicationConfig;
import com.parcelflow.infrastructure.persistence.InMemoryParcelRepository;
import com.parcelflow.domain.ports.MailSourcePort;
import com.parcelflow.domain.ports.ParcelExtractionPort;
import com.parcelflow.domain.ports.ProviderRegistryPort;
import com.parcelflow.domain.model.MailFetchResult;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ContextConfiguration;

import java.time.ZonedDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@CucumberContextConfiguration
@ContextConfiguration(classes = {CucumberConfiguration.TestConfig.class})
public class CucumberConfiguration {

    @Configuration
    @Import({ApplicationConfig.class, InMemoryParcelRepository.class})
    static class TestConfig {
        @Bean
        @Primary
        public MailSourcePort mailSourcePort() {
            MailSourcePort mock = mock(MailSourcePort.class);
            // Default return to avoid NPE in polling orchestrator
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
        public ProviderRegistryPort providerRegistryPort() {
            return mock(ProviderRegistryPort.class);
        }
    }
}