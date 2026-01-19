# Story 1.2: Cœur du Domaine (Agrégation par Lieu & Modèle Colis)

Status: ready-for-dev

## Story

As a **User**,
I want **my parcels to be grouped by Pickup Location**,
so that **I can optimize my trip and pick up multiple parcels at once.**

## Acceptance Criteria

1. **Domain Purity**: The `domain` package MUST remain pure Java (No Spring/JPA).
2. **PickupPoint Aggregate**:
    - `PickupPoint` Record exists with: `id`, `name`, `rawAddress`, `openingHours`.
    - `openingHours` allows determining if the shop is currently open/closed.
3. **Parcel Aggregate**:
    - Linked to a `PickupPoint`.
    - Includes `ParcelId`, `TrackingNumber`, `Deadline`, `Status`.
4. **Dashboard Aggregation Logic**:
    - The `RetrieveDashboardUseCase` returns a `List<LocationGroup>`.
    - A `LocationGroup` contains:
        - The `PickupPoint` details.
        - A list of `Parcel` belonging to this point.
        - An `UrgencyLevel` (High, Medium, Low) derived from the *most urgent* parcel in the group.
5. **Persistence**:
    - The repository layer handles the join/grouping logic (or the domain does it in memory if volume is low).

## Tasks / Subtasks

- [ ] **Domain Modeling**
  - [ ] Create `PickupPoint` and `Parcel` records.
  - [ ] Implement `LocationGroup` (The Aggregate Root for the Dashboard View).
  - [ ] Implement `UrgencyCalculator` logic.
- [ ] **Primary Port (Use Case)**
  - [ ] `RetrieveDashboardUseCase`: Orchestrates the retrieval and grouping.
- [ ] **Secondary Ports**
  - [ ] `ParcelRepositoryPort` with a method to fetch all active parcels with their pickup points.
- [ ] **ATDD**
  - [ ] Update `DashboardAggregation.feature` to test the grouping logic:
      - *Given* 2 parcels at "Relais A" and 1 at "Relais B"
      - *When* I retrieve the dashboard
      - *Then* I should see 2 Location Groups.

## Dev Notes

### UX Alignment
- This story implements the **"Location-Centric Card Stack"** pattern defined in UX Specs.
- The API output must directly map to the `LocationGroupCard` UI component.

### Technical Implementation Hints
- **Opening Hours**: Keep it simple for now (String), but structure it enough to display.
- **Grouping**: Since we expect < 50 active parcels, doing the grouping in Java (`Collectors.groupingBy`) inside the Domain Service is acceptable and keeps the Domain pure and testable.

### References
- [Source: docs/architecture.md#Section 4] - Domain Design
- [Source: docs/planning-artifacts/ux-design-specification.md#Chosen Direction] - Location-Centric Card Stack

## Dev Agent Record

### Agent Model Used
Gemini 2.0 Flash
