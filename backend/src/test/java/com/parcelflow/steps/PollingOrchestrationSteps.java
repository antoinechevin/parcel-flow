package com.parcelflow.steps;

import com.parcelflow.application.usecases.EmailPollingOrchestrator;
import com.parcelflow.application.usecases.ExtractParcelUseCase;
import com.parcelflow.domain.model.MailFetchResult;
import com.parcelflow.domain.model.ProviderDefinition;
import com.parcelflow.domain.ports.MailSourcePort;
import com.parcelflow.domain.ports.ProviderRegistryPort;
import com.parcelflow.domain.ports.WatermarkRepositoryPort;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.mockito.Mockito;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class PollingOrchestrationSteps {

    private ProviderRegistryPort providerRegistry;
    private MailSourcePort mailSourcePort;
    private WatermarkRepositoryPort watermarkRepositoryPort;
    private ExtractParcelUseCase extractParcelUseCase;
    private EmailPollingOrchestrator orchestrator;

    @Before
    public void setup() {
        providerRegistry = mock(ProviderRegistryPort.class);
        mailSourcePort = mock(MailSourcePort.class);
        watermarkRepositoryPort = mock(WatermarkRepositoryPort.class);
        extractParcelUseCase = mock(ExtractParcelUseCase.class);
        orchestrator = new EmailPollingOrchestrator(providerRegistry, mailSourcePort, watermarkRepositoryPort, extractParcelUseCase);
    }

    @Given("the following providers are configured in the registry:")
    public void the_following_providers_are_configured_in_the_registry(List<Map<String, String>> providers) {
        when(providerRegistry.getAllProviders()).thenReturn(
                providers.stream().map(row -> new ProviderDefinition(
                        row.get("name"),
                        row.get("query"),
                        null // Adapter not needed for this test
                )).toList()
        );
    }

    @Given("the watermark for {string} is {string}")
    public void the_watermark_for_is(String provider, String timestamp) {
        when(watermarkRepositoryPort.getWatermark(provider))
                .thenReturn(Optional.of(ZonedDateTime.parse(timestamp)));
    }

    @Given("the current watermark for {string} is {string}")
    public void the_current_watermark_for_is(String provider, String timestamp) {
        the_watermark_for_is(provider, timestamp);
    }

    @Given("the provider {string} is configured with query {string}")
    public void the_provider_is_configured_with_query(String name, String query) {
        when(providerRegistry.getAllProviders()).thenReturn(
                List.of(new ProviderDefinition(name, query, null))
        );
    }

    @Given("the mail source returns a new watermark {string} for {string}")
    public void the_mail_source_returns_a_new_watermark_for(String newWatermark, String provider) {
        MailFetchResult result = new MailFetchResult(Collections.emptyList(), ZonedDateTime.parse(newWatermark));
        when(mailSourcePort.fetchEmails(any(), anyString())).thenReturn(result);
    }

    @Given("the mail source throws an error for {string}")
    public void the_mail_source_throws_an_error_for(String providerName) {
         when(mailSourcePort.fetchEmails(any(), contains("faulty")))
                 .thenThrow(new RuntimeException("Mail source error"));
    }

    @When("the polling orchestrator runs")
    public void the_polling_orchestrator_runs() {
        orchestrator.run();
    }

    @Then("the mail source should be called for {string} with query {string} and watermark {string}")
    public void the_mail_source_should_be_called_for_with_query_and_watermark(String provider, String query, String watermark) {
        verify(mailSourcePort).fetchEmails(eq(ZonedDateTime.parse(watermark)), eq(query));
    }

    @Then("the watermark for {string} should be saved as {string}")
    public void the_watermark_for_should_be_saved_as(String provider, String timestamp) {
        verify(watermarkRepositoryPort).saveWatermark(eq(provider), eq(ZonedDateTime.parse(timestamp)));
    }

    @Then("the mail source should be called for {string} with query {string}")
    public void the_mail_source_should_be_called_for_with_query(String provider, String query) {
        verify(mailSourcePort).fetchEmails(any(), eq(query));
    }
}