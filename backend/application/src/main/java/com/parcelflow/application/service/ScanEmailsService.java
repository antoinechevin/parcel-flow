package com.parcelflow.application.service;

import com.parcelflow.domain.model.EmailMetadata;
import com.parcelflow.domain.port.in.ScanEmailsUseCase;
import com.parcelflow.domain.port.out.EmailProviderPort;
import java.util.List;

public class ScanEmailsService implements ScanEmailsUseCase {

    private final EmailProviderPort emailProviderPort;

    public ScanEmailsService(EmailProviderPort emailProviderPort) {
        this.emailProviderPort = emailProviderPort;
    }

    @Override
    public void scan() {
        List<EmailMetadata> emails = emailProviderPort.fetchUnreadDeliveryEmails();
        for (EmailMetadata email : emails) {
            emailProviderPort.markAsProcessed(email.id());
        }
    }
}
