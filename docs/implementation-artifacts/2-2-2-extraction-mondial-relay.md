# Story 2.2.2: Extraction de Métadonnées Mondial Relay

Status: in-progress

## Story

As a **User**,
I want **the system to extract the tracking code, carrier, and expiration date from Mondial Relay emails using Regex**,
so that **structured parcel information is created automatically for this provider.**

## Acceptance Criteria

**Given** a raw email text from Mondial Relay (sample provided in `mail_mondial_relay.eml`)
**And** the email reception date
**When** processed by the `MondialRelayExtractionAdapter`
**Then** a `ParcelMetadata` object is returned containing:
    - `trackingNumber`: String ("44795167")
    - `carrier`: String ("Mondial Relay")
    - `expirationDate`: Date (Reception Date + "DANS X JOURS" extracted from mail)
    - `pickupLocation`: String ("LOCKER 24/7 LA CERISE BLEUE BESSENAY")
**And** a new `Parcel` entity is created and persisted in the database.

## Tasks / Subtasks

- [x] **Architecture Refactoring**
  - [x] Update `ParcelExtractionPort.extract()` signature to accept `emailContent` AND `receivedAt` (ZonedDateTime/LocalDate).
  - [x] Update `ChronopostPickupExtractionAdapter` to respect the new signature (ignore date if absolute date is in mail).
  - [x] Update `ExtractParcelUseCase` to pass the date from `GmailInboundAdapter`.
- [x] **Infrastructure: Regex Adapter**
  - [x] Create `MondialRelayExtractionAdapter` implementing `ParcelExtractionPort`.
  - [x] Use Jsoup to parse the HTML part of the email.
  - [x] Implement Regex extraction for tracking number, pin code, and pickup point.
- [x] **Testing**
  - [x] Create `MondialRelayExtractionAdapterTest`.
  - [x] Verify extraction with the provided `mail_mondial_relay.eml`.
  - [x] Ensure existing Chronopost tests still pass.

## Dev Notes

### Patterns identified in Sample
- **Tracking**: `Votre colis 44795167 est disponible !` (Subject/Body)
- **PIN Code**: Inside a div `<div style="...margin-bottom: 20px;">887498</div>`
- **Location**: `Locker <span.*?> (.*?) .</span>`
- **Deadline**: "DANS (\d+) JOURS VOTRE COLIS REPARTIRA" -> Use this relative value added to `receivedAt`.

### Status
Status: done

## Dev Agent Record

### Completion Notes
- Refactored `ParcelExtractionPort` to include `receivedAt` date for relative date calculation.
- Implemented `MondialRelayExtractionAdapter` using Jsoup and Regex.
- Handled potential encoding issues with Regex by using `sed` injection during file creation.
- Verified implementation with `MondialRelayExtractionAdapterTest` using a real email sample.
- Ensured regression testing passed for `ChronopostPickupExtractionAdapter` and `ExtractParcelUseCase`.

### Code Review (2026-01-27)
- **Reviewer**: AI Senior Developer
- **Outcome**: 3 Medium issues found and fixed.
- **Fixes Applied**:
    - **Safety**: Added bounds check (0-365) and `Long` parsing for expiration days to prevent overflow/exceptions.
    - **Maintainability**: Extracted "Mondial Relay Point" fallback to constant.
    - **Accuracy**: Implemented explicit timezone conversion to `Europe/Paris` for day calculation to avoid UTC offset errors.

### File List
- `backend/src/main/java/com/parcelflow/domain/ports/ParcelExtractionPort.java`
- `backend/src/main/java/com/parcelflow/infrastructure/extraction/ChronopostPickupExtractionAdapter.java`
- `backend/src/main/java/com/parcelflow/infrastructure/extraction/MondialRelayExtractionAdapter.java`
- `backend/src/main/java/com/parcelflow/application/usecases/ExtractParcelUseCase.java`
- `backend/src/main/java/com/parcelflow/infrastructure/api/DebugExtractionController.java`
- `backend/src/main/java/com/parcelflow/infrastructure/api/MailScanController.java`
- `backend/src/test/java/com/parcelflow/infrastructure/extraction/MondialRelayExtractionAdapterTest.java`
- `backend/src/test/java/com/parcelflow/infrastructure/extraction/ChronopostPickupExtractionAdapterTest.java`
- `backend/src/test/java/com/parcelflow/application/usecases/ExtractParcelUseCaseTest.java`
- `backend/src/test/java/com/parcelflow/steps/ParcelExtractionSteps.java`