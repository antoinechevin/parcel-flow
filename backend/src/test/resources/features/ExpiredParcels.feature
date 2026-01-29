Feature: Expired parcels management

  Scenario: A parcel with a past date is marked as expired
    Given a parcel with a deadline on "2026-01-20"
    And today's date is "2026-01-29"
    When I check the parcel status
    Then the status should be "EXPIRED"

  Scenario: Expired parcels appear after available parcels in the dashboard
    Given these parcels are in the system:
      | trackingNumber | carrier    | deadline   | pickupPoint | status    |
      | EXP-001        | Chronopost | 2026-01-20 | Point A     | AVAILABLE |
      | OK-001         | Vinted Go  | 2026-02-05 | Point A     | AVAILABLE |
    And today's date is "2026-01-29"
    When I retrieve the dashboard
    Then the group "Point A" should contain 2 parcels
    And the first parcel should be "OK-001"
    And the second parcel should be "EXP-001"