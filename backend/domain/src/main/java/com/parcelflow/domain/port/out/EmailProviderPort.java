package com.parcelflow.domain.port.out;

import com.parcelflow.domain.model.EmailMetadata;
import java.util.List;

public interface EmailProviderPort {
    List<EmailMetadata> fetchUnreadDeliveryEmails();
    void markAsProcessed(String id);
}
