# Story 1.2.2: Regroupement par Point de Retrait

Status: review

## Story

As a **User**,
I want **my parcels grouped by pickup location on my dashboard**,
so that **I can see where I need to go.**

## Acceptance Criteria

### Backend
1.  **Domain Model Extension**: [x]
    *   `PickupPoint` Record: `id`, `name`, `address`.
    *   `LocationGroup` Record: `PickupPoint` + `List<Parcel>`.
2.  **Aggregation Logic**: [x]
    *   `RetrieveDashboardUseCase` returns `List<LocationGroup>`.
3.  **API**: [x]
    *   GET `/api/dashboard` returns the grouped data structure.

### Frontend
1.  **UI Components**: [x]
    *   `LocationGroupCard` implemented to wrap multiple `ParcelCard`s.
2.  **Screen Update**: [x]
    *   `ParcelListScreen` updated to render a list of `LocationGroupCard`.

### Quality (ATDD)
1.  **Feature File**: `backend/src/test/resources/features/DashboardAggregation.feature` exists and passes. [x]

## Tasks / Subtasks

- [x] **Backend: Domain Modeling**
    - [x] Create `PickupPoint` and `LocationGroup` records.
    - [x] Update `Parcel` to include `PickupPoint`.
- [x] **Backend: Business Logic**
    - [x] Refactor `RetrieveDashboardUseCase` for grouping logic.
    - [x] Update API endpoint to `/api/dashboard`.
- [x] **Backend: Testing**
    - [x] Create `backend/src/test/resources/features/DashboardAggregation.feature`.
    - [x] Update Glue Code for grouped verification.
- [x] **Frontend: UI**
    - [x] Implement `LocationGroupCard`.
    - [x] Update screen logic to handle the new grouped API response.

## Dev Notes

- **UI Pattern**: "Location-Centric Card Stack".

## Dev Agent Record

### Agent Model Used
Gemini 2.0 Flash

### Implementation Plan
- **Backend**:
    - Extension du modèle de domaine avec `PickupPoint` et `LocationGroup`.
    - Refactoring de `RetrieveDashboardUseCase` pour utiliser `Collectors.groupingBy`.
    - Changement de l'endpoint API vers `/api/dashboard`.
    - Validation via tests unitaires et Cucumber (ATDD).
- **Frontend**:
    - Centralisation des types dans `src/types/index.ts`.
    - Création de `LocationGroupCard` pour l'affichage groupé.
    - Mise à jour du hook `useParcels` et de l'écran principal.
    - Validation via Jest et React Native Testing Library.

### File List
- `backend/src/main/java/com/parcelflow/domain/model/PickupPoint.java` (Nouveau)
- `backend/src/main/java/com/parcelflow/domain/model/LocationGroup.java` (Nouveau)
- `backend/src/main/java/com/parcelflow/domain/model/Parcel.java` (Modifié)
- `backend/src/main/java/com/parcelflow/application/usecases/RetrieveDashboardUseCase.java` (Modifié)
- `backend/src/main/java/com/parcelflow/infrastructure/api/ParcelController.java` (Modifié)
- `backend/src/main/java/com/parcelflow/infrastructure/persistence/InMemoryParcelRepository.java` (Modifié)
- `backend/src/test/java/com/parcelflow/domain/DomainModelingTest.java` (Nouveau)
- `backend/src/test/java/com/parcelflow/application/RetrieveDashboardUseCaseTest.java` (Modifié)
- `backend/src/test/java/com/parcelflow/infrastructure/api/ParcelControllerTest.java` (Modifié)
- `backend/src/test/java/com/parcelflow/steps/DashboardSteps.java` (Modifié)
- `backend/src/test/java/com/parcelflow/CucumberTest.java` (Modifié)
- `frontend/src/types/index.ts` (Nouveau)
- `frontend/src/components/ParcelCard.tsx` (Modifié)
- `frontend/src/components/LocationGroupCard.tsx` (Nouveau)
- `frontend/src/components/LocationGroupCard.test.tsx` (Nouveau)
- `frontend/src/hooks/useParcels.ts` (Modifié)
- `frontend/app/index.tsx` (Modifié)
