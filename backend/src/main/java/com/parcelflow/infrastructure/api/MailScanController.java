package com.parcelflow.infrastructure.api;

import com.parcelflow.domain.model.InboundEmail;
import com.parcelflow.domain.model.MailFetchResult;
import com.parcelflow.domain.model.ParcelMetadata;
import com.parcelflow.domain.ports.MailSourcePort;
import com.parcelflow.domain.ports.ParcelExtractionPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/debug")
public class MailScanController {

    private static final Logger log = LoggerFactory.getLogger(MailScanController.class);

    private final MailSourcePort mailSourcePort;
    private final com.parcelflow.infrastructure.extraction.ProviderRegistry providerRegistry;
    private final com.parcelflow.application.usecases.ExtractParcelUseCase extractParcelUseCase;

    public MailScanController(MailSourcePort mailSourcePort, 
                              com.parcelflow.infrastructure.extraction.ProviderRegistry providerRegistry,
                              com.parcelflow.application.usecases.ExtractParcelUseCase extractParcelUseCase) {
        this.mailSourcePort = mailSourcePort;
        this.providerRegistry = providerRegistry;
        this.extractParcelUseCase = extractParcelUseCase;
    }

    @GetMapping("/scan")
    public Map<String, Object> scanEmails(@RequestParam(defaultValue = "7") int days) {
        log.info("Starting targeted debug email scan for last {} days...", days);
        
        ZonedDateTime since = ZonedDateTime.now(ZoneId.of("UTC")).minusDays(days);
        List<Map<String, Object>> globalResults = new ArrayList<>();
        int totalEmailsFound = 0;

        for (var provider : providerRegistry.getAllProviders()) {
            log.info("Scanning for provider: {} with query: {}", provider.name(), provider.query());
            
            MailFetchResult result = mailSourcePort.fetchEmails(since, provider.query());
            List<InboundEmail> emails = result.emails();
            totalEmailsFound += emails.size();

            for (InboundEmail email : emails) {
                Map<String, Object> emailResult = new HashMap<>();
                emailResult.put("provider", provider.name());
                emailResult.put("subject", email.subject());
                emailResult.put("receivedAt", email.receivedAt());
                
                try {
                    String contentToAnalyze = "Subject: " + email.subject() + "\n\n" + email.body();
                    // On utilise le useCase qui sauvegarde en DB si l'extraction réussit
                    Optional<ParcelMetadata> metadataOpt = extractParcelUseCase.execute(contentToAnalyze, email.receivedAt(), provider.adapter());
                    
                    if (metadataOpt.isPresent()) {
                        emailResult.put("status", "PROCESSED");
                        ParcelMetadata meta = metadataOpt.get();
                        Map<String, Object> metaMap = new HashMap<>();
                        metaMap.put("trackingNumber", meta.trackingCode());
                        metaMap.put("carrier", meta.carrier());
                        metaMap.put("expirationDate", meta.expirationDate());
                        metaMap.put("pickupLocation", meta.pickupLocation());
                        emailResult.put("metadata", metaMap);
                    } else {
                        emailResult.put("status", "NO_MATCH");
                    }
                } catch (Exception e) {
                    log.error("Failed to process email {} for provider {}", email.id(), provider.name(), e);
                    emailResult.put("status", "ERROR");
                    emailResult.put("error", e.getMessage());
                }
                globalResults.add(emailResult);
            }
        }

        Map<String, Object> report = new HashMap<>();
        report.put("scannedDays", days);
        report.put("totalEmailsFound", totalEmailsFound);
        report.put("results", globalResults);

        return report;
    }
}
