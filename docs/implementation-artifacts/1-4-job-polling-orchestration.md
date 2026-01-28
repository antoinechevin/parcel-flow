# Story 1.4: Polling Job & Orchestration

Status: done

## Story

As a **System**,
I want **to periodically trigger the mail checking process**,
so that **new delivery emails are automatically processed without manual intervention.**

## Acceptance Criteria

1.  **Scheduling (Infrastructure)**: Une tâche Spring `@Scheduled` s'exécute à un intervalle configurable (par défaut toutes les 15 minutes via `application.yml`). (DONE)
2.  **Orchestration (Application)**: Le Job utilise le `ProviderRegistry` (introduit en story 2.2.4) pour itérer sur chaque transporteur configuré (Chronopost, Mondial Relay, Vinted). (DONE)
3.  **Targeted Fetching**: Pour chaque provider, le Job appelle `MailSourcePort.fetchEmails(watermark, query)` en utilisant la requête Gmail spécifique du registre. (DONE)
4.  **Traitement Unitaire**: Le Job invoque le `ProcessIncomingEmailUseCase` (ou `ExtractParcelUseCase` refactoré) pour chaque email trouvé, en passant l'adaptateur d'extraction correspondant au provider. (DONE)
5.  **Résilience**: L'échec du traitement d'un email ou d'un provider entier ne doit **PAS** interrompre le Job. Les erreurs doivent être logguées via SLF4J avec le contexte (Provider Name, Email ID). (DONE)
6.  **Watermark Persistence**: La date du dernier email traité doit être persistée via un `WatermarkRepositoryPort` pour éviter de re-traiter les emails déjà vus après un redémarrage. (DONE)
7.  **Transition**: La logique de scan manuel présente dans `MailScanController` (Story 2.2.4) doit être déléguée au nouveau service d'orchestration pour éviter la duplication de code. (DONE)

## Tasks / Subtasks

- [x] **Application: Orchestration Layer**
    - [x] Créer `EmailPollingOrchestrator` (Service) qui contient la boucle sur le `ProviderRegistry`.
    - [x] Implémenter la logique de gestion des Watermarks par provider ou globalement.
- [x] **Infrastructure: Background Job**
    - [x] Créer `EmailPollingJob` dans `com.parcelflow.infrastructure.scheduler`.
    - [x] Utiliser `@Scheduled(fixedDelayString = "${parcelflow.polling.interval:PT15M}")`.
    - [x] Appeler `EmailPollingOrchestrator.run()`.
- [x] **Domain: Persistence Port**
    - [x] Définir `WatermarkRepositoryPort` pour stocker l'état du polling.
    - [x] Implémenter un adaptateur JPA simple pour ce port. (Note: Implémenté en `InMemory` pour le MVP).
- [x] **Refactoring & Cleanup**
    - [x] Refactorer `MailScanController` pour utiliser `EmailPollingOrchestrator`.
- [x] **Testing (ATDD)**
    - [x] Créer `backend/src/test/resources/features/epic-1/polling-orchestration.feature`.
    - [x] Vérifier que le Job s'exécute et que les emails sont correctement routés vers les bons extracteurs.

## Dev Notes

### Architecture Patterns
- **Primary Adapter (Time-driven)**: Le scheduler déclenche l'orchestrateur d'application.
- **Provider Strategy**: Réutilisation impérative du `ProviderRegistryPort` (découplage architecture hexagonale).
- **Resilience**: Utiliser un bloc `try-catch` robuste dans la boucle des providers.

### Source Tree Components
- `backend/src/main/java/com/parcelflow/domain/model/ProviderDefinition.java`
- `backend/src/main/java/com/parcelflow/domain/ports/ProviderRegistryPort.java`
- `backend/src/main/java/com/parcelflow/domain/ports/WatermarkRepositoryPort.java`
- `backend/src/main/java/com/parcelflow/application/usecases/EmailPollingOrchestrator.java`
- `backend/src/main/java/com/parcelflow/infrastructure/scheduler/EmailPollingJob.java`
- `backend/src/main/java/com/parcelflow/infrastructure/persistence/InMemoryWatermarkRepository.java`
- `backend/src/main/java/com/parcelflow/infrastructure/extraction/ProviderRegistry.java`
- `backend/src/test/java/com/parcelflow/steps/PollingOrchestrationSteps.java`
- `backend/src/test/resources/features/epic-1/polling-orchestration.feature`

### Testing Standards
- **Cucumber**: Simuler des emails pour différents providers et vérifier la persistance des colis résultants.
- **Logging**: Vérifier la présence de logs clairs en cas d'erreur de connexion à l'API Gmail.

### References
- [Source: docs/architecture.md#2. High Level Architecture]
- [Source: docs/implementation-artifacts/1-3-adapter-gmail-client.md]
- [Source: docs/implementation-artifacts/2-2-4-orchestration-par-requetes-ciblees.md]

## Dev Agent Record

### Agent Model Used
Gemini 2.0 Flash (via SM Agent Bob)

### File List
- `docs/implementation-artifacts/1-4-job-polling-orchestration.md`
