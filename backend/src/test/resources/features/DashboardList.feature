Feature: Dashboard List
  As a User
  I want to see a list of my active parcels on my phone
  So that I can track what I need to pick up

  Scenario: Retrieve active parcels
    Given the following parcels exist:
      | trackingNumber | status      | daysRelativeToNow |
      | SHOES-123      | AVAILABLE   | 1                 |
      | BOOK-456       | AVAILABLE   | 2                 |
      | HAT-789        | PICKED_UP   | -1                |
    When I request the dashboard parcel list
    Then I should receive 3 parcels
    And the parcel with tracking number "SHOES-123" should be "AVAILABLE"
    And the parcel with tracking number "HAT-789" should be "PICKED_UP"