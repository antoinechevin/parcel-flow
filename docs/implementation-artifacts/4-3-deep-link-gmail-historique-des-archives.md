# Story 4.3: Deep Link Gmail & Historique des Archives

Status: ready-for-dev

<!-- Note: Validation is optional. Run validate-create-story for quality check before dev-story. -->

## Story

As a **User**,
I want **to be able to open the original email in Gmail or consult my archived parcels**,
so that **I have a fallback for complex cases or to verify past deliveries.**

## Acceptance Criteria

1. **Given** a parcel detail or "Guichet Mode" view.
2. **When** I click the "Open original email" button.
3. **Then** the native Gmail app opens directly on the corresponding email thread (using `expo-linking`).
4. **And** I can navigate to a dedicated "Archives" screen to see the history of all archived parcels.
5. **And** I can unarchive a parcel from the history if needed.

## Tasks / Subtasks

- [ ] **Infrastructure**: Configure deep linking to Gmail (AC: 3)
- [ ] **Navigation**: Create `app/archives.tsx` screen (AC: 4)
- [ ] **API**: Fetch archived parcels from backend (AC: 4)
- [ ] **Components**: Implement "Unarchive" action (AC: 5)
- [ ] **Testing**: Verify linking and navigation (AC: 1-5)

## Dev Notes

- **Deep Link**: Use `googlegmail://co?threadId=...` or similar if supported, or fallback to generic `https://mail.google.com/mail/u/0/#search/...`.
- **Navigation**: Expo Router `app/archives.tsx`.
- **Status**: Backend already supports `ARCHIVED`. Need to ensure `GET /api/parcels?status=ARCHIVED` or similar exists or works.

### Project Structure Notes

- New screen in `frontend/app/archives.tsx`.
- Update `frontend/src/hooks/useDashboard.ts` to support fetching archives.

### References

- [Source: docs/planning-artifacts/epics.md#Story 4.3]
- [Source: docs/prd.md#FR3.3, FR4.3]

## Dev Agent Record

### Agent Model Used

Gemini 2.0 Flash (SM Agent YOLO Mode)

### Debug Log References

### Completion Notes List

### File List
