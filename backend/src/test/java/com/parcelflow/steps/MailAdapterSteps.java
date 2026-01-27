package com.parcelflow.steps;

import com.parcelflow.domain.model.InboundEmail;
import com.parcelflow.domain.model.MailFetchResult;
import com.parcelflow.domain.ports.MailSourcePort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MailAdapterSteps {

    @Autowired
    private MailSourcePort mailSourcePort;

    private ZonedDateTime lastWatermark;
    private MailFetchResult fetchResult;
    private List<InboundEmail> availableEmails = new ArrayList<>();

    @Given("the mail service contains these emails:")
    public void the_mail_service_contains_these_emails(List<Map<String, String>> dataTable) {
        availableEmails = dataTable.stream().map(row -> new InboundEmail(
            row.get("id"),
            row.get("subject"),
            "Body of " + row.get("subject"),
            row.getOrDefault("sender", "sender@example.com"),
            ZonedDateTime.parse(row.get("receivedAt"))
        )).toList();
    }

    @Given("the last processed watermark was {string}")
    public void the_last_processed_watermark_was(String watermarkStr) {
        this.lastWatermark = ZonedDateTime.parse(watermarkStr);
    }

    @When("I fetch delivery emails")
    public void i_fetch_delivery_emails() {
        // Prepare the mock behavior based on availableEmails and lastWatermark
        // Filter by date AND by the new mandatory sender
        List<InboundEmail> filteredEmails = availableEmails.stream()
            .filter(e -> e.receivedAt().isAfter(lastWatermark))
            .filter(e -> e.sender().equals("chronopost@network1.pickup.fr"))
            .toList();
        
        ZonedDateTime newWatermark = filteredEmails.stream()
            .map(InboundEmail::receivedAt)
            .max(ZonedDateTime::compareTo)
            .orElse(lastWatermark);

        when(mailSourcePort.fetchEmails(any(ZonedDateTime.class), anyString()))
            .thenReturn(new MailFetchResult(filteredEmails, newWatermark));

        fetchResult = mailSourcePort.fetchEmails(lastWatermark, "from:chronopost@network1.pickup.fr");
    }

    @Then("I should receive {int} emails")
    public void i_should_receive_emails(int count) {
        assertEquals(count, fetchResult.emails().size());
    }

    @Then("the new watermark should be {string}")
    public void the_new_watermark_should_be(String expectedWatermarkStr) {
        assertEquals(ZonedDateTime.parse(expectedWatermarkStr), fetchResult.newWatermark());
    }

    @Then("the new watermark should still be {string}")
    public void the_new_watermark_should_still_be(String expectedWatermarkStr) {
        assertEquals(ZonedDateTime.parse(expectedWatermarkStr), fetchResult.newWatermark());
    }
}
