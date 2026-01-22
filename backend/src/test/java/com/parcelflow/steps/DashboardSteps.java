package com.parcelflow.steps;

import com.parcelflow.application.usecases.RetrieveDashboardUseCase;
import com.parcelflow.domain.model.LocationGroup;
import com.parcelflow.domain.model.Parcel;
import com.parcelflow.domain.model.ParcelId;
import com.parcelflow.domain.model.ParcelStatus;
import com.parcelflow.domain.model.PickupPoint;
import com.parcelflow.domain.ports.ParcelRepositoryPort;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DashboardSteps {

    private static final PickupPoint DEFAULT_PP = new PickupPoint("default", "Default Relais", "Address", "08:00-19:00");

    @Autowired
    private RetrieveDashboardUseCase useCase;

    @Autowired
    private ParcelRepositoryPort repository;
    
    private List<LocationGroup> retrievedGroups;

    @Before
    public void reset() {
        repository.deleteAll();
    }

    @Given("the following parcels exist:")
    public void the_following_parcels_exist(List<Map<String, String>> dataTable) {
        List<Parcel> parcels = dataTable.stream().map(row -> {
            String trackingNumber = row.get("trackingNumber");
            ParcelStatus status = ParcelStatus.valueOf(row.get("status"));
            int daysRelativeToNow = Integer.parseInt(row.get("daysRelativeToNow"));
            LocalDate deadline = LocalDate.now().plusDays(daysRelativeToNow);
            
            return new Parcel(
                ParcelId.random(),
                trackingNumber,
                deadline,
                status,
                DEFAULT_PP
            );
        }).collect(Collectors.toList());
        
        repository.saveAll(parcels);
    }

    @When("I request the dashboard parcel list")
    public void i_request_the_dashboard_parcel_list() {
        retrievedGroups = useCase.retrieve();
    }

    @Then("I should receive {int} parcels")
    public void i_should_receive_parcels(int count) {
        long totalParcels = retrievedGroups.stream()
            .mapToLong(g -> g.parcels().size())
            .sum();
        assertEquals(count, totalParcels);
    }

    @Then("the parcel with tracking number {string} should be {string}")
    public void the_parcel_with_tracking_number_should_be(String trackingNumber, String statusStr) {
        Parcel parcel = retrievedGroups.stream()
            .flatMap(g -> g.parcels().stream())
            .filter(p -> p.trackingNumber().equals(trackingNumber))
            .findFirst()
            .orElseThrow(() -> new AssertionError("Parcel not found: " + trackingNumber));
        
        assertEquals(ParcelStatus.valueOf(statusStr), parcel.status());
    }

    @Given("there are no parcels in the system")
    public void there_are_no_parcels_in_the_system() {
        repository.deleteAll();
    }

    @Given("these parcels exist:")
    public void these_parcels_exist(List<Map<String, String>> dataTable) {
        List<Parcel> parcels = dataTable.stream().map(row -> {
            String trackingNumber = row.get("trackingNumber");
            String ppName = row.get("pickupPoint");
            PickupPoint pp = new PickupPoint(ppName.toLowerCase().replace(" ", "-"), ppName, "Address of " + ppName, "08:00-19:00");
            
            return new Parcel(
                ParcelId.random(),
                trackingNumber,
                LocalDate.now().plusDays(5),
                ParcelStatus.AVAILABLE,
                pp
            );
        }).collect(Collectors.toList());
        
        repository.saveAll(parcels);
    }

    @When("I retrieve the dashboard")
    public void i_retrieve_the_dashboard() {
        retrievedGroups = useCase.retrieve();
    }

    @Then("I should see {int} location groups")
    public void i_should_see_location_groups(int count) {
        assertEquals(count, retrievedGroups.size());
    }

    @Then("the group {string} should contain {int} parcel(s)")
    public void the_group_should_contain_parcels(String ppName, int count) {
        LocationGroup group = retrievedGroups.stream()
            .filter(g -> g.pickupPoint().name().equals(ppName))
            .findFirst()
            .orElseThrow(() -> new AssertionError("Group not found: " + ppName));
        
        assertEquals(count, group.parcels().size());
    }

    @Given("a group {string} has a parcel expiring tomorrow")
    public void a_group_has_a_parcel_expiring_tomorrow(String ppName) {
        PickupPoint pp = new PickupPoint(ppName.toLowerCase().replace(" ", "-"), ppName, "Address", "08:00-19:00");
        Parcel parcel = new Parcel(
            ParcelId.random(),
            "EXP-TOMORROW",
            LocalDate.now().plusDays(1),
            ParcelStatus.AVAILABLE,
            pp
        );
        repository.saveAll(List.of(parcel));
    }

    @Given("a group {string} has a parcel expiring in {int} days")
    public void a_group_has_a_parcel_expiring_in_days(String ppName, int days) {
        PickupPoint pp = new PickupPoint(ppName.toLowerCase().replace(" ", "-"), ppName, "Address", "08:00-19:00");
        Parcel parcel = new Parcel(
            ParcelId.random(),
            "EXP-IN-" + days,
            LocalDate.now().plusDays(days),
            ParcelStatus.AVAILABLE,
            pp
        );
        repository.saveAll(List.of(parcel));
    }

    @Then("{string} should appear before {string}")
    public void should_appear_before(String firstPpName, String secondPpName) {
        List<String> names = retrievedGroups.stream()
            .map(g -> g.pickupPoint().name())
            .collect(Collectors.toList());
        
        int firstIndex = names.indexOf(firstPpName);
        int secondIndex = names.indexOf(secondPpName);
        
        assertTrue(firstIndex != -1, "Group not found: " + firstPpName);
        assertTrue(secondIndex != -1, "Group not found: " + secondPpName);
        assertTrue(firstIndex < secondIndex, 
            String.format("Expected %s (index %d) to be before %s (index %d)", 
                firstPpName, firstIndex, secondPpName, secondIndex));
    }
}
