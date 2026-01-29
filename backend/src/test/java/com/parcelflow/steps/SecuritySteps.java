package com.parcelflow.steps;

import com.parcelflow.infrastructure.api.security.ApiKeyFilter;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

public class SecuritySteps {

    @Autowired
    private TestRestTemplate restTemplate;

    private ResponseEntity<String> lastResponse;

    @Given("the system security key is {string}")
    public void theSystemSecurityKeyIs(String key) {
        ApiKeyFilter.TEST_KEY_OVERRIDE = key;
    }

    @When("I request the dashboard without a security header")
    public void iRequestTheDashboardWithoutASecurityHeader() {
        lastResponse = restTemplate.getForEntity("/api/dashboard", String.class);
    }

    @When("I request the dashboard with header {string} set to {string}")
    public void iRequestTheDashboardWithHeaderSetTo(String headerName, String headerValue) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(headerName, headerValue);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        lastResponse = restTemplate.exchange("/api/dashboard", HttpMethod.GET, entity, String.class);
    }

    @Then("I should receive a {int} Unauthorized response")
    public void iShouldReceiveAUnauthorizedResponse(int statusCode) {
        assertThat(lastResponse.getStatusCode().value()).isEqualTo(statusCode);
    }

    @Then("I should receive a {int} OK response")
    public void iShouldReceiveAOKResponse(int statusCode) {
        assertThat(lastResponse.getStatusCode().value()).isEqualTo(statusCode);
    }
}
