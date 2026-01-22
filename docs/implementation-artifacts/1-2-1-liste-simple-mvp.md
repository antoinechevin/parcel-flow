# Story 1.2.1: Visualisation Liste Simple (MVP)

Status: ready-for-dev

## Story

As a **User**,
I want **to see a list of my active parcels on my phone**,
so that **I can track what I need to pick up.**

## Acceptance Criteria

### Backend
1.  **Domain Model**:
    *   `Parcel` Record exists with: `ParcelId` (UUID), `TrackingNumber` (String), `Deadline` (LocalDate), `Status` (Enum: AVAILABLE, PICKED_UP).
2.  **Output Port (Repo)**:
    *   `ParcelRepositoryPort` interface defined.
    *   `InMemoryParcelRepository` (Pre-loads 3 static parcels).
3.  **Input Port (Use Case)**:
    *   `RetrieveDashboardUseCase` returns `List<Parcel>`.
4.  **API**:
    *   GET `/api/parcels` returns the flat list of parcels.

### Frontend
1.  **Screen**: `ParcelListScreen` implemented in React Native.
2.  **UI**: Displays a flat list of `ParcelCard` components.
3.  **Data**: Fetches data from the Backend API using a simple hook.

### Quality (ATDD)
1.  **Feature File**: `backend/src/test/resources/features/DashboardList.feature` exists and passes.

## Tasks / Subtasks

- [x] **Backend: Domain & Application**
    - [x] Create `Parcel` record and `ParcelStatus` enum.
    - [x] Define `ParcelRepositoryPort` and `InMemoryParcelRepository`.
    - [x] Implement `RetrieveDashboardUseCase`.
    - [x] Create REST Controller for `/api/parcels`.
- [x] **Backend: Testing**
    - [x] Create `backend/src/test/resources/features/DashboardList.feature`.
    - [x] Implement Cucumber Glue Code.
- [x] **Frontend: UI & Integration**
    - [x] Create `ParcelListScreen` in `frontend/app/`.
    - [x] Create `ParcelCard` component in `frontend/src/components/`.
    - [x] Implement API service/hook to fetch parcels.

## Dev Notes

- **Sprint 1 Focus**: End-to-end connectivity.
- **Fake Data**: "Shoes", "Book", "Hat".

## Dev Agent Record

### Implementation Plan
1.  **Hexagonal Architecture**: Separation of Domain (Parcel), Application (UseCase), and Infrastructure (Controller, Repo).
2.  **TDD**: Unit tests for all backend and frontend components.
3.  **ATDD**: Cucumber feature file implemented for the backend API.
4.  **Frontend**: React Native Paper used for UI components.

### Implementation Notes
- Added Cucumber dependencies to Maven.
- Fixed a pre-existing issue in `Heartbeat.test.tsx`.
- Integrated frontend with backend via `fetch` in `useParcels` hook.

### File List
- `backend/pom.xml` (Modified)
- `backend/src/main/java/com/parcelflow/domain/Parcel.java` (New)
- `backend/src/main/java/com/parcelflow/domain/ParcelStatus.java` (New)
- `backend/src/main/java/com/parcelflow/domain/ParcelRepositoryPort.java` (New)
- `backend/src/main/java/com/parcelflow/infrastructure/persistence/InMemoryParcelRepository.java` (New)
- `backend/src/main/java/com/parcelflow/application/RetrieveDashboardUseCase.java` (New)
- `backend/src/main/java/com/parcelflow/infrastructure/api/ParcelController.java` (New)
- `backend/src/main/java/com/parcelflow/infrastructure/config/ApplicationConfig.java` (New)
- `backend/src/test/java/com/parcelflow/domain/ParcelTest.java` (New)
- `backend/src/test/java/com/parcelflow/infrastructure/persistence/InMemoryParcelRepositoryTest.java` (New)
- `backend/src/test/java/com/parcelflow/application/RetrieveDashboardUseCaseTest.java` (New)
- `backend/src/test/java/com/parcelflow/infrastructure/api/ParcelControllerTest.java` (New)
- `backend/src/test/resources/features/DashboardList.feature` (Modified/Overwritten)
- `backend/src/test/java/com/parcelflow/steps/DashboardSteps.java` (New)
- `backend/src/test/java/com/parcelflow/steps/CucumberConfiguration.java` (New)
- `backend/src/test/java/com/parcelflow/CucumberTest.java` (New)
- `frontend/src/components/ParcelCard.tsx` (New)
- `frontend/src/components/ParcelCard.test.tsx` (New)
- `frontend/src/hooks/useParcels.ts` (New)
- `frontend/app/index.tsx` (Modified)
- `frontend/src/Heartbeat.test.tsx` (Modified)

### Change Log
- 2026-01-22: Initial implementation of Story 1.2.1. (Amelia)

Status: review

## Story