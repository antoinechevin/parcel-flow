# Story 4.2: Archivage par Swipe & Undo

Status: ready-for-dev

<!-- Note: Validation is optional. Run validate-create-story for quality check before dev-story. -->

## Story

As a **User**,
I want **to archive a parcel with a natural swipe gesture and have a safety net to undo it**,
so that **my list stays clean with minimal effort and no fear of mistakes.**

## Acceptance Criteria

1. **Given** I am on the dashboard list.
2. **When** I perform a "Swipe-to-left" gesture on a parcel item.
3. **Then** a subtle haptic feedback is triggered (using `expo-haptics`).
4. **And** the parcel is immediately removed from the active list (optimistic UI).
5. **And** a Snackbar appears for 5 seconds with an "UNDO" (ANNULER) button.
6. **And** clicking "UNDO" restores the parcel to its exact previous position in the list.
7. **And** if no Undo occurs, the `ARCHIVED` status is persisted in the backend.

## Tasks / Subtasks

- [ ] **Components**: Wrap `ParcelCard` with `Swipeable` from `react-native-gesture-handler` (AC: 2)
- [ ] **Infrastructure**: Integrate `expo-haptics` for vibration feedback (AC: 3)
- [ ] **State**: Implement `Undo` logic in the `useDashboard` store/hook (AC: 4, 5, 6)
- [ ] **API**: Call the `archiveParcel` endpoint after the 5s timeout if not undone (AC: 7)
- [ ] **Testing**: Create component tests for swipe and undo behavior (AC: 1-7)

## Dev Notes

- **Libraries**: `react-native-gesture-handler`, `react-native-reanimated`, `expo-haptics` are ready.
- **Optimistic UI**: Remove from local state immediately, but queue the API call.
- **Handoff**: Focus on the `ParcelCard` and `useDashboard` hook.

### Project Structure Notes

- `frontend/src/components/ParcelCard.tsx`
- `frontend/src/hooks/useDashboard.ts`

### References

- [Source: docs/planning-artifacts/epics.md#Story 4.2]
- [Source: docs/prd.md#FR4.1, FR4.2]
- [Source: docs/planning-artifacts/ux-design-specification.md#Smart Archiving]

## Dev Agent Record

### Agent Model Used

Gemini 2.0 Flash (SM Agent YOLO Mode)

### Debug Log References

### Completion Notes List

### File List
