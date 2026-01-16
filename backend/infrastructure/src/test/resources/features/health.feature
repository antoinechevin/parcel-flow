Feature: Health Check & Architecture Initialization

  Scenario: Verify Backend Bootstrap
    Given the Spring Boot application context is loaded
    When I request the status from the "health-check" endpoint
    Then I should receive a 200 OK response
    And the response body should contain "Parcel-Flow Backend is Running"

  Scenario: Verify Domain Isolation (Hexagonal Purity)
    Given the project structure
    When I analyze the dependencies of the "domain" module
    Then it should NOT depend on "spring-boot-starter"
    And it should NOT depend on "jakarta.persistence"
    And it should only depend on standard Java libraries
