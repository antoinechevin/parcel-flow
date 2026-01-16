package com.parcelflow.application.service;

import com.parcelflow.domain.model.EmailMetadata;
import com.parcelflow.domain.port.out.EmailProviderPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScanEmailsServiceTest {

    @Mock
    private EmailProviderPort emailProviderPort;

    @InjectMocks
    private ScanEmailsService scanEmailsService;

    @Test
    void shouldFetchAndMarkEmails() {
        // Given
        EmailMetadata email1 = new EmailMetadata("1", "Colis", "...", LocalDateTime.now());
        EmailMetadata email2 = new EmailMetadata("2", "Livraison", "...", LocalDateTime.now());
        when(emailProviderPort.fetchUnreadDeliveryEmails()).thenReturn(List.of(email1, email2));

        // When
        scanEmailsService.scan();

        // Then
        verify(emailProviderPort).fetchUnreadDeliveryEmails();
        verify(emailProviderPort).markAsProcessed("1");
        verify(emailProviderPort).markAsProcessed("2");
    }
}
