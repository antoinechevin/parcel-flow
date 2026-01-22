package com.parcelflow.infrastructure.mail;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.parcelflow.domain.exception.MailSourceException;
import com.parcelflow.domain.model.InboundEmail;
import com.parcelflow.domain.model.MailFetchResult;
import com.parcelflow.domain.ports.MailSourcePort;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class GmailInboundAdapter implements MailSourcePort {

    public static final String DEFAULT_DELIVERY_QUERY = "subject:(colis OR livraison)";
    private final Gmail gmailService;

    public GmailInboundAdapter(Gmail gmailService) {
        this.gmailService = gmailService;
    }

    @Override
    public MailFetchResult fetchEmails(ZonedDateTime since, String query) {
        try {
            String searchQuery = (query == null || query.isBlank()) ? DEFAULT_DELIVERY_QUERY : query;
            long epochSecond = since.toEpochSecond();
            String fullQuery = searchQuery + " after:" + epochSecond;

            ListMessagesResponse response = gmailService.users().messages().list("me")
                    .setQ(fullQuery)
                    .execute();

            List<Message> messages = response.getMessages();
            if (messages == null || messages.isEmpty()) {
                return new MailFetchResult(List.of(), since);
            }

            List<InboundEmail> emails = new ArrayList<>();
            ZonedDateTime latestTimestamp = since;

            for (Message msgSummary : messages) {
                Message msg = gmailService.users().messages().get("me", msgSummary.getId()).execute();
                InboundEmail email = mapToDomain(msg);
                emails.add(email);
                
                if (email.receivedAt().isAfter(latestTimestamp)) {
                    latestTimestamp = email.receivedAt();
                }
            }

            return new MailFetchResult(emails, latestTimestamp);
        } catch (IOException e) {
            throw new MailSourceException("Failed to fetch emails from Gmail API", e);
        }
    }

    private InboundEmail mapToDomain(Message msg) {
        String id = msg.getId();
        String subject = "";
        String sender = "";
        long internalDate = msg.getInternalDate();
        ZonedDateTime receivedAt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(internalDate), ZoneId.of("UTC"));

        if (msg.getPayload() != null && msg.getPayload().getHeaders() != null) {
            subject = msg.getPayload().getHeaders().stream()
                    .filter(h -> h.getName().equalsIgnoreCase("Subject"))
                    .map(h -> h.getValue())
                    .findFirst().orElse("");
            sender = msg.getPayload().getHeaders().stream()
                    .filter(h -> h.getName().equalsIgnoreCase("From"))
                    .map(h -> h.getValue())
                    .findFirst().orElse("");
        }

        String body = msg.getSnippet(); // Simplified: use snippet as body for now

        return new InboundEmail(id, subject, body, sender, receivedAt);
    }
}
