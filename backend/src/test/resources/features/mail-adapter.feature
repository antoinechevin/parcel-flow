Feature: Mail Adapter Watermarking
  As a system,
  I want to fetch delivery emails received after a specific timestamp.

  Background:
    Given the mail service contains these emails:
      | id | sender                         | subject               | receivedAt           |
      | 1  | chronopost@network1.pickup.fr  | Colis Arrivé          | 2026-01-22T10:00:00Z |
      | 2  | chronopost@network1.pickup.fr  | Livraison prévue      | 2026-01-22T10:10:00Z |
      | 3  | random@gmail.com               | Autre sujet           | 2026-01-22T10:20:00Z |

  Scenario: Watermark updates to the most recent email timestamp from authorized sender
    Given the last processed watermark was "2026-01-22T09:00:00Z"
    When I fetch delivery emails
    Then I should receive 2 emails
    And the new watermark should be "2026-01-22T10:10:00Z"

  Scenario: Watermark remains unchanged when no new emails are found from authorized sender
    Given the last processed watermark was "2026-01-22T11:00:00Z"
    When I fetch delivery emails
    Then I should receive 0 emails
    And the new watermark should still be "2026-01-22T11:00:00Z"
