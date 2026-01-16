package com.parcelflow.infrastructure.in.job;

import com.parcelflow.domain.port.in.ScanEmailsUseCase;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class GmailPollingJob {

    private final ScanEmailsUseCase scanEmailsUseCase;

    public GmailPollingJob(ScanEmailsUseCase scanEmailsUseCase) {
        this.scanEmailsUseCase = scanEmailsUseCase;
    }

    @Scheduled(fixedDelayString = "${gmail.polling.interval:60000}")
    public void poll() {
        scanEmailsUseCase.scan();
    }
}
