# Story 2.2.2: Extraction de Métadonnées Mondial Relay

Status: ready-for-dev

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

- [ ] **Architecture Refactoring**
  - [ ] Update `ParcelExtractionPort.extract()` signature to accept `emailContent` AND `receivedAt` (ZonedDateTime/LocalDate).
  - [ ] Update `ChronopostPickupExtractionAdapter` to respect the new signature (ignore date if absolute date is in mail).
  - [ ] Update `ExtractParcelUseCase` to pass the date from `GmailInboundAdapter`.
- [ ] **Infrastructure: Regex Adapter**
  - [ ] Create `MondialRelayExtractionAdapter` implementing `ParcelExtractionPort`.
  - [ ] Use Jsoup to parse the HTML part of the email.
  - [ ] Implement Regex extraction for tracking number, pin code, and pickup point.
- [ ] **Testing**
  - [ ] Create `MondialRelayExtractionAdapterTest`.
  - [ ] Verify extraction with the provided `mail_mondial_relay.eml`.
  - [ ] Ensure existing Chronopost tests still pass.

## Dev Notes

### Patterns identified in Sample
- **Tracking**: `Votre colis 44795167 est disponible !` (Subject/Body)
- **PIN Code**: Inside a div `<div style="...margin-bottom: 20px;">887498</div>`
- **Location**: `Locker <span.*?> (.*?) .</span>`
- **Deadline**: "DANS (\d+) JOURS VOTRE COLIS REPARTIRA" -> Use this relative value added to `receivedAt`.

### Status
Status: ready-for-dev