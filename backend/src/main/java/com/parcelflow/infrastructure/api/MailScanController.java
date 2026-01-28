package com.parcelflow.infrastructure.api;

import com.parcelflow.application.usecases.EmailPollingOrchestrator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/debug")
public class MailScanController {

    private static final Logger log = LoggerFactory.getLogger(MailScanController.class);

    private final EmailPollingOrchestrator orchestrator;

    public MailScanController(EmailPollingOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @GetMapping("/scan")
    public Map<String, Object> scanEmails(@RequestParam(defaultValue = "7") int days) {
        log.info("Starting targeted debug email scan for last {} days...", days);
        
        ZonedDateTime since = ZonedDateTime.now(ZoneId.of("UTC")).minusDays(days);
        
        // On délègue à l'orchestrateur avec l'override de watermark
        // Note: L'orchestrateur actuel ne retourne pas de rapport détaillé, 
        // on simplifie donc la réponse du contrôleur de debug.
        orchestrator.run(since);

        Map<String, Object> report = new HashMap<>();
        report.put("scannedDays", days);
        report.put("status", "COMPLETED");
        report.put("message", "Check logs for detailed processing results");

        return report;
    }
}
