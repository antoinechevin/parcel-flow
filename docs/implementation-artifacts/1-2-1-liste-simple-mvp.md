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

- [ ] **Backend: Domain & Application**
    - [ ] Create `Parcel` record and `ParcelStatus` enum.
    - [ ] Define `ParcelRepositoryPort` and `InMemoryParcelRepository`.
    - [ ] Implement `RetrieveDashboardUseCase`.
    - [ ] Create REST Controller for `/api/parcels`.
- [ ] **Backend: Testing**
    - [ ] Create `backend/src/test/resources/features/DashboardList.feature`.
    - [ ] Implement Cucumber Glue Code.
- [ ] **Frontend: UI & Integration**
    - [ ] Create `ParcelListScreen` in `frontend/app/`.
    - [ ] Create `ParcelCard` component in `frontend/src/components/`.
    - [ ] Implement API service/hook to fetch parcels.

## Dev Notes

- **Sprint 1 Focus**: End-to-end connectivity.
- **Fake Data**: "Shoes", "Book", "Hat".

## Dev Agent Record

### Agent Model Used
Gemini 2.0 Flash