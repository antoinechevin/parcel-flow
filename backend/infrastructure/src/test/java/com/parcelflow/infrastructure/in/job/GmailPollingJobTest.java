package com.parcelflow.infrastructure.in.job;

import com.parcelflow.domain.port.in.ScanEmailsUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GmailPollingJobTest {

    @Mock
    private ScanEmailsUseCase scanEmailsUseCase;

    @InjectMocks
    private GmailPollingJob gmailPollingJob;

    @Test
    void shouldTriggerScan() {
        gmailPollingJob.poll();
        verify(scanEmailsUseCase).scan();
    }
}
