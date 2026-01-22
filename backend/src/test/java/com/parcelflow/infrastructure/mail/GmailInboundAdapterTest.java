package com.parcelflow.infrastructure.mail;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartHeader;
import com.parcelflow.domain.exception.MailSourceException;
import com.parcelflow.domain.model.MailFetchResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class GmailInboundAdapterTest {

    private static final ZoneId UTC = ZoneId.of("UTC");
    private Gmail gmailService;
    private Gmail.Users users;
    private Gmail.Users.Messages messages;
    private GmailInboundAdapter adapter;

    @BeforeEach
    void setUp() {
        gmailService = mock(Gmail.class);
        users = mock(Gmail.Users.class);
        messages = mock(Gmail.Users.Messages.class);
        
        when(gmailService.users()).thenReturn(users);
        when(users.messages()).thenReturn(messages);
        
        adapter = new GmailInboundAdapter(gmailService);
    }

    @Test
    void should_fetch_emails_and_update_watermark() throws IOException {
        // Given
        ZonedDateTime since = ZonedDateTime.parse("2026-01-22T09:00:00Z").withZoneSameInstant(UTC);
        String query = "subject:colis";

        Gmail.Users.Messages.List listRequest = mock(Gmail.Users.Messages.List.class);
        when(messages.list("me")).thenReturn(listRequest);
        when(listRequest.setQ(anyString())).thenReturn(listRequest);
        
        Message msgSummary = new Message().setId("msg123");
        ListMessagesResponse listResponse = new ListMessagesResponse()
                .setMessages(List.of(msgSummary));
        when(listRequest.execute()).thenReturn(listResponse);

        Gmail.Users.Messages.Get getRequest = mock(Gmail.Users.Messages.Get.class);
        when(messages.get("me", "msg123")).thenReturn(getRequest);

        Message fullMsg = new Message()
                .setId("msg123")
                .setInternalDate(ZonedDateTime.parse("2026-01-22T10:00:00Z").toInstant().toEpochMilli())
                .setSnippet("You have a parcel")
                .setPayload(new MessagePart().setHeaders(List.of(
                        new MessagePartHeader().setName("Subject").setValue("Colis Arrivé"),
                        new MessagePartHeader().setName("From").setValue("sender@test.com")
                )));
        when(getRequest.execute()).thenReturn(fullMsg);

        // When
        MailFetchResult result = adapter.fetchEmails(since, query);

        // Then
        assertEquals(1, result.emails().size());
        assertEquals("Colis Arrivé", result.emails().get(0).subject());
        assertTrue(ZonedDateTime.parse("2026-01-22T10:00:00Z").isEqual(result.newWatermark()));
    }

    @Test
    void should_throw_mail_source_exception_on_io_error() throws IOException {
        // Given
        Gmail.Users.Messages.List listRequest = mock(Gmail.Users.Messages.List.class);
        when(messages.list("me")).thenReturn(listRequest);
        when(listRequest.setQ(anyString())).thenReturn(listRequest);
        when(listRequest.execute()).thenThrow(new IOException("Connection failed"));

        // When & Then
        assertThrows(MailSourceException.class, () -> 
            adapter.fetchEmails(ZonedDateTime.now(), "query")
        );
    }

    @Test
    void should_return_empty_list_when_no_messages_found() throws IOException {
        // Given
        ZonedDateTime since = ZonedDateTime.parse("2026-01-22T11:00:00Z");
        Gmail.Users.Messages.List listRequest = mock(Gmail.Users.Messages.List.class);
        when(messages.list("me")).thenReturn(listRequest);
        when(listRequest.setQ(anyString())).thenReturn(listRequest);
        when(listRequest.execute()).thenReturn(new ListMessagesResponse());

        // When
        MailFetchResult result = adapter.fetchEmails(since, "query");

        // Then
        assertEquals(0, result.emails().size());
        assertEquals(since, result.newWatermark());
    }
}
