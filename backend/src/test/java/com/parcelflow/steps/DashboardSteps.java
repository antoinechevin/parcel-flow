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

import java.time.Clock;
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

    @Autowired
    private Clock clock;
    
    private List<LocationGroup> retrievedGroups;
    private Parcel lastParcel;

    @Before
    public void reset() {
        repository.deleteAll();
    }

    @Given("a parcel with a deadline on {string}")
    public void a_parcel_with_a_deadline_on(String dateStr) {
        PickupPoint pp = new PickupPoint("point-a", "Point A", "Address", "08:00-19:00");
        lastParcel = new Parcel(
            ParcelId.random(),
            "TRK-EXPIRED",
            "Carrier",
            LocalDate.parse(dateStr),
            ParcelStatus.AVAILABLE,
            pp,
            null,
            null
        );
        repository.save(lastParcel);
    }

    @Given("today's date is {string}")
    public void today_s_date_is(String dateStr) {
        if (clock instanceof TestClock) {
            ((TestClock) clock).setFixedDate(dateStr);
        }
    }

    @When("I check the parcel status")
    public void i_check_the_parcel_status() {
        retrievedGroups = useCase.retrieve();
    }

    @Then("the status should be {string}")
    public void the_status_should_be(String expectedStatus) {
        Parcel found = retrievedGroups.stream()
            .flatMap(g -> g.parcels().stream())
            .filter(p -> p.id().equals(lastParcel.id()))
            .findFirst()
            .orElseThrow();
        
        assertEquals(ParcelStatus.valueOf(expectedStatus), found.status());
    }

    @Given("these parcels are in the system:")
    public void these_parcels_are_in_the_system(List<Map<String, String>> dataTable) {
        List<Parcel> parcels = dataTable.stream().map(row -> {
            String ppName = row.get("pickupPoint");
            PickupPoint pp = new PickupPoint(ppName.toLowerCase().replace(" ", "-"), ppName, "Address", "08:00-19:00");
            return new Parcel(
                ParcelId.random(),
                row.get("trackingNumber"),
                row.get("carrier"),
                LocalDate.parse(row.get("deadline")),
                ParcelStatus.valueOf(row.get("status")),
                pp,
                null,
                null
            );
        }).collect(Collectors.toList());
        repository.saveAll(parcels);
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
                "Carrier",
                deadline,
                status,
                DEFAULT_PP,
                null,
                null
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
                "Carrier",
                LocalDate.now().plusDays(5),
                ParcelStatus.AVAILABLE,
                pp,
                null,
                null
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

    @Then("the first parcel should be {string}")
    public void the_first_parcel_should_be(String trackingNumber) {
        assertEquals(trackingNumber, retrievedGroups.get(0).parcels().get(0).trackingNumber());
    }

    @Then("the second parcel should be {string}")
    public void the_second_parcel_should_be(String trackingNumber) {
        assertEquals(trackingNumber, retrievedGroups.get(0).parcels().get(1).trackingNumber());
    }

    @Given("a group {string} has a parcel expiring tomorrow")
    public void a_group_has_a_parcel_expiring_tomorrow(String ppName) {
        PickupPoint pp = new PickupPoint(ppName.toLowerCase().replace(" ", "-"), ppName, "Address", "08:00-19:00");
        Parcel parcel = new Parcel(
            ParcelId.random(),
            "EXP-TOMORROW",
            "Carrier",
            LocalDate.now().plusDays(1),
            ParcelStatus.AVAILABLE,
            pp,
            null,
            null
        );
        repository.saveAll(List.of(parcel));
    }

    @Given("a group {string} has a parcel expiring in {int} days")
    public void a_group_has_a_parcel_expiring_in_days(String ppName, int days) {
        PickupPoint pp = new PickupPoint(ppName.toLowerCase().replace(" ", "-"), ppName, "Address", "08:00-19:00");
        Parcel parcel = new Parcel(
            ParcelId.random(),
            "EXP-IN-" + days,
            "Carrier",
            LocalDate.now().plusDays(days),
            ParcelStatus.AVAILABLE,
            pp,
            null,
            null
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

    @When("I save a parcel with tracking number {string} and status {string}")
    public void i_save_a_parcel_with_tracking_number_and_status(String trackingNumber, String statusStr) {
        Parcel parcel = new Parcel(
            ParcelId.random(),
            trackingNumber,
            "Carrier",
            LocalDate.now().plusDays(5),
            ParcelStatus.valueOf(statusStr),
            DEFAULT_PP,
            null,
            null
        );
        repository.save(parcel);
        // Refresh dashboard view for "Then" steps
        retrievedGroups = useCase.retrieve();
    }
}
