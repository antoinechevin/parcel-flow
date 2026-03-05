package com.parcelflow.infrastructure.config;

import com.parcelflow.domain.ports.MailSourcePort;
import com.parcelflow.infrastructure.mail.GmailInboundAdapter;
import com.parcelflow.infrastructure.mail.PreviewMailSourceDecorator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class PreviewConfig {

    @Bean
    @Primary
    @ConditionalOnProperty(name = "MOCK_MAIL_SOURCE", havingValue = "true")
    public MailSourcePort previewMailSourceDecorator(GmailInboundAdapter gmailInboundAdapter) {
        return new PreviewMailSourceDecorator(gmailInboundAdapter);
    }
}
