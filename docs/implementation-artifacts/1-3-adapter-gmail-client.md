# Story 1.3: Adapter Gmail (Client d'Infrastructure)

Status: ready-for-dev

## Story

As a **Developer**,
I want **a Gmail client that can list and read unread delivery emails**,
so that **the system can fetch data from the outside world using a standardized Port.**

## Acceptance Criteria

1. **Port Definition**: A `MailSourcePort` interface exists in the application/domain layer with methods:
    - `List<InboundEmail> fetchUnreadEmails(String query)`
    - `void markAsRead(String messageId)`
2. **Infrastructure Adapter**: `GmailInboundAdapter` implements `MailSourcePort`.
3. **Connectivity**: The adapter successfully connects to Gmail API using OAuth2 credentials.
4. **Filtering**: The adapter correctly applies the query `subject:(colis OR livraison) is:unread`.
5. **Security**: Credentials/Secrets are NEVER hardcoded (use `@ConfigurationProperties` and Env Vars).

## Tasks / Subtasks

- [ ] **Port Definition**
  - [ ] Create `InboundEmail` DTO in Domain.
  - [ ] Create `MailSourcePort` interface.
- [ ] **Infrastructure: Gmail Adapter**
  - [ ] Add `google-api-client` and `google-oauth-client` dependencies.
  - [ ] Implement `GmailInboundAdapter`.
  - [ ] Setup OAuth2 configuration (Client ID, Secret, Refresh Token).
- [ ] **Testing**
  - [ ] Integration test with a Mocked Gmail Service to verify filtering logic.

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