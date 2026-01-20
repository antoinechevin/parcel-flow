# Story 1.4: Polling Job & Orchestration

Status: ready-for-dev

## Story

As a **System**,
I want **to periodically trigger the mail checking process**,
so that **new delivery emails are automatically processed without manual intervention.**

## Acceptance Criteria

1. **Scheduling**: A Spring `@Scheduled` task runs at a configurable interval (default: every 15 minutes).
2. **Orchestration**: The job calls `MailSourcePort.fetchUnreadEmails()`.
3. **Processing**: For each email fetched, it calls the `ProcessIncomingEmailUseCase` (to be fully implemented in later stories, use a mock/stub for now).
4. **Cleanup**: After successful processing, the email is marked as read via `MailSourcePort.markAsRead()`.
5. **Observability**: The job logs the number of emails found and processed.

## Tasks / Subtasks

- [ ] **Application: Scheduling**
  - [ ] Create `EmailPollingJob` in the application layer.
  - [ ] Configure the cron/fixedDelay in `application.yml`.
- [ ] **Orchestration Logic**
  - [ ] Inject `MailSourcePort` and `ProcessIncomingEmailUseCase` (or its stub).
  - [ ] Implement the loop logic with error handling (if one email fails, continue with others).
- [ ] **Testing**
  - [ ] Unit test the Job by mocking the Port and the Use Case.
  - [ ] Verify that `markAsRead` is only called if the Use Case succeeds.

## Dev Notes

### Resilience
- Use a simple `try-catch` inside the loop to ensure a single malformed email doesn't stop the whole polling process.

### References
- [Source: docs/architecture.md] - Primary Adapters (Time-driven)

## Dev Agent Record

### Agent Model Used
Gemini 2.0 Flash
