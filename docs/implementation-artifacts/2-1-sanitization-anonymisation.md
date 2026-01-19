# Story 2.1: Sanitization & Anonymisation (Data Minimization)

Status: ready-for-dev

<!-- Note: Validation is optional. Run validate-create-story for quality check before dev-story. -->

## Story

As a **Privacy-Conscious User**,
I want **my sensitive personal data (phone numbers, addresses) removed from the email body**,
so that **only necessary metadata is processed by the AI, respecting my privacy.**

## Acceptance Criteria

### Scenario 1: Phone Number Removal
**Given** an email body containing "Call me at 06 12 34 56 78 regarding the delivery"
**When** the sanitization service runs
**Then** the output should be "Call me at [PHONE_REMOVED] regarding the delivery"
**And** the original phone number must NOT be present in the output

### Scenario 2: Postal Address Removal
**Given** an email body containing "Deliver to 123 Rue de la Paix, 75000 Paris"
**When** the sanitization service runs
**Then** the output should replace the address with "[ADDRESS_REMOVED]" (best effort)

### Scenario 3: Preservation of Tracking Codes
**Given** an email body containing "Your tracking code is ABC-123-XYZ"
**When** the sanitization service runs
**Then** the tracking code "ABC-123-XYZ" MUST remain preserved in the output

### Scenario 4: No Persistence of Raw Data
**Given** a raw email input
**When** the processing pipeline finishes
**Then** the original raw body MUST NOT be saved in any database table

## Tasks / Subtasks

- [ ] **Define Behavior (ATDD)**
  - [ ] Create `backend/src/test/resources/features/privacy/Sanitization.feature`
  - [ ] Define Scenarios for Phone, Address, and Preservation of Tracking Codes
- [ ] **Implement Domain Logic (Pure Java)**
  - [ ] Create `EmailSanitizer` interface in `com.parcelflow.domain.ports.in` (Use Case) or `com.parcelflow.domain.service`
  - [ ] Implement `RegexEmailSanitizer` in `com.parcelflow.domain.logic` (or inside domain if purely logic)
      - *Note:* Since this is pure logic, it belongs in the Domain (Strategy Pattern) or as a Domain Service. Ensure NO Spring dependencies.
  - [ ] Implement Phone Number Regex (International/French format)
  - [ ] Implement Address Regex (Best effort for common patterns)
- [ ] **Verify Architecture Compliance**
  - [ ] Ensure `EmailSanitizer` does not use `java.util.regex` in a way that hurts performance (Pre-compile patterns in static final fields)
  - [ ] Verify 0% Spring dependency in this class

## Dev Notes

### Architecture & Patterns
- **Location:** This logic belongs to the **Domain** (Core). It is a business rule (Privacy Policy).
- **Package:** `com.parcelflow.domain.privacy` or `com.parcelflow.domain.service`.
- **Constraint:** STRICTLY NO SPRING ANNOTATIONS (`@Service`, `@Component`). Use Constructor Injection if needed, but this is likely a stateless service.
- **Instantiation:** The Infrastructure layer (Spring) will instantiate this class as a Bean in a `@Configuration` class (e.g., `DomainConfig`).

### Technical Implementation Hints
- **Regex Performance:** Use `java.util.regex.Pattern` compiled as `static final`.
- **Patterns:**
    - French Mobile: `(0|\+33)[1-9]([-. ]?[0-9]{2}){4}`
    - Address: Look for patterns like `\d+ (rue|avenue|boulevard|impasse|place) ...`

### Testing Strategy
- **Unit Tests:** `RegexEmailSanitizerTest` (JUnit 5) for edge cases.
- **Acceptance Tests:** Cucumber steps covering the main scenarios.

### Project Structure Notes
- **File:** `backend/src/main/java/com/parcelflow/domain/logic/SanitizationService.java` (Suggested)

## Dev Agent Record

### Agent Model Used
Gemini 2.0 Flash

### Debug Log References
- N/A
