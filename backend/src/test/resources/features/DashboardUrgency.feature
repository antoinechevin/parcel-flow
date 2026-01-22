Feature: Dashboard Urgency and Sorting
  As a User
  I want to see urgency levels and have my dashboard sorted by urgency
  To prioritize my collection trips

  Scenario: Sorting groups by urgency
    Given a group "URGENT" has a parcel expiring tomorrow
    And a group "SOON" has a parcel expiring in 10 days
    When I retrieve the dashboard
    Then "URGENT" should appear before "SOON"
