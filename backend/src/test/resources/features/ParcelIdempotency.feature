Feature: Parcel Repository Idempotency

  Scenario: Saving a duplicate parcel updates the existing one
    Given there are no parcels in the system
    When I save a parcel with tracking number "DUP-TEST" and status "AVAILABLE"
    And I save a parcel with tracking number "DUP-TEST" and status "PICKED_UP"
    Then I should receive 1 parcels
    And the parcel with tracking number "DUP-TEST" should be "PICKED_UP"
