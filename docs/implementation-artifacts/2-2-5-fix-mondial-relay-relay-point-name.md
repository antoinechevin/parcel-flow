# Story: 2-2-5-fix-mondial-relay-relay-point-name

**ID:** 2-2-5-fix-mondial-relay-relay-point-name
**Epic:** Epic 2 - Le Cœur d'Extraction (Regex) & Protection de la Vie Privée
**Status:** done
**Priority:** HIGH
**Type:** BUG

## Description
Le nom du point relais Mondial Relay n'est pas correctement extrait dans les nouveaux formats d'emails "disponible". Actuellement, l'adapteur semble cibler uniquement les "Lockers" ou retourne une valeur par défaut "Mondial Relay Point".

## Acceptance Criteria
1. **Extraction Nom du Point Relais**: Le système doit extraire correctement le nom du point relais (ex: "CAMINHOS DE PORTUGAL SAIN-BEL") pour les emails de type "Point Relais" standard.
2. **Support JSON-LD**: Si possible, privilégier l'extraction via le bloc `<script type="application/ld+json">` qui contient la structure `ParcelDelivery` avec `deliveryAddress.name`, car c'est la source la plus robuste.
3. **Regex Fallback**: Mettre à jour la Regex de secours pour capturer le texte entre `Point Relais<sup>®</sup> <span...>` et le code postal/ville.
4. **Non-Régression**: Vérifier que l'extraction des numéros de suivi et des délais (expiration) fonctionne toujours.

## Technical Notes
- Email de référence reçu le 27 Jan 2026.
- Structure HTML cible : `Point Relais<sup>®</sup> <span style="color: #FF5C84; font-weight: bold;"> NOM_DU_POINT </span>`
- Structure JSON cible : `deliveryAddress.name`
- Fichier à modifier : `backend/src/main/java/com/parcelflow/infrastructure/extraction/MondialRelayExtractionAdapter.java`

## Tasks
- [x] Créer un test unitaire avec le contenu de l'email fourni par Antoine (dans `MondialRelayExtractionAdapterTest`).
- [x] Implémenter l'extraction via JSON-LD (prioritaire).
- [x] Mettre à jour la Regex de fallback si le JSON-LD est absent.
- [x] Valider que tous les tests de `MondialRelayExtractionAdapter` passent.

## Dev Agent Record
### Implementation Plan
- Added `mail_mondial_relay_standard.eml` with the content provided by Antoine.
- Added test case `shouldExtractMetadataFromStandardPickupEmail` to `MondialRelayExtractionAdapterTest`.
- Implemented `extractFromJsonLd` in `MondialRelayExtractionAdapter` using Jackson.
- Configured `ObjectMapper` to allow trailing commas (common in Mondial Relay emails).
- Implemented `cleanLocationName` to strip trailing dots and whitespaces.
- Updated `extractPickupLocation` to use JSON-LD first, then fallback to updated Regex.
- Added `shouldExtractMetadataUsingRegexFallback` test case.

### Completion Notes
- All tests passing, including non-regression on Lockers.
- Robust extraction via JSON-LD is now the primary strategy for Mondial Relay.
- Nettoyage automatique des noms de lieux (suppression du point final).
- Fixed JSON-LD fragility regarding leading spaces in keys (e.g. " @type").
- Improved `cleanLocationName` to be more robust against formatting artifacts.
- Removed unused `PIN_PATTERN`.

## File List
- `backend/src/main/java/com/parcelflow/infrastructure/extraction/MondialRelayExtractionAdapter.java`
- `backend/src/test/java/com/parcelflow/infrastructure/extraction/MondialRelayExtractionAdapterTest.java`
- `backend/src/test/resources/emails/mail_mondial_relay_2.eml`

## Change Log
- Fix extraction of Mondial Relay pickup location name.
- Add JSON-LD support for parcel extraction with handling of leading spaces in keys.
- Fix Jackson parsing for invalid JSON with trailing commas.
- Add unit tests for standard pickup format and fallback mechanism.
- Refactor: Remove unused patterns and improve string cleaning logic.
- Addressed code review findings (Date: 2026-01-29).
