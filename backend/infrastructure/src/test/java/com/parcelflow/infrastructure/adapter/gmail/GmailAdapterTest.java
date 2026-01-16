package com.parcelflow.infrastructure.adapter.gmail;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.parcelflow.domain.model.EmailMetadata;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GmailAdapterTest {

    @Mock
    private Gmail gmail;

    @Mock
    private Gmail.Users users;

    @Mock
    private Gmail.Users.Messages messages;

    @Mock
    private Gmail.Users.Messages.List listRequest;

    @Mock
    private Gmail.Users.Messages.Get getRequest;

    @InjectMocks
    private GmailAdapter gmailAdapter;

    @Test
    void shouldFetchEmails() throws IOException {
        // Mocking the chain: gmail.users().messages().list(...)
        when(gmail.users()).thenReturn(users);
        when(users.messages()).thenReturn(messages);
        when(messages.list(anyString())).thenReturn(listRequest);
        when(listRequest.setQ(anyString())).thenReturn(listRequest);

        ListMessagesResponse response = new ListMessagesResponse();
        Message messageSummary = new Message().setId("123").setThreadId("thread1");
        response.setMessages(List.of(messageSummary));
        when(listRequest.execute()).thenReturn(response);

        // Mocking get details: gmail.users().messages().get(...)
        when(messages.get(anyString(), eq("123"))).thenReturn(getRequest);
        Message fullMessage = new Message()
            .setId("123")
            .setSnippet("Your parcel...")
            .setInternalDate(System.currentTimeMillis());
        when(getRequest.execute()).thenReturn(fullMessage);

        List<EmailMetadata> results = gmailAdapter.fetchUnreadDeliveryEmails();

        assertThat(results).hasSize(1);
        assertThat(results.get(0).id()).isEqualTo("123");
        assertThat(results.get(0).snippet()).isEqualTo("Your parcel...");
    }

    @Test
    void shouldMarkAsProcessed() throws IOException {
        // Mocking modify
        when(gmail.users()).thenReturn(users);
        when(users.messages()).thenReturn(messages);
        Gmail.Users.Messages.Modify modifyRequest = mock(Gmail.Users.Messages.Modify.class);
        when(messages.modify(anyString(), eq("123"), any())).thenReturn(modifyRequest);
        when(modifyRequest.execute()).thenReturn(new Message());

        gmailAdapter.markAsProcessed("123");

        verify(messages).modify(anyString(), eq("123"), argThat(req -> 
            req.getRemoveLabelIds().contains("UNREAD")
        ));
    }
}
