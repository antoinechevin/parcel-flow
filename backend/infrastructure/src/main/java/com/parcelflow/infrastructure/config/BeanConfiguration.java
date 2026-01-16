package com.parcelflow.infrastructure.config;

import com.parcelflow.application.service.ScanEmailsService;
import com.parcelflow.domain.port.in.ScanEmailsUseCase;
import com.parcelflow.domain.port.out.EmailProviderPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public ScanEmailsUseCase scanEmailsUseCase(EmailProviderPort emailProviderPort) {
        return new ScanEmailsService(emailProviderPort);
    }
}
