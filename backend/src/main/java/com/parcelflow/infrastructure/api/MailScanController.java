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
    private final ParcelExtractionPort extractionPort;

    public MailScanController(MailSourcePort mailSourcePort, 
                              ParcelExtractionPort extractionPort) {
        this.mailSourcePort = mailSourcePort;
        this.extractionPort = extractionPort;
    }

    @GetMapping("/scan")
    public Map<String, Object> scanEmails(@RequestParam(defaultValue = "7") int days) {
        log.info("Starting manual email scan for last {} days...", days);
        
        ZonedDateTime since = ZonedDateTime.now(ZoneId.of("UTC")).minusDays(days);
        
        // 1. Fetch Emails
        MailFetchResult result = mailSourcePort.fetchEmails(since, null); // uses default query
        List<InboundEmail> emails = result.emails();
        
        log.info("Fetched {} emails.", emails.size());

        List<Map<String, Object>> extractionResults = new ArrayList<>();

        // 2. Process Extraction & Capture Details
        for (InboundEmail email : emails) {
            String contentToAnalyze = "Subject: " + email.subject() + "\n\n" + email.body();
            
            Optional<ParcelMetadata> metadataOpt = extractionPort.extract(contentToAnalyze);
            
            Map<String, Object> emailResult = new HashMap<>();
            emailResult.put("subject", email.subject());
            emailResult.put("receivedAt", email.receivedAt());
            emailResult.put("sender", email.sender());
            
            if (metadataOpt.isPresent()) {
                emailResult.put("status", "EXTRACTED");
                emailResult.put("metadata", metadataOpt.get());
            } else {
                emailResult.put("status", "IGNORED_OR_FAILED");
                emailResult.put("reason", "No critical metadata found by AI");
            }
            
            extractionResults.add(emailResult);
        }

        Map<String, Object> report = new HashMap<>();
        report.put("scannedDays", days);
        report.put("emailsFound", emails.size());
        report.put("results", extractionResults);

        return report;
    }
}
