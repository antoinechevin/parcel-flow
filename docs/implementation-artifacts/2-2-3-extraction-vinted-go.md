# Story 2.2.3: Extraction de Métadonnées - Vinted Go

**Epic:** 2 - Le Cœur d'Extraction (Regex) & Protection de la Vie Privée
**Status:** Review
**Priorité:** High

## Description
En tant qu'utilisateur,
Je veux que le système extraie automatiquement les informations de retrait depuis les emails **Vinted Go**,
Afin de ne pas avoir à chercher ces informations manuellement dans mes mails.

## Acceptance Criteria

### 1. Détection du Provider
**Given** un email entrant provenant de `no-reply@vinted.com`
**When** le système analyse les métadonnées de l'email
**Then** il identifie le provider comme étant `VINTED_GO`.

### 2. Extraction des Données (Nominal)
**Given** un email de notification de disponibilité Vinted Go (ex: `mail_vinted_go.eml`)
**When** le système applique la stratégie d'extraction Regex pour Vinted Go
**Then** les informations suivantes sont extraites avec succès :
  - **Code de retrait :** "E32782" (Pattern: `saisis le code suivant : <b>(.*?)</b>`)
  - **Date limite :** "2025-12-17" (Pattern: `À retirer avant le.*?<b>(\d{2}/\d{2}/\d{4})</b>`)
  - **Lieu :** "Les Casiers Des Saveurs, 210 Route Du Bourg, Sourcieux Les Mines"
  - **Numéro de suivi :** "1764156123430443"

### 3. Gestion des formats inconnus
**Given** un email Vinted Go dont la structure HTML a changé
**When** l'extraction échoue pour le Code ou la Date
**Then** le colis est créé avec le statut `TO_VERIFY`
**And** l'email brut est lié pour référence manuelle.

## Technical Notes

### Parsing Strategy (Vinted Go)
Basé sur l'analyse de `mail_vinted_go.eml` :
- **Subject Trigger:** "Il est temps de récupérer ton colis !"
- **Sender:** `no-reply@vinted.com`

**Regex Candidates:**
- **Code Retrait:** `saisis le code suivant\s*:\s*<b>([A-Z0-9]+)</b>`
- **Deadline:** `retirer avant le\s*</div>\s*<div.*?>\s*<b>(\d{2}/\d{2}/\d{4})</b>` (Attention aux sauts de ligne HTML)
- **Tracking Number:** `Numéro de suivi\s*:\s*<a.*?>(.*?)</a>`

### Architecture Elements
- **Component:** `VintedGoExtractionAdapter` implements `ParcelExtractionPort`
- **Location:** `backend/src/main/java/com/parcelflow/infrastructure/extraction/`
- **Test Resource:** `backend/src/test/resources/emails/mail_vinted_go.eml`

## Tasks/Subtasks

- [x] Task 1: Create Gherkin Feature File
    - [x] Create `backend/src/test/resources/features/extraction_vinted_go.feature`
- [x] Task 2: Implement VintedGoExtractionAdapter (TDD)
    - [x] Create unit test `backend/src/test/java/com/parcelflow/infrastructure/extraction/VintedGoExtractionAdapterTest.java` (Red)
    - [x] Implement `VintedGoExtractionAdapter` with Jsoup/Regex logic (Green)
    - [x] Refactor and ensure all acceptance criteria are met (Refactor)
- [x] Task 3: Integration & Validation
    - [x] Verify adapter is registered and used by the system
    - [x] Run full regression suite

## Definition of Done
- [x] Gherkin Feature file created (`src/test/resources/features/extraction_vinted_go.feature`)
- [x] Unit Tests passing with `mail_vinted_go.eml` fixture
- [x] Domain implementation pure Java (No html parsers libraries if possible, simple Regex preferred per constraints)
- [x] Integration test verification

## Dev Agent Record

### Implementation Notes
- Created `VintedGoExtractionAdapter` in Infrastructure layer using Jsoup and Regex.
- Added `pickupCode` field to `Parcel` and `ParcelMetadata` (Domain refactoring).
- Created `CompositeParcelExtractionAdapter` to handle multiple strategies properly in Spring Context (Integration).
- Validated with Gherkin feature `extraction_vinted_go.feature`.
- Ensured all regression tests pass after Domain changes.

### File List
- `backend/src/main/java/com/parcelflow/infrastructure/extraction/VintedGoExtractionAdapter.java`
- `backend/src/main/java/com/parcelflow/infrastructure/extraction/CompositeParcelExtractionAdapter.java`
- `backend/src/main/java/com/parcelflow/domain/model/ParcelMetadata.java`
- `backend/src/main/java/com/parcelflow/domain/model/Parcel.java`
- `backend/src/main/java/com/parcelflow/domain/model/ParcelStatus.java`
- `backend/src/test/resources/features/extraction_vinted_go.feature`
- `backend/src/test/java/com/parcelflow/steps/VintedGoExtractionSteps.java`
- `backend/src/test/java/com/parcelflow/domain/model/ParcelMetadataTest.java`
- `backend/src/test/java/com/parcelflow/application/usecases/ExtractParcelUseCaseTest.java`
- `backend/src/test/java/com/parcelflow/steps/DashboardSteps.java`
- `backend/src/test/java/com/parcelflow/domain/model/ParcelTest.java`
- `backend/src/test/java/com/parcelflow/domain/DomainModelingTest.java`
- `backend/src/test/java/com/parcelflow/infrastructure/extraction/VintedGoExtractionAdapterTest.java`

### Fix Log (Code Review 2026-01-27)
- Fixed Regex quadruple backslash issue in `VintedGoExtractionAdapter`.
- Reverted AC3 (TO_VERIFY status) per PR review request (scope reduction).
- Reverted pickupCode field in Domain Model per PR review request (YAGNI).
- Removed CompositeParcelExtractionAdapter per PR review request (simplification).
- Removed redundant Unit Test `VintedGoExtractionAdapterTest` (covered by Cucumber).
- Fixed `ParcelTest` regression regarding enum values count.