# Story 1.2.2: Regroupement par Point de Retrait

Status: ready-for-dev

## Story

As a **User**,
I want **my parcels grouped by pickup location on my dashboard**,
so that **I can see where I need to go.**

## Acceptance Criteria

### Backend
1.  **Domain Model Extension**:
    *   `PickupPoint` Record: `id`, `name`, `address`.
    *   `LocationGroup` Record: `PickupPoint` + `List<Parcel>`.
2.  **Aggregation Logic**:
    *   `RetrieveDashboardUseCase` returns `List<LocationGroup>`.
3.  **API**:
    *   GET `/api/dashboard` returns the grouped data structure.

### Frontend
1.  **UI Components**:
    *   `LocationGroupCard` implemented to wrap multiple `ParcelCard`s.
2.  **Screen Update**:
    *   `ParcelListScreen` updated to render a list of `LocationGroupCard`.

### Quality (ATDD)
1.  **Feature File**: `backend/src/test/resources/features/DashboardAggregation.feature` exists and passes.

## Tasks / Subtasks

- [ ] **Backend: Domain Modeling**
    - [ ] Create `PickupPoint` and `LocationGroup` records.
    - [ ] Update `Parcel` to include `PickupPoint`.
- [ ] **Backend: Business Logic**
    - [ ] Refactor `RetrieveDashboardUseCase` for grouping logic.
    - [ ] Update API endpoint to `/api/dashboard`.
- [ ] **Backend: Testing**
    - [ ] Create `backend/src/test/resources/features/DashboardAggregation.feature`.
    - [ ] Update Glue Code for grouped verification.
- [ ] **Frontend: UI**
    - [ ] Implement `LocationGroupCard`.
    - [ ] Update screen logic to handle the new grouped API response.

## Dev Notes

- **UI Pattern**: "Location-Centric Card Stack".

## Dev Agent Record

### Agent Model Used
Gemini 2.0 Flash