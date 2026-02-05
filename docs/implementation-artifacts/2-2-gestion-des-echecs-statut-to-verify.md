# Story 2.2: Gestion des échecs (Statut TO_VERIFY)

Status: ready-for-dev

<!-- Note: Validation is optional. Run validate-create-story for quality check before dev-story. -->

## Story

As a **System**,
I want **to flag parcels as `TO_VERIFY` if extraction fails**,
so that **the user knows a manual check is needed.**

## Acceptance Criteria

1. **Given** an email that doesn't match any known Regex pattern.
2. **When** the processing job runs.
3. **Then** a parcel record is created with status `TO_VERIFY`.
4. **And** the record includes a reference/link to the original email for manual consultation.
5. **And** the failure is logged for monitoring.

## Tasks / Subtasks

- [ ] **Domain**: Update `ParcelStatus` enum and `Parcel` entity (AC: 3)
- [ ] **Application**: Update `ProcessIncomingEmailUseCase` to handle extraction failures (AC: 2, 3)
- [ ] **Infrastructure**: Update `RegexParserAdapter` to return a failure indicator instead of throwing exception (AC: 1, 3)
- [ ] **Infrastructure**: Ensure `qrCodeUrl` or other link points to the source email in Gmail (AC: 4)
- [ ] **Testing**: Create `ExtractionFailure.feature` and implement glue code (AC: 1-5)

## Dev Notes

- **Architecture Pattern**: Respect Hexagonal Architecture. The Domain should define the `TO_VERIFY` status.
- **Source Tree**:
  - `backend/src/main/java/com/parcelflow/domain/model/ParcelStatus.java`
  - `backend/src/main/java/com/parcelflow/application/usecases/ExtractParcelUseCase.java`
  - `backend/src/main/java/com/parcelflow/infrastructure/extraction/RegexParserAdapter.java`
- **Error Handling**: Use RFC 7807 for API errors, but here it's an asynchronous background process, so status mapping is preferred over crashing.

### Project Structure Notes

- Aligned with `backend/` and `frontend/` monorepo structure.
- Status `TO_VERIFY` must be supported by the frontend as well.

### References

- [Source: docs/planning-artifacts/epics.md#Story 2.2]
- [Source: docs/prd.md#FR2.3]
- [Source: docs/architecture.md#4.1 Entités & Value Objects]

## Dev Agent Record

### Agent Model Used

Gemini 2.0 Flash (SM Agent YOLO Mode)

### Debug Log References

### Completion Notes List

### File List
