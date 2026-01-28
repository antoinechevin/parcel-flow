package com.parcelflow.infrastructure.scheduler;

import com.parcelflow.application.usecases.EmailPollingOrchestrator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class EmailPollingJob {

    private static final Logger log = LoggerFactory.getLogger(EmailPollingJob.class);
    private final EmailPollingOrchestrator orchestrator;

    public EmailPollingJob(EmailPollingOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @Scheduled(fixedDelayString = "${parcelflow.polling.interval:PT15M}")
    public void execute() {
        log.info("Executing scheduled email polling task...");
        try {
            orchestrator.run();
        } catch (Exception e) {
            log.error("Fatal error during scheduled polling execution", e);
        }
    }
}
