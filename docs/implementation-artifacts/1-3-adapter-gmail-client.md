# Story 1.3: Adapter Gmail (Client d'Infrastructure)

Status: done

## Story

As a **Developer**,
I want **a Gmail client that can list and read unread delivery emails**,
so that **the system can fetch data from the outside world using a standardized Port.**

## Acceptance Criteria

1. **Port Definition**: A `MailSourcePort` interface exists in the application/domain layer with methods:
    - `MailFetchResult fetchEmails(ZonedDateTime since, String query)`
2. **Result Object**: `MailFetchResult` contains the list of `InboundEmail` and the `newWatermark` (timestamp of the most recent email fetched).
3. **Infrastructure Adapter**: `GmailInboundAdapter` implements `MailSourcePort`.
4. **Connectivity**: The adapter successfully connects to Gmail API using OAuth2 credentials.
5. **Filtering**: The adapter correctly applies the query `subject:(colis OR livraison)` combined with the `since` timestamp.
6. **Security**: Credentials/Secrets are NEVER hardcoded (use `@ConfigurationProperties` and Env Vars).

## Tasks / Subtasks

- [x] **Port Definition**
  - [x] Create `InboundEmail` DTO in Domain.
  - [x] Create `MailFetchResult` DTO.
  - [x] Create `MailSourcePort` interface.
- [x] **Infrastructure: Gmail Adapter**
  - [x] Add `google-api-client` and `google-oauth-client` dependencies.
  - [x] Implement `GmailInboundAdapter` using the watermark logic.
  - [x] Setup OAuth2 configuration (Client ID, Secret, Refresh Token).
- [x] **Testing**
  - [x] ATDD: Implement Cucumber glue code for `mail-adapter.feature`.
  - [x] Integration test with a Mocked Gmail Service to verify filtering and watermark update.

## Dev Notes

### Architecture
- This is a **Secondary Adapter** (Client). It implements a Port defined by the application.
- `InboundEmail` should be a simple record containing: `id`, `subject`, `body`, `sender`.

### Security
- **WARNING**: Do not commit `credentials.json` or any tokens. Use a `.env.example` file to document required variables.

### References
- [Source: docs/architecture.md] - Secondary Adapters

## Dev Agent Record



### Agent Model Used

Gemini 2.0 Flash



### Implementation Summary

- **Domain**: Created `InboundEmail` and `MailFetchResult` records. Defined `MailSourcePort`.

- **Infrastructure**: 

    - Added Google API dependencies.

    - Implemented `GmailInboundAdapter` with `after:TIMESTAMP` query filtering for efficient polling.

    - Configured `GmailConfig` for OAuth2 using Refresh Token.

- **Testing**:

    - implemented `MailAdapterSteps` for Cucumber ATDD.

    - Added `GmailInboundAdapterTest` (Unit Test with Mockito RETURNS_DEEP_STUBS for Gmail API).

    - All 20 tests (18 Cucumber + 2 Unit) are GREEN.



### Decisions Made



- Used `InternalDate` from Gmail Message for `receivedAt` to ensure consistency with the `after` query filter.



- Simplified body extraction using `snippet` for this story; full body parsing can be refined in future stories if needed.



- Centralized test configuration in `CucumberConfiguration` to maintain compatibility with existing tests.







### Code Review Improvements (Adversarial Review)



- **Security**: Added validation for OAuth2 credentials in `GmailConfig` to prevent runtime failures with missing configuration.



- **Architecture**: Created `MailSourceException` in domain layer to map infrastructure-specific IO issues, maintaining domain purity.



- **Code Quality**: Extracted `DEFAULT_DELIVERY_QUERY` as a constant in `GmailInboundAdapter`.



- **Test Quality**: Refactored `GmailInboundAdapterTest` to avoid `RETURNS_DEEP_STUBS` and added explicit error handling tests.







### Change Log



- `backend/src/main/java/com/parcelflow/domain/model/InboundEmail.java`: Added domain record.



- `backend/src/main/java/com/parcelflow/domain/model/MailFetchResult.java`: Added domain record.



- `backend/src/main/java/com/parcelflow/domain/ports/MailSourcePort.java`: Added port interface.



- `backend/src/main/java/com/parcelflow/domain/exception/MailSourceException.java`: Added custom domain exception.



- `backend/src/main/java/com/parcelflow/infrastructure/mail/GmailInboundAdapter.java`: Implemented adapter with refined error mapping and constants.



- `backend/src/main/java/com/parcelflow/infrastructure/config/GmailConfig.java`: Implemented configuration with validation.



- `backend/src/test/java/com/parcelflow/infrastructure/mail/GmailInboundAdapterTest.java`: Added unit tests with robust mocking.




