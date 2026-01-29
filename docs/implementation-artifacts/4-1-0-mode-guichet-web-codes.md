# Story 4.1.0: Mode Guichet Web (Codes & QR Codes)

Status: todo

## Story

As a **User (Web App)**,
I want **to see my withdrawal codes and QR codes for Chronopost, Vinted Go, and Mondial Relay in a dedicated "Counter Mode" view**,
so that **I can easily retrieve my parcels tonight even without the mobile app's native features.**

## Acceptance Criteria

**Backend & Extraction:**
- [x] **Domain**: Add `pickupCode` and `qrCodeUrl` fields to `Parcel` and `ParcelMetadata`.
- [x] **Extraction Chronopost**: 
    - Extract the "PIN code" (e.g., `611553`) into `pickupCode`.
    - Extract the AztecCode image URL into `qrCodeUrl`.
- [x] **Extraction Vinted Go**: 
    - Extract the 6-character code (e.g., `E32782`) into `pickupCode`.
    - Extract the QR Code image URL into `qrCodeUrl`.
- [x] **Extraction Mondial Relay**: 
    - Extract the "Code PIN" (6 digits) into `pickupCode`.
- [x] **API**: Ensure `pickupCode` and `qrCodeUrl` are returned by the `GET /api/parcels` endpoint.

**Frontend (Web Friendly):**
- [x] **UI**: Add a "Mode Guichet" button on each parcel card.
- [x] **View**: Create a dedicated View or Modal displaying:
    - Carrier name and Tracking Number.
    - `pickupCode` in **huge** characters (centered).
    - **QR Code Image**: If `qrCodeUrl` is present, display the image from the URL.
    - **Fallback QR**: If no `qrCodeUrl` but a `pickupCode` is present, generate a QR code locally from the `pickupCode` (for carriers where it's applicable).
- [x] **Visuals**: High contrast mode (White background, Black text) to ensure readability.

## Tasks / Subtasks

- [x] **Backend**
  - [x] Update `Parcel` and `ParcelMetadata` models.
  - [x] Implement `pickupCode` and `qrCodeUrl` extraction in `ChronopostPickupExtractionAdapter`.
  - [x] Implement `pickupCode` and `qrCodeUrl` extraction in `VintedGoExtractionAdapter`.
  - [x] Implement `pickupCode` extraction in `MondialRelayExtractionAdapter`.
  - [x] Update `ExtractParcelUseCase` and Mapping logic.
- [x] **Frontend**
  - [x] Add `pickupCode` and `qrCodeUrl` to Frontend `Parcel` type.
  - [x] Create `GuichetModeModal` component.
  - [x] Use `react-native-qrcode-svg` for local generation fallback.
  - [x] Handle image loading for `qrCodeUrl`.

## Dev Notes
- **Security**: These codes are sensitive. They are only displayed when the user explicitly enters "Mode Guichet".
- **Tonight's Constraint**: Focus on extraction of existing URLs from emails first as it's the most reliable for current provider formats.

## Dev Agent Record

### Implementation Plan
- Backend: Update models, extractors, and use cases.
- Tests: Add assertions for new fields in extraction tests.
- Frontend: Install QR code libraries, create Modal, update ParcelCard.
- Verification: Fix test regressions in NominalFlow and ParcelCard tests.

### Completion Notes
- Extraction logic added for Chronopost (AztecCode + PIN), Vinted Go (QR URL + PIN), and Mondial Relay (PIN).
- Local QR generation fallback using `react-native-qrcode-svg`.
- High contrast "Mode Guichet" UI implemented.
- All backend and frontend tests passing.

### File List
- `backend/src/main/java/com/parcelflow/domain/model/Parcel.java`
- `backend/src/main/java/com/parcelflow/domain/model/ParcelMetadata.java`
- `backend/src/main/java/com/parcelflow/application/usecases/ExtractParcelUseCase.java`
- `backend/src/main/java/com/parcelflow/application/usecases/RetrieveDashboardUseCase.java`
- `backend/src/main/java/com/parcelflow/infrastructure/extraction/ChronopostPickupExtractionAdapter.java`
- `backend/src/main/java/com/parcelflow/infrastructure/extraction/VintedGoExtractionAdapter.java`
- `backend/src/main/java/com/parcelflow/infrastructure/extraction/MondialRelayExtractionAdapter.java`
- `backend/src/main/java/com/parcelflow/infrastructure/bootstrap/DataInitializer.java`
- `backend/src/test/java/com/parcelflow/application/RetrieveDashboardUseCaseTest.java`
- `backend/src/test/java/com/parcelflow/application/usecases/ExtractParcelUseCaseTest.java`
- `backend/src/test/java/com/parcelflow/infrastructure/extraction/ChronopostPickupExtractionAdapterTest.java`
- `backend/src/test/java/com/parcelflow/infrastructure/extraction/VintedGoExtractionAdapterTest.java`
- `backend/src/test/java/com/parcelflow/infrastructure/extraction/MondialRelayExtractionAdapterTest.java`
- `backend/src/test/java/com/parcelflow/infrastructure/api/ParcelControllerTest.java`
- `frontend/src/types/index.ts`
- `frontend/src/components/GuichetModeModal.tsx`
- `frontend/src/components/ParcelCard.tsx`
- `frontend/src/components/LocationGroupCard.tsx`
- `frontend/app/index.tsx`
- `frontend/jest.setup.js`
- `frontend/package.json`
- `frontend/src/NominalFlow.test.tsx`
- `frontend/src/components/ParcelCard.test.tsx`

### Change Log
- Added `pickupCode` and `qrCodeUrl` to full-stack pipeline.
- Implemented specialized extraction per provider.
- Created "Mode Guichet" modal for counter assistance.
- Addressed code review findings: robust PIN extraction, QR error handling, and test specificity.

## Status
Status: done
