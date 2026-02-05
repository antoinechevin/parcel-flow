# Story 4.2: Archivage par Swipe & Undo

Status: done

<!-- Note: Validation is optional. Run validate-create-story for quality check before dev-story. -->

## Story

As a **User**,
I want **to archive a parcel with a natural swipe gesture and have a safety net to undo it**,
so that **my list stays clean with minimal effort and no fear of mistakes.**

## Acceptance Criteria

1. [x] **Given** I am on the dashboard list.
2. [x] **When** I perform a "Swipe-to-left" gesture on a parcel item.
3. [x] **Then** a subtle haptic feedback is triggered (using `expo-haptics`).
4. [x] **And** the parcel is immediately removed from the active list (optimistic UI).
5. [x] **And** a Snackbar appears for 5 seconds with an "UNDO" (ANNULER) button.
6. [x] **And** clicking "UNDO" restores the parcel to its exact previous position in the list.
7. [x] **And** if no Undo occurs, the `ARCHIVED` status is persisted in the backend.

## Tasks / Subtasks

- [x] **Components**: Wrap `ParcelCard` with `Swipeable` from `react-native-gesture-handler` (AC: 2)
- [x] **Infrastructure**: Integrate `expo-haptics` for vibration feedback (AC: 3)
- [x] **State**: Implement `Undo` logic in the `useDashboard` store/hook (AC: 4, 5, 6)
- [x] **API**: Call the `archiveParcel` endpoint after the 5s timeout if not undone (AC: 7)
- [x] **Testing**: Create component tests for swipe and undo behavior (AC: 1-7)

## Dev Notes

- **Libraries**: `react-native-gesture-handler`, `react-native-reanimated`, `expo-haptics` are ready.
- **Optimistic UI**: Remove from local state immediately, but queue the API call.
- **Handoff**: Focus on the `ParcelCard` and `useDashboard` hook.

### Project Structure Notes

- `frontend/src/components/ParcelCard.tsx`
- `frontend/src/hooks/useDashboard.ts`
- `frontend/app/index.tsx` (Added Snackbar)
- `frontend/app/_layout.tsx` (Added GestureHandlerRootView)

### References

- [Source: docs/planning-artifacts/epics.md#Story 4.2]
- [Source: docs/prd.md#FR4.1, FR4.2]
- [Source: docs/planning-artifacts/ux-design-specification.md#Smart Archiving]

## Dev Agent Record

### Agent Model Used

Gemini 2.0 Flash (SM Agent YOLO Mode)

### Debug Log References

### Completion Notes List
- Implemented `Swipeable` in `ParcelCard` using `react-native-gesture-handler`.
- Added haptic feedback using `expo-haptics`.
- Refactored `useDashboard` hook to support a 5-second undo window.
- Added `Snackbar` to `app/index.tsx` for visual Undo feedback with tracking number.
- **Review Fixes Applied**:
    - Added `GestureHandlerRootView` to `app/_layout.tsx` (Critical for Android support).
    - Fixed memory leak in `useDashboard` by cleaning up `setTimeout` on unmount.
    - Improved race condition handling when archiving multiple parcels in sequence.
    - Removed redundant "ARCHIVER" button from `ParcelCard`.
- Created `frontend/src/hooks/useDashboard.test.ts` to verify the logic.
- All tests passing (13/13).

### File List
- `frontend/src/hooks/useDashboard.ts`
- `frontend/src/hooks/useDashboard.test.ts`
- `frontend/src/components/ParcelCard.tsx`
- `frontend/src/components/ParcelCard.test.tsx`
- `frontend/app/index.tsx`
- `frontend/app/_layout.tsx`
