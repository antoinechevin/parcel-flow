# Story 2.2.4: Orchestration par Requêtes Ciblées (Targeted Polling)

**Epic:** 2 - Le Cœur d'Extraction (Regex) & Protection de la Vie Privée
**Status:** done
**Priorité:** High

## Description
En tant que développeur,
Je veux configurer des requêtes Gmail spécifiques pour chaque transporteur et les lier directement à leur adaptateur d'extraction,
Afin que le système ne récupère que les emails pertinents et applique la bonne logique de parsing sans ambiguïté.

## Acceptance Criteria

### 1. Configuration des Providers
**Given** le système démarre
**When** les composants sont injectés
**Then** il existe un registre (Bean Registry ou Map) associant chaque provider à sa requête spécifique :
  - **Chronopost :** `from:chronopost@network1.pickup.fr`
  - **Mondial Relay :** `from:noreply@mondialrelay.fr subject:"disponible"`
  - **Vinted Go :** `from:(noreply@vinted.com | no-reply@vinted.com) subject:(récupère ton colis | récupérer ton colis)`

### 2. Mise à jour du MailSourcePort
**Given** l'interface `MailSourcePort`
**When** je veux récupérer les emails
**Then** la méthode `fetchEmails` accepte un paramètre `query` (String).
**And** l'implémentation `GmailInboundAdapter` utilise cette query pour filtrer côté serveur (API Gmail).

### 3. Logique de Polling Ciblée
**Given** le job planifié `PollingJob` s'exécute (Simulé via `MailScanController` pour le debug)
**When** il itère sur les providers configurés
**Then** pour chaque provider, il appelle `mailSource.fetchEmails(providerQuery)`.
**And** il passe les emails récupérés EXCLUSIVEMENT à l'adaptateur `ParcelExtractionPort` associé.
**And** il ne tente pas de passer ces emails aux autres adaptateurs.

### 4. Efficacité (Pas de fetch global)
**Given** le job de polling
**When** il s'exécute
**Then** aucune requête `fetchEmails` sans filtre (ou avec filtre générique large) n'est effectuée.

## Technical Notes

### Architecture
- **Infrastructure Layer:**
    - `GmailInboundAdapter` : Update signature to `List<Email> fetchEmails(Instant watermark, String query)`.
    - **Configuration:** Créer une classe de configuration ou un composant `ExtractionOrchestrator` qui détient la Map `Provider -> (Query, Adapter)`.
- **Refactoring:**
    - Le `ExtractParcelUseCase` actuel devra être adapté pour recevoir l'adaptateur spécifique à utiliser, afin d'éviter la détection automatique qui pourrait être source d'erreur.

## Tasks/Subtasks

- [x] Task 1: Update MailSourcePort & GmailInboundAdapter
    - [x] Modifier `MailSourcePort.fetchEmails` pour accepter la query.
    - [x] Implémenter le filtrage `q` dans `GmailInboundAdapter`.
- [x] Task 2: Provider Registry Implementation
    - [x] Créer une structure de données (ex: `ProviderDefinition`) associant Nom, Query et Adaptateur.
    - [x] Configurer les 3 providers avec les requêtes fournies.
- [x] Task 3: Refactor Polling Job (via Debug Controller)
    - [x] Modifier la boucle dans `MailScanController` pour itérer sur les providers du registre.
    - [x] Passer l'adaptateur spécifique au Use Case.
- [x] Task 4: Tests & Validation
    - [x] Vérifier le bon fonctionnement avec `ProviderRegistryTest`.

## Definition of Done
- [x] Requêtes ciblées fonctionnelles pour les 3 transporteurs (via `/api/debug/scan`).
- [x] 100% des tests Cucumber et Unitaires passent.
- [x] Plus aucun fetch "global" n'est présent dans le code.

## Dev Agent Record (Amelia)
### Implementation Plan
- Implémentation du registre `ProviderRegistry` pour centraliser les requêtes Gmail et les adaptateurs d'extraction.
- Refactorisation de `ExtractParcelUseCase` pour supporter l'injection d'un adaptateur spécifique via surcharge de la méthode `execute`.
- Mise à jour de `MailScanController` pour orchestrer le scan ciblé en utilisant le registre, permettant de tester la logique sans attendre la story 1.4.

### File List
- `backend/src/main/java/com/parcelflow/infrastructure/extraction/ProviderDefinition.java`
- `backend/src/main/java/com/parcelflow/infrastructure/extraction/ProviderRegistry.java`
- `backend/src/main/java/com/parcelflow/application/usecases/ExtractParcelUseCase.java`
- `backend/src/main/java/com/parcelflow/infrastructure/api/MailScanController.java`
- `backend/src/main/java/com/parcelflow/infrastructure/config/ApplicationConfig.java`
- `backend/src/test/java/com/parcelflow/infrastructure/extraction/ProviderRegistryTest.java`
- `backend/src/main/java/com/parcelflow/infrastructure/mail/GmailInboundAdapter.java`
- `backend/src/main/java/com/parcelflow/domain/ports/MailSourcePort.java`
- `backend/src/test/java/com/parcelflow/application/usecases/ExtractParcelUseCaseTest.java`

### Change Log
- 2026-01-27: Initial implementation of targeted scanning via ProviderRegistry in MailScanController.
- 2026-01-27: (Code Review) Added unit tests for targeted extraction in ExtractParcelUseCaseTest and updated File List.

## Status
Status: review
