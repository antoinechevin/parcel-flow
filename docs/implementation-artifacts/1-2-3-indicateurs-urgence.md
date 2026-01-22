# Story 1.2.3: Indicateurs d'Urgence

Status: ready-for-dev

## Story

As a **User**,
I want **to see urgency indicators on my pickup locations**,
so that **I don't miss a deadline.**

## Acceptance Criteria

### Backend
1.  **Domain Logic**:
    *   `UrgencyLevel` Enum (HIGH, MEDIUM, LOW).
    *   `UrgencyCalculator` service.
2.  **Dashboard Sorting**:
    *   Groups sorted by Urgency (HIGH first).

### Frontend
1.  **Visuals**:
    *   `LocationGroupCard` displays a colored indicator (Red, Orange, Blue) based on urgency.
    *   Display "Expires in X days" on the card.

### Quality (ATDD)
1.  **Feature File**: `backend/src/test/resources/features/DashboardUrgency.feature` exists and passes.

## Tasks / Subtasks

- [ ] **Backend: Logic**
    - [ ] Implement `UrgencyCalculator`.
    - [ ] Integrate urgency calculation into the dashboard aggregation.
    - [ ] Apply sorting in the Use Case.
- [ ] **Frontend: UI**
    - [ ] Update `LocationGroupCard` with urgency-based styling (React Native Paper colors).
    - [ ] Add expiration date labels to `ParcelCard`.
- [ ] **Testing**
    - [ ] Unit test the calculator.
    - [ ] Verify sorting in Cucumber tests.

## Dev Notes

- **Colors**: Use `theme.colors.error` for HIGH urgency.

## Dev Agent Record

### Agent Model Used
Gemini 2.0 Flash