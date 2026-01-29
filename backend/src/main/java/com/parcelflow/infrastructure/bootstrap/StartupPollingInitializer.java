package com.parcelflow.infrastructure.bootstrap;

import com.parcelflow.application.usecases.EmailPollingOrchestrator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class StartupPollingInitializer {

    private static final Logger log = LoggerFactory.getLogger(StartupPollingInitializer.class);
    private final EmailPollingOrchestrator orchestrator;

    public StartupPollingInitializer(EmailPollingOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("Application ready. Triggering initial email polling...");
        try {
            orchestrator.run();
            log.info("Initial email polling completed.");
        } catch (Exception e) {
            log.error("Failed to execute initial email polling at startup", e);
        }
    }
}
