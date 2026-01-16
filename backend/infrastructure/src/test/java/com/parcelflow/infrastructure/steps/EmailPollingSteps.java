package com.parcelflow.infrastructure.steps;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartHeader;
import com.parcelflow.infrastructure.in.job.GmailPollingJob;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EmailPollingSteps {

    @Autowired
    private Gmail gmail;

    @Autowired
    private GmailPollingJob gmailPollingJob;

    private Gmail.Users.Messages messages;

    @Given("que la boite Gmail contient un email non lu avec le sujet {string}")
    public void que_la_boite_gmail_contient_un_email_non_lu_avec_le_sujet(String subject) throws IOException {
        Gmail.Users users = mock(Gmail.Users.class);
        messages = mock(Gmail.Users.Messages.class);
        Gmail.Users.Messages.List listRequest = mock(Gmail.Users.Messages.List.class);
        Gmail.Users.Messages.Get getRequest = mock(Gmail.Users.Messages.Get.class);
        Gmail.Users.Messages.Modify modifyRequest = mock(Gmail.Users.Messages.Modify.class);

        when(gmail.users()).thenReturn(users);
        when(users.messages()).thenReturn(messages);
        when(messages.list(anyString())).thenReturn(listRequest);
        when(listRequest.setQ(anyString())).thenReturn(listRequest);
        
        ListMessagesResponse response = new ListMessagesResponse();
        Message msgSummary = new Message().setId("msg123").setThreadId("thread1");
        response.setMessages(List.of(msgSummary));
        when(listRequest.execute()).thenReturn(response);

        when(messages.get(anyString(), eq("msg123"))).thenReturn(getRequest);
        Message fullMsg = new Message().setId("msg123").setSnippet("Snippet...");
        MessagePart payload = new MessagePart();
        MessagePartHeader header = new MessagePartHeader().setName("Subject").setValue(subject);
        payload.setHeaders(List.of(header));
        fullMsg.setPayload(payload);
        fullMsg.setInternalDate(System.currentTimeMillis());
        
        when(getRequest.execute()).thenReturn(fullMsg);

        when(messages.modify(anyString(), eq("msg123"), any())).thenReturn(modifyRequest);
        when(modifyRequest.execute()).thenReturn(new Message());
    }

    @When("le job de polling s'exécute")
    public void le_job_de_polling_s_execute() {
        gmailPollingJob.poll();
    }

    @Then("l'email est identifié comme une livraison")
    public void l_email_est_identifie_comme_une_livraison() {
        // Assertions logic if needed
    }

    @Then("l'email est marqué comme traité dans Gmail")
    public void l_email_est_marque_comme_traite_dans_gmail() throws IOException {
        verify(messages).modify(anyString(), eq("msg123"), any());
    }
}
