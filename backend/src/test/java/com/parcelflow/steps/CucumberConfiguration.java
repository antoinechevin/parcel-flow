package com.parcelflow.steps;

import com.parcelflow.infrastructure.config.ApplicationConfig;
import com.parcelflow.infrastructure.persistence.InMemoryParcelRepository;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.test.context.ContextConfiguration;

@CucumberContextConfiguration
@ContextConfiguration(classes = {ApplicationConfig.class, InMemoryParcelRepository.class})
public class CucumberConfiguration {
}
