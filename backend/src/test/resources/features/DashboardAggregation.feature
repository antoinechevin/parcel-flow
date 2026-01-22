Feature: Dashboard Aggregation by Location
  As a User
  I want my parcels grouped by pickup location
  To optimize my collection trips

  Scenario: Grouping parcels by pickup point
    Given these parcels exist:
      | trackingNumber | pickupPoint |
      | SHOES-123      | Relais A    |
      | HAT-789        | Relais A    |
      | BOOK-456       | Shop B      |
    When I retrieve the dashboard
    Then I should see 2 location groups
    And the group "Relais A" should contain 2 parcels
    And the group "Shop B" should contain 1 parcel

  Scenario: Empty dashboard
    Given there are no parcels in the system
    When I retrieve the dashboard
    Then I should see 0 location groups
