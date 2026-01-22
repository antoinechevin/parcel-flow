package com.parcelflow.steps;

import com.parcelflow.application.RetrieveDashboardUseCase;
import com.parcelflow.domain.Parcel;
import com.parcelflow.domain.ParcelStatus;
import com.parcelflow.infrastructure.persistence.InMemoryParcelRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DashboardSteps {

    @Autowired
    private RetrieveDashboardUseCase useCase;
    
    private List<Parcel> retrievedParcels;

    @Given("the following parcels exist:")
    public void the_following_parcels_exist(List<Map<String, String>> dataTable) {
        // MVP: Repository is hardcoded.
    }

    @When("I request the dashboard parcel list")
    public void i_request_the_dashboard_parcel_list() {
        retrievedParcels = useCase.retrieve();
    }

    @Then("I should receive {int} parcels")
    public void i_should_receive_parcels(int count) {
        assertEquals(count, retrievedParcels.size());
    }

    @Then("the parcel with tracking number {string} should be {string}")
    public void the_parcel_with_tracking_number_should_be(String trackingNumber, String statusStr) {
        Parcel parcel = retrievedParcels.stream()
            .filter(p -> p.trackingNumber().equals(trackingNumber))
            .findFirst()
            .orElseThrow(() -> new AssertionError("Parcel not found: " + trackingNumber));
        
        assertEquals(ParcelStatus.valueOf(statusStr), parcel.status());
    }
}
