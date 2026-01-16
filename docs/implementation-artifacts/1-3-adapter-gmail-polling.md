# Story 1.3: Adapter Gmail & Polling des Emails

Status: review

<!-- Note: Validation is optional. Run validate-create-story for quality check before dev-story. -->

## Story

As a User,
I want the system to automatically connect to my Gmail and identify unread emails related to deliveries,
so that I don't have to manually forward or input tracking information.

## Acceptance Criteria

1. **Given** valid Gmail OAuth2 credentials, **When** the application starts, **Then** it can authenticate successfully with the Gmail API.
2. **Given** unread emails with subjects containing "colis" or "livraison", **When** the Polling Job runs, **Then** the system identifies these emails.
3. **Given** an identified email, **When** it is processed, **Then** it is marked as "processed" (or read/archived in Gmail) to avoid duplicate processing.
4. **Given** the polling configuration, **When** the configured interval elapses, **Then** the job triggers automatically.
5. **Given** the `GmailProvider` adapter, **When** implemented, **Then** it resides in the `infrastructure` layer and implements a Domain Port.

## Tasks / Subtasks

- [ ] **Domain Layer (Pure Java)**
  - [x] Define `EmailMetadata` record (id, subject, snippet, date) in `domain/model`.
  - [x] Define `EmailProviderPort` interface in `domain/port/out` with methods `fetchUnreadDeliveryEmails()` and `markAsProcessed(String id)`.
  - [x] Define `ScanEmailsUseCase` interface in `domain/port/in`.
  - [x] Implement `ScanEmailsService` in `application/service` that orchestrates the fetching and marking.
- [ ] **Infrastructure Layer (Gmail Adapter)**
  - [x] Add Google Gmail API dependencies (google-api-client, google-api-services-gmail) to `backend/infrastructure/pom.xml`.
  - [x] Implement `GmailAdapter` in `infrastructure/adapter/gmail` implementing `EmailProviderPort`.
  - [x] Configure `GmailConfig` to load OAuth2 credentials from environment variables (`GMAIL_CLIENT_ID`, `GMAIL_CLIENT_SECRET`, `GMAIL_REFRESH_TOKEN`).
- [ ] **Infrastructure Layer (Job)**
  - [x] Implement `GmailPollingJob` in `infrastructure/in/job` using `@Scheduled` (configurable interval).
  - [x] Inject `ScanEmailsUseCase` into the Job.
- [ ] **Tests**
  - [x] Create `email_polling.feature` in `backend/infrastructure/src/test/resources/features`.
  - [x] Implement Cucumber Step Definitions mocking the Gmail API (using Mockito or WireMock/MockServer if integration level).
  - [x] Ensure ArchUnit tests pass (no new violations).

## Dev Notes

- **Architecture Compliance**:
  - The Domain MUST NOT depend on the Google API libraries. It only defines the `EmailProviderPort` interface.
  - The `GmailAdapter` (Infrastructure) converts Google API objects to Domain `EmailMetadata` objects.
- **Gmail API**:
  - Use `users.messages.list` with query `subject:(colis OR livraison) is:unread`.
  - Use `users.messages.modify` to remove `UNREAD` label.
- **Security**:
  - Do NOT commit credentials. Use `application.properties` referencing Environment Variables.
- **Testing**:
  - For Cucumber tests, mock the `EmailProviderPort` behavior to simulate finding emails without calling real Gmail.
  - If possible, create an integration test that uses a Mock Web Server to simulate the Gmail REST API, but simple Mockito of the Port is acceptable for this stage if the Adapter logic is tested separately.

### Project Structure Notes

- **Module**: `backend`
- **Packages**:
  - `com.parcelflow.domain.model`
  - `com.parcelflow.domain.port.out`
  - `com.parcelflow.application.service`
  - `com.parcelflow.infrastructure.adapter.gmail`
  - `com.parcelflow.infrastructure.in.job`

### References

- [Source: docs/planning-artifacts/epics.md#story-13-adapter-gmail--polling-des-emails](docs/planning-artifacts/epics.md)
- [Source: docs/architecture.md#4-stack-technique--décisions](docs/architecture.md)

## Dev Agent Record

### Agent Model Used

Gemini 2.0 Flash

### Debug Log References

- Mockito cannot mock `Gmail` class due to version mismatch (Fixed by downgrading `google-api-client` to 1.35.2).
- `GmailPollingJob` context failure due to missing `ScanEmailsUseCase` bean (Fixed by creating `BeanConfiguration`).
- Cucumber Step definition `@MockBean` injection issue (Fixed by moving `@MockBean` to `CucumberSpringConfiguration`).

### Completion Notes List

- Implemented Domain logic: `EmailMetadata`, `EmailProviderPort`, `ScanEmailsUseCase`, `ScanEmailsService`.
- Added Google Gmail API dependencies.
- Implemented Infrastructure Adapter: `GmailAdapter` using `google-api-client`.
- Implemented Configuration: `GmailConfig` for OAuth2 setup.
- Implemented Polling Job: `GmailPollingJob` with `@Scheduled`.
- Implemented Tests: Unit tests for Domain, Application, Adapter, Job. Cucumber Feature test `email_polling.feature`.
- Added `BeanConfiguration` to wire Application beans in Infrastructure.

### File List

- backend/domain/src/main/java/com/parcelflow/domain/model/EmailMetadata.java
- backend/domain/src/main/java/com/parcelflow/domain/port/out/EmailProviderPort.java
- backend/domain/src/main/java/com/parcelflow/domain/port/in/ScanEmailsUseCase.java
- backend/domain/src/test/java/com/parcelflow/domain/model/EmailMetadataTest.java
- backend/application/src/main/java/com/parcelflow/application/service/ScanEmailsService.java
- backend/application/src/test/java/com/parcelflow/application/service/ScanEmailsServiceTest.java
- backend/infrastructure/pom.xml
- backend/infrastructure/src/main/java/com/parcelflow/infrastructure/adapter/gmail/GmailAdapter.java
- backend/infrastructure/src/main/java/com/parcelflow/infrastructure/config/GmailConfig.java
- backend/infrastructure/src/main/java/com/parcelflow/infrastructure/config/BeanConfiguration.java
- backend/infrastructure/src/main/java/com/parcelflow/infrastructure/in/job/GmailPollingJob.java
- backend/infrastructure/src/test/java/com/parcelflow/infrastructure/adapter/gmail/GmailAdapterTest.java
- backend/infrastructure/src/test/java/com/parcelflow/infrastructure/in/job/GmailPollingJobTest.java
- backend/infrastructure/src/test/resources/features/email_polling.feature
- backend/infrastructure/src/test/java/com/parcelflow/infrastructure/steps/EmailPollingSteps.java
- backend/infrastructure/src/test/java/com/parcelflow/infrastructure/CucumberSpringConfiguration.java
