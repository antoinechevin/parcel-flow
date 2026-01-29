# Story 4.5: Gestion et Affichage des Colis Expirés

Status: done

## Story

As a **User**,
I want **expired parcels to be visually distinct and moved to the bottom of the list**,
so that **I can focus on parcels I can still collect while keeping track of those I missed.**

## Acceptance Criteria

### Backend
1.  **Domain Model**:
    *   `ParcelStatus` enum includes `EXPIRED`.
2.  **Domain Logic**:
    *   Un colis est considéré comme `EXPIRED` si sa `deadline` est strictement antérieure à la date du jour.
    *   Le système doit traiter ces colis comme non-urgents pour le calcul du niveau d'urgence global du groupe.
3.  **Use Case (`RetrieveDashboardUseCase`)**:
    *   Les groupes contenant uniquement des colis expirés sont placés en fin de liste.
    *   Au sein d'un groupe, les colis expirés sont affichés après les colis disponibles.

### Frontend
1.  **UI**: Les colis expirés sont affichés en gris (opacité réduite, ex: 0.5).
2.  **Badge**: Un texte ou badge "Expiré" est visible sur la carte du colis.
3.  **Tri**: Respect du tri backend (les colis expirés en dernier).

### Quality (ATDD)
1.  **Feature File**: `backend/src/test/resources/features/ExpiredParcels.feature` existe.
    *   Scenario: Un colis avec une date passée est marqué comme expiré.
    *   Scenario: Les colis expirés apparaissent après les colis disponibles dans le dashboard.

## Tasks / Subtasks

- [x] **Backend: Domain**
    - [x] Ajouter `EXPIRED` à `ParcelStatus`.
    - [x] Mettre à jour la logique de détermination du statut dans le domaine.
    - [x] Adapter `UrgencyCalculator` pour ignorer les colis expirés dans le calcul de l'urgence.
- [x] **Backend: Application**
    - [x] Mettre à jour le tri dans `RetrieveDashboardUseCase`.
- [x] **Backend: Testing**
    - [x] Créer `backend/src/test/resources/features/ExpiredParcels.feature`.
    - [x] Mettre à jour les tests unitaires et d'intégration.
- [x] **Frontend: UI**
    - [x] Modifier `ParcelCard.tsx` pour gérer le style "Expiré".
    - [x] S'assurer que le thème supporte une couleur "disabled" ou utiliser l'opacité.
- [x] **Frontend: Testing**
    - [x] Ajouter un test de composant pour vérifier le rendu d'un colis expiré.

## Dev Agent Record

### Implementation Plan
1.  **Backend**:
    - Ajout de la valeur `EXPIRED` à l'énumération `ParcelStatus`.
    - Modification de `RetrieveDashboardUseCase` pour calculer dynamiquement le statut `EXPIRED` si la deadline est dépassée (comparaison avec `LocalDate.now(clock)`).
    - Mise en place d'un tri complexe :
        - Les groupes (Lieux) contenant au moins un colis `AVAILABLE` apparaissent en premier.
        - Les groupes sans colis disponible (uniquement expirés ou récupérés) apparaissent en fin de liste.
        - Au sein d'un groupe, les colis sont triés par priorité : `AVAILABLE` < `EXPIRED` < `PICKED_UP`.
    - Mise à jour de `UrgencyCalculator` pour exclure explicitement les colis `EXPIRED` du calcul de l'urgence globale du lieu.
2.  **Frontend**:
    - Mise à jour du type TypeScript `Parcel`.
    - Modification de `ParcelCard.tsx` : les colis expirés utilisent la couleur grise du thème (ou `#bdc3c7`), une opacité de 0.5, et un style de texte grisé.
3.  **Tests**:
    - Création de `ExpiredParcels.feature` (ATDD) validant le changement de statut et le tri.
    - Ajout de `TestClock` pour piloter la date dans les tests Cucumber.
    - Mise à jour de `UrgencyCalculatorTest.java`.
    - Ajout d'un test Jest dans `ParcelCard.test.tsx`.

### File List
- `backend/src/main/java/com/parcelflow/domain/model/Parcel.java`
- `backend/src/main/java/com/parcelflow/domain/model/ParcelStatus.java`
- `backend/src/main/java/com/parcelflow/domain/service/UrgencyCalculator.java`
- `backend/src/main/java/com/parcelflow/application/usecases/RetrieveDashboardUseCase.java`
- `backend/src/test/resources/features/ExpiredParcels.feature`
- `backend/src/test/java/com/parcelflow/steps/DashboardSteps.java`
- `backend/src/test/java/com/parcelflow/steps/TestClock.java`
- `backend/src/test/java/com/parcelflow/steps/CucumberConfiguration.java`
- `backend/src/test/java/com/parcelflow/domain/ParcelTest.java`
- `backend/src/test/java/com/parcelflow/domain/service/UrgencyCalculatorTest.java`
- `frontend/src/types/index.ts`
- `frontend/src/components/ParcelCard.tsx`
- `frontend/src/components/ParcelCard.test.tsx`
