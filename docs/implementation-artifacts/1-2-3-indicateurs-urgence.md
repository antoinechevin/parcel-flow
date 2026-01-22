# Story 1.2.3: Indicateurs d'Urgence

Status: done

...

## Dev Agent Record

### Agent Model Used
Gemini 2.0 Flash

### Implementation Plan
- Création de l'énumération `UrgencyLevel` et du service `UrgencyCalculator` (Backend).
- Intégration du calcul d'urgence dans `RetrieveDashboardUseCase` et tri par niveau d'urgence décroissant.
- Mise à jour de `LocationGroupCard` et `ParcelCard` pour utiliser les couleurs du thème (Red, Orange, Blue) et afficher le délai d'expiration.
- Mise à jour du thème global dans `_layout.tsx`.

### Tests Created
- Backend: `UrgencyCalculatorTest.java` (Unit), `DashboardUrgency.feature` (ATDD).
- Frontend: `LocationGroupCard.urgency.test.tsx` (Component).

### File List
- `backend/src/main/java/com/parcelflow/domain/model/UrgencyLevel.java`
- `backend/src/main/java/com/parcelflow/domain/service/UrgencyCalculator.java`
- `backend/src/main/java/com/parcelflow/domain/model/LocationGroup.java`
- `backend/src/main/java/com/parcelflow/application/usecases/RetrieveDashboardUseCase.java`
- `backend/src/main/java/com/parcelflow/infrastructure/config/ApplicationConfig.java`
- `backend/src/main/java/com/parcelflow/infrastructure/persistence/InMemoryParcelRepository.java`
- `backend/src/main/java/com/parcelflow/domain/ports/ParcelRepositoryPort.java`
- `backend/src/test/java/com/parcelflow/CucumberTest.java`
- `backend/src/test/java/com/parcelflow/domain/service/UrgencyCalculatorTest.java`
- `backend/src/test/java/com/parcelflow/steps/DashboardSteps.java`
- `frontend/src/types/index.ts`
- `frontend/src/components/LocationGroupCard.tsx`
- `frontend/src/components/ParcelCard.tsx`
- `frontend/app/_layout.tsx`
- `frontend/src/components/LocationGroupCard.urgency.test.tsx`