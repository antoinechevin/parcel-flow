package com.parcelflow.steps;

import com.parcelflow.infrastructure.config.ApplicationConfig;
import com.parcelflow.infrastructure.persistence.InMemoryParcelRepository;
import com.parcelflow.domain.ports.MailSourcePort;
import com.parcelflow.domain.ports.ParcelExtractionPort;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ContextConfiguration;

import static org.mockito.Mockito.mock;

@CucumberContextConfiguration
@ContextConfiguration(classes = {CucumberConfiguration.TestConfig.class})
public class CucumberConfiguration {

    @Configuration
    @Import({ApplicationConfig.class, InMemoryParcelRepository.class})
    static class TestConfig {
        @Bean
        @Primary
        public MailSourcePort mailSourcePort() {
            return mock(MailSourcePort.class);
        }

        @Bean
        @Primary
        public ParcelExtractionPort parcelExtractionPort() {
            return mock(ParcelExtractionPort.class);
        }
    }
}