Feature: API Security Access Control

  Scenario: Accessing dashboard without API Key is forbidden
    When I request the dashboard without a security header
    Then I should receive a 401 Unauthorized response

  Scenario: Accessing dashboard with wrong API Key is forbidden
    When I request the dashboard with header "X-API-KEY" set to "wrong-password"
    Then I should receive a 401 Unauthorized response

  Scenario: Accessing dashboard with correct API Key is allowed
    Given the system security key is "test-api-key"
    When I request the dashboard with header "X-API-KEY" set to "test-api-key"
    Then I should receive a 200 OK response
