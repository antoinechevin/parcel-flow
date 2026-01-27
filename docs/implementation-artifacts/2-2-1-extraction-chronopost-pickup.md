# Story 2.2.1: Extraction de Métadonnées Chronopost / Pickup

Status: done

## Story

As a **User**,
I want **the system to extract the tracking code, carrier, and expiration date from Chronopost emails using Regex**,
so that **structured parcel information is created automatically without relying on AI.**

## Acceptance Criteria

**Given** a raw email text from Chronopost
**When** processed by the `ChronopostPickupExtractionAdapter`
**Then** a `ParcelMetadata` object is returned containing:
    - `trackingNumber`: String (e.g., "XW...")
    - `carrier`: String (e.g., "Chronopost / Pickup")
    - `expirationDate`: Date
    - `pickupLocation`: String
**And** a new `Parcel` entity is created and persisted in the database with the extracted data.

## Tasks / Subtasks

- [x] **Domain Model**
  - [x] Create `ParcelMetadata` record (Domain DTO).
  - [x] Update `Parcel` entity to support all fields.
  - [x] Define `ParcelExtractionPort` interface (Secondary Port).
- [x] **Infrastructure: Regex Adapter**
  - [x] Create `ChronopostPickupExtractionAdapter` implementing `ParcelExtractionPort`.
  - [x] Use Jsoup for HTML parsing and Regex for field extraction.
- [x] **Application Service**
  - [x] Create `ExtractParcelUseCase`.
  - [x] Orchestrate: Receive Email -> Call Extraction Adapter -> Save Parcel.
- [x] **Testing**
  - [x] Unit Test `ChronopostPickupExtractionAdapter`.
  - [x] ATDD: `parcel-extraction.feature`.

## Dev Notes

### Architecture & Tech Stack
- **Jsoup**: For robust HTML traversal.
- **Regex**: Specific patterns for Chronopost/Pickup.
- **Hexagonal**: The Domain is decoupled from extraction logic.

## Dev Agent Record

### Implementation Plan
- Domain: Add `ParcelMetadata`.
- Port: `ParcelExtractionPort` interface.
- Infrastructure: `ChronopostPickupExtractionAdapter`.
- Application: `ExtractParcelUseCase`.
- Tests: Unit tests + ATDD.

### File List
- `backend/src/main/java/com/parcelflow/domain/model/ParcelMetadata.java`
- `backend/src/main/java/com/parcelflow/domain/ports/ParcelExtractionPort.java`
- `backend/src/main/java/com/parcelflow/infrastructure/extraction/ChronopostPickupExtractionAdapter.java`
- `backend/src/main/java/com/parcelflow/application/usecases/ExtractParcelUseCase.java`
- `backend/src/test/java/com/parcelflow/infrastructure/extraction/ChronopostPickupExtractionAdapterTest.java`
- `backend/src/test/java/com/parcelflow/application/usecases/ExtractParcelUseCaseTest.java`
- `backend/src/test/resources/features/parcel-extraction.feature`

### Change Log
- Pivoted from Gemini AI to Regex for reliability and privacy.
- Implemented Chronopost specific parser.
- Verified with unit and ATDD tests.

### Status
Status: done
