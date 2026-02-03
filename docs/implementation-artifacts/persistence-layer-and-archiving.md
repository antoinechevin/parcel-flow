# Story: Persistence Layer and Archiving

## Status
Status: review
Epic: epic-4

## Description
As a user, I want my parcels to be persisted in a database so that I don't lose them when the server restarts and I want to be able to archive them.

## Acceptance Criteria
- [x] All parcels are stored in a PostgreSQL database.
- [x] A parcel can be archived via a REST endpoint.
- [x] Archived parcels are not visible in the dashboard.
- [x] The persistence layer follows Hexagonal Architecture (Ports & Adapters).

## Tasks
- [x] Define `ARCHIVED` status in `ParcelStatus`
- [x] Create `ParcelEntity` for JPA persistence
- [x] Implement `PostgresParcelRepositoryAdapter`
- [x] Implement `ArchiveParcelUseCase`
- [x] Add archive endpoint to `ParcelController`
- [x] Filter out archived parcels in `RetrieveDashboardUseCase`
- [x] Add `ARCHIVED` status to frontend `Parcel` type
- [x] Implement `archiveParcel` API call in frontend
- [x] Add Archive button to `ParcelCard` for `EXPIRED` and `PICKED_UP` parcels
- [x] Update dashboard after successful archiving

## Dev Agent Record
### File List
- `backend/src/main/java/com/parcelflow/domain/model/ParcelStatus.java`
- `backend/src/main/java/com/parcelflow/application/usecases/RetrieveDashboardUseCase.java`
- `backend/src/main/java/com/parcelflow/application/usecases/ArchiveParcelUseCase.java`
- `backend/src/main/java/com/parcelflow/infrastructure/persistence/ParcelEntity.java`
- `backend/src/main/java/com/parcelflow/infrastructure/persistence/SpringDataParcelRepository.java`
- `backend/src/main/java/com/parcelflow/infrastructure/persistence/PostgresParcelRepositoryAdapter.java`
- `backend/src/main/java/com/parcelflow/infrastructure/api/ParcelController.java`
- `backend/src/main/java/com/parcelflow/infrastructure/config/ApplicationConfig.java`
- `backend/src/test/resources/features/ArchiveParcel.feature`
- `backend/src/test/java/com/parcelflow/steps/ArchiveParcelSteps.java`
- `backend/src/test/java/com/parcelflow/infrastructure/persistence/PostgresParcelRepositoryAdapterIT.java`
- `frontend/src/types/index.ts`
- `frontend/src/hooks/useDashboard.ts`
- `frontend/src/components/ParcelCard.tsx`
- `frontend/src/components/LocationGroupCard.tsx`
- `frontend/app/index.tsx`

### Change Log
- Added `ARCHIVED` status.
- Implemented JPA persistence for parcels with PostgreSQL.
- Added archiving logic with business exception handling.
- Added REST endpoint for archiving.
- Updated dashboard to exclude archived parcels (in-memory filtering).
- Implemented frontend support for archiving with "Silent Refresh" for better UX.
- Added "ARCHIVER" button to UI for non-active parcels.
- Optimized JPA adapter to reduce unnecessary database lookups.
- Added full test suite: Gherkin features, Step definitions, and Integration tests.
- Cleaned up backend use cases (formatting and indentation).
- Fixed a syntax error in `useDashboard.ts` that was breaking the frontend build.

