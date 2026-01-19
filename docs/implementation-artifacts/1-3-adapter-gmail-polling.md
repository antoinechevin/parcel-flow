# Story 1.3: Adapter Gmail & Polling des Emails

Status: ready-for-dev

## Story

As a **System**,
I want **to periodically poll Gmail for delivery-related emails**,
so that **I can trigger the automated extraction process for every new parcel identified.**

## Acceptance Criteria

1. **Gmail Query Integration**: The system queries Gmail using: `subject:(colis OR livraison) is:unread`.
2. **Infrastructure Adapter**: `GmailInboundAdapter` implements a (yet to be defined) internal port or directly calls the Use Case.
3. **Orchestration**: For each message found, the system invokes `ProcessIncomingEmailUseCase`.
4. **Deduplication Strategy**: 
    - The email is marked as "READ" immediately after processing.
    - (Optional but recommended) A "PARCEL-FLOW" label is added to the email.
5. **Resilience**: The polling job handles Gmail API quotas and transient network errors (Retries).

## Tasks / Subtasks

- [ ] **Infrastructure: Gmail Adapter**
  - [ ] Implement `GmailInboundAdapter` in `com.parcelflow.infrastructure.mail`.
  - [ ] Configure `Gmail` SDK with OAuth2 (Credentials from Env Vars).
- [ ] **Application: Scheduling**
  - [ ] Implement `EmailPollingJob` with `@Scheduled`.
  - [ ] Inject `ProcessIncomingEmailUseCase` into the job.
- [ ] **Testing**
  - [ ] Mock Gmail API response in integration tests.
  - [ ] Verify that the Use Case is called exactly once per unread email.

## Dev Notes

### Architecture & Patterns
- **Entry Point**: This is a **Primary Adapter** (driven by time/external event).
- **Domain Link**: It must NOT contain business logic, only the transformation from Gmail Message -> Use Case Input.

### Security
- Ensure `spring.security.oauth2` is properly configured in `backend/pom.xml`.

### References
- [Source: docs/architecture.md#Section 2] - High Level Architecture (Gmail -> MailAdapter -> UC_Process)

## Dev Agent Record

### Agent Model Used
Gemini 2.0 Flash
