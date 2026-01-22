Feature: Parcel List MVP
  As a User
  I want to see a flat list of my parcels
  To track my deliveries

  Scenario: Retrieve all active parcels
    Given the repository contains the following active parcels:
      | trackingNumber | status    |
      | SHOES-123      | AVAILABLE |
      | BOOK-456       | AVAILABLE |
    When I request the parcel list
    Then I should receive 2 parcels
    And the parcel "SHOES-123" should be marked as "AVAILABLE"

  Scenario: No parcels available
    Given the repository is empty
    When I request the parcel list
    Then I should receive an empty list
