# Story 4.1: Mode Guichet (Luminosité Native & Zoom)

Status: done

<!-- Note: Validation is optional. Run validate-create-story for quality check before dev-story. -->

## Story

As a **User**,
I want **the screen to automatically switch to maximum brightness when showing a withdrawal code**,
so that **the shopkeeper's scanner can read it without friction.**

## Acceptance Criteria

1. **Given** I open the "Guichet Mode" for a specific parcel.
2. **When** the modal becomes visible.
3. **Then** the device screen brightness is set to 100% (using `expo-brightness`).
4. **And** the withdrawal code is displayed in large, high-contrast characters.
5. **And** the brightness returns to its original system level when the modal is closed.

## Tasks / Subtasks

- [x] **Infrastructure**: Configure `expo-brightness` in the mobile app (AC: 3)
- [x] **Components**: Update `GuichetModeModal` to trigger brightness changes on mount/unmount (AC: 1, 2, 3, 5)
- [x] **UI**: Ensure high-contrast styling is applied (AC: 4)
- [x] **Testing**: Verify brightness API calls (mocked) and UI visibility (AC: 1-5)

## Dev Notes

- **Library**: `expo-brightness` has already been installed. Use `Brightness.setBrightnessAsync(1)`.
- **Permission**: Handled `Brightness.requestPermissionsAsync()` in `GuichetModeModal`.
- **Handoff**: This is a frontend-only story.
- **Components**: `frontend/src/components/GuichetModeModal.tsx`

### Project Structure Notes

- Uses `frontend/` Expo environment.
- Respect Material Design 3 via `react-native-paper`.
- Added `expo-brightness` plugin to `app.json`.

### References

- [Source: docs/planning-artifacts/epics.md#Story 4.1]
- [Source: docs/prd.md#FR3.2]
- [Source: docs/planning-artifacts/ux-design-specification.md#2.5 Experience Mechanics]

## Dev Agent Record

### Agent Model Used

Gemini 2.0 Flash (SM Agent YOLO Mode)

### Debug Log References

### Completion Notes List
- Implemented automatic brightness control in `GuichetModeModal`.
- Added `expo-brightness` plugin configuration to `frontend/app.json`.
- Created comprehensive tests in `frontend/src/components/GuichetModeModal.test.tsx` to verify brightness management.
- Verified high-contrast UI compliance.
- **Review Fixes Applied**:
    - Extracted brightness logic into a reusable hook `useBrightnessControl.ts`.
    - Optimized permission requests to avoid redundant system calls.
    - Added test case for permission denial.
    - Cleaned up unused imports (`Platform`).
- All tests passing (10/10).

### File List
- `frontend/app.json`
- `frontend/src/components/GuichetModeModal.tsx`
- `frontend/src/components/GuichetModeModal.test.tsx`
- `frontend/src/hooks/useBrightnessControl.ts`
