package com.parcelflow.application.usecases;

import com.parcelflow.domain.model.InboundEmail;
import com.parcelflow.domain.model.MailFetchResult;
import com.parcelflow.domain.model.ProviderDefinition;
import com.parcelflow.domain.ports.MailSourcePort;
import com.parcelflow.domain.ports.ProviderRegistryPort;
import com.parcelflow.domain.ports.WatermarkRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.util.List;

public class EmailPollingOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(EmailPollingOrchestrator.class);

    private final ProviderRegistryPort providerRegistry;
    private final MailSourcePort mailSourcePort;
    private final WatermarkRepositoryPort watermarkRepositoryPort;
    private final ExtractParcelUseCase extractParcelUseCase;

    public EmailPollingOrchestrator(ProviderRegistryPort providerRegistry,
                                    MailSourcePort mailSourcePort,
                                    WatermarkRepositoryPort watermarkRepositoryPort,
                                    ExtractParcelUseCase extractParcelUseCase) {
        this.providerRegistry = providerRegistry;
        this.mailSourcePort = mailSourcePort;
        this.watermarkRepositoryPort = watermarkRepositoryPort;
        this.extractParcelUseCase = extractParcelUseCase;
    }

    public void run() {
        run(null);
    }

    public void run(ZonedDateTime overrideWatermark) {
        log.info("Starting email polling job{}...", overrideWatermark != null ? " with override watermark " + overrideWatermark : "");
        List<ProviderDefinition> providers = providerRegistry.getAllProviders();

        for (ProviderDefinition provider : providers) {
            try {
                log.info("Polling emails for provider: {}", provider.name());

                // Get last watermark or default to 14 days ago
                ZonedDateTime watermark = overrideWatermark != null ? overrideWatermark : 
                        watermarkRepositoryPort.getWatermark(provider.name())
                        .orElse(ZonedDateTime.now().minusDays(14));

                // Fetch emails
                MailFetchResult result = mailSourcePort.fetchEmails(watermark, provider.query());
                log.info("Fetched {} emails for provider {}", result.emails().size(), provider.name());

                // Process each email
                for (InboundEmail email : result.emails()) {
                    try {
                        log.debug("Processing email {} from {}", email.id(), email.sender());
                        // Extract and save parcel
                        extractParcelUseCase.execute(email.body(), email.receivedAt(), provider.adapter());
                    } catch (Exception e) {
                        log.error("Error processing email {} for provider {}: {}", email.id(), provider.name(), e.getMessage(), e);
                        // Continue to next email
                    }
                }

                // Update watermark if successful and NOT an override
                if (overrideWatermark == null && result.newWatermark() != null) {
                    watermarkRepositoryPort.saveWatermark(provider.name(), result.newWatermark());
                    log.info("Updated watermark for provider {} to {}", provider.name(), result.newWatermark());
                }

            } catch (Exception e) {
                log.error("Error polling provider {}: {}", provider.name(), e.getMessage(), e);
                // Continue to next provider
            }
        }
        log.info("Email polling job completed.");
    }
}