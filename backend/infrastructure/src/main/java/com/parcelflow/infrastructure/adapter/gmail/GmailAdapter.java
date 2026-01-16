package com.parcelflow.infrastructure.adapter.gmail;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.ModifyMessageRequest;
import com.parcelflow.domain.model.EmailMetadata;
import com.parcelflow.domain.port.out.EmailProviderPort;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class GmailAdapter implements EmailProviderPort {

    private final Gmail gmail;

    public GmailAdapter(Gmail gmail) {
        this.gmail = gmail;
    }

    @Override
    public List<EmailMetadata> fetchUnreadDeliveryEmails() {
        try {
            List<Message> messages = new ArrayList<>();
            // Query: subject:(colis OR livraison) is:unread
            String query = "subject:(colis OR livraison) is:unread";
            
            Gmail.Users.Messages.List request = gmail.users().messages().list("me").setQ(query);
            ListMessagesResponse response = request.execute();
            
            if (response.getMessages() != null) {
                messages.addAll(response.getMessages());
            }

            // Fetch details for each message
            List<EmailMetadata> results = new ArrayList<>();
            for (Message msg : messages) {
                Message fullMsg = gmail.users().messages().get("me", msg.getId()).execute();
                
                String subject = "(No Subject)";
                String snippet = fullMsg.getSnippet();
                long internalDate = fullMsg.getInternalDate();
                LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(internalDate), ZoneId.systemDefault());
                
                if (fullMsg.getPayload() != null && fullMsg.getPayload().getHeaders() != null) {
                     subject = fullMsg.getPayload().getHeaders().stream()
                        .filter(h -> "Subject".equalsIgnoreCase(h.getName()))
                        .map(com.google.api.services.gmail.model.MessagePartHeader::getValue)
                        .findFirst()
                        .orElse("(No Subject)");
                }

                results.add(new EmailMetadata(fullMsg.getId(), subject, snippet, date));
            }
            return results;

        } catch (IOException e) {
            throw new RuntimeException("Failed to fetch emails from Gmail", e);
        }
    }

    @Override
    public void markAsProcessed(String id) {
        try {
            ModifyMessageRequest modRequest = new ModifyMessageRequest()
                .setRemoveLabelIds(Collections.singletonList("UNREAD"));
            gmail.users().messages().modify("me", id, modRequest).execute();
        } catch (IOException e) {
            throw new RuntimeException("Failed to mark email as processed", e);
        }
    }
}
