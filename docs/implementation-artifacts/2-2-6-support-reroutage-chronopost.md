# User Story 2-2-6 : Support du Reroutage Colis (Chronopost)

**Statut :** `review`
**Épique :** Epic 2 - Extraction de métadonnées avec Gemini (et Fallbacks déterministes)

## Description
En tant qu'utilisateur Parcel-Flow, je veux que mon colis soit automatiquement mis à jour si Chronopost le reroute vers un autre point relais, afin de ne pas me déplacer au mauvais endroit.

## Contexte Métier
Chronopost envoie un mail spécifique ("Votre colis a été déposé dans un nouveau point de retrait") lorsque la livraison initiale au point choisi a échoué (ex: point saturé). Ce mail contient un nouveau nom de point relais qui doit écraser l'ancien dans notre base.

## Critères d'Acceptation (AC)
1.  **Détection de l'événement :** Le système doit identifier le mail de type "Reroutage" via une empreinte textuelle unique (ex: objet du mail ou phrase clé spécifique).
2.  **Architecture Isolée (Strategy) :** Créer une classe `ChronopostReroutingStrategy` dédiée à ce format. Elle ne doit pas partager de code ou de Regex avec la stratégie de livraison standard.
3.  **Extraction du Nouveau Lieu :** Extraire le nom du nouveau point relais avec une Regex chirurgicale spécifique à ce format de mail.
4.  **Mise à jour du Domaine :** L'événement d'extraction doit déclencher la mise à jour du champ `relayPointName` sur l'agrégat `Parcel` correspondant à l'ID de suivi.
5.  **Test de Non-Régression :** Un test unitaire doit valider l'extraction à partir du fichier `backend/src/test/resources/emails/mail_redirection_relais.txt` (ou équivalent .eml).

## Directives pour l'Agent Dev
- Implémenter la nouvelle stratégie dans `backend/src/main/java/com/parcelflow/infrastructure/adapters/extraction/chronopost/`.
- S'assurer que le `Registry` d'extraction appelle la bonne stratégie en fonction du contenu du mail.
- Ne pas modifier le parseur existant pour la livraison standard.

## Tasks/Subtasks

- [x] Task 1: Redirection Mail Detection
    - [x] Identify unique fingerprint for Chronopost rerouting email in `mail_redirection_relais.txt`.
- [x] Task 2: Implementation of `ChronopostReroutingStrategy` (TDD)
    - [x] Create unit test in `backend/src/test/java/com/parcelflow/infrastructure/adapters/extraction/chronopost/ChronopostReroutingStrategyTest.java`.
    - [x] Implement `ChronopostReroutingStrategy` in `backend/src/main/java/com/parcelflow/infrastructure/adapters/extraction/chronopost/`.
- [x] Task 3: Registry Integration
    - [x] Update `ProviderRegistry` to route rerouting emails via Gmail query filtering.
- [x] Task 4: Domain Update Logic
    - [x] Ensure `relayPointName` is updated on the `Parcel` aggregate via the extraction event.
- [x] Task 5: Final Validation
    - [x] Run full test suite and verify all ACs.

## Dev Agent Record

### Implementation Plan
- Analyze rerouting email format and identify key extraction points.
- Implement standalone strategy `ChronopostReroutingStrategy` for isolated logic.
- Integrate into `ProviderRegistry` using a specific Gmail query to separate rerouting from standard delivery.
- Fix compilation error in `ExtractParcelUseCase` (field `deadline` vs `expirationDate`).
- Add unit test for parcel update logic in `ExtractParcelUseCaseTest`.

### Completion Notes
- Story fully implemented and verified with TDD.
- Rerouting detection is based on the unique fingerprint: "n’a pas pu être livré dans votre point initial".
- Registry integration ensures that rerouting emails are handled by the new strategy without affecting standard delivery.
- Domain update logic correctly replaces the old `pickupPoint` with the new one.

## File List
- `backend/src/main/java/com/parcelflow/infrastructure/adapters/extraction/chronopost/ChronopostReroutingStrategy.java`
- `backend/src/test/java/com/parcelflow/infrastructure/adapters/extraction/chronopost/ChronopostReroutingStrategyTest.java`
- `backend/src/main/java/com/parcelflow/infrastructure/extraction/ProviderRegistry.java`
- `backend/src/test/java/com/parcelflow/infrastructure/extraction/ProviderRegistryTest.java`
- `backend/src/main/java/com/parcelflow/application/usecases/ExtractParcelUseCase.java`
- `backend/src/test/java/com/parcelflow/application/usecases/ExtractParcelUseCaseTest.java`
- `backend/src/test/resources/emails/mail_redirection_relais.txt`

## Change Log
- 2026-02-27: Initial task list created based on ACs.
- 2026-02-27: Implementation complete, tests passing.
- 2026-02-28: Adversarial Code Review: Fixed dangerous location fallback, stale status on update, and improved regex robustness in `ChronopostReroutingStrategy`.
