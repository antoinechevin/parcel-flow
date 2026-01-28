Feature: Email Polling Orchestration

  As a System
  I want to periodically check for new emails from configured providers
  So that parcels are automatically extracted and processed

  Scenario: Targeted polling iterates over all providers using the registry
    Given the following providers are configured in the registry:
      | name          | query                |
      | Chronopost    | from:chrono          |
      | MondialRelay  | from:mondial         |
    And the watermark for "Chronopost" is "2023-10-01T10:00:00Z"
    And the watermark for "MondialRelay" is "2023-10-01T11:00:00Z"
    When the polling orchestrator runs
    Then the mail source should be called for "Chronopost" with query "from:chrono" and watermark "2023-10-01T10:00:00Z"
    And the mail source should be called for "MondialRelay" with query "from:mondial" and watermark "2023-10-01T11:00:00Z"

  Scenario: Watermark is updated after successful fetch
    Given the provider "Vinted" is configured with query "from:vinted"
    And the current watermark for "Vinted" is "2023-10-01T09:00:00Z"
    And the mail source returns a new watermark "2023-10-01T09:15:00Z" for "Vinted"
    When the polling orchestrator runs
    Then the watermark for "Vinted" should be saved as "2023-10-01T09:15:00Z"

  Scenario: Error in one provider does not stop others
    Given the following providers are configured in the registry:
      | name          | query                |
      | FaultyProvider| from:faulty          |
      | GoodProvider  | from:good            |
    And the mail source throws an error for "FaultyProvider"
    When the polling orchestrator runs
    Then the mail source should be called for "GoodProvider" with query "from:good"
