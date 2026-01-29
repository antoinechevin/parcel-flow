# Story 3.0.S: Verrouillage de l'Accès Public (Security MVP)

Status: done

## Story

...

## Dev Agent Record

### Implementation Plan
1. **Backend**: Implemented `ApiKeyFilter` using `OncePerRequestFilter`.
2. **Frontend**: Implemented `LoginScreen` and `useAuthStore` with persistence.
3. **Tests**: Added `Security.feature` and updated `CucumberConfiguration`.

### Completion Notes
- Backend security implemented via `ApiKeyFilter` protecting `/api/**`.
- **HTTPS Enforcement**: Added `SECURITY_REQUIRE_HTTPS` check (via `X-Forwarded-Proto`) to protect API keys in transit.
- Error responses follow RFC 7807 (application/problem+json).
- Frontend now redirects to `/login` if not authenticated.
- **Architecture**: Centralized `API_URL` in `frontend/src/core/api/config.ts`.
- **Mobile Compatibility**: Fixed by using `@react-native-async-storage/async-storage`.
- Password is persisted securely and used in all API calls via `X-API-KEY` header.
- All tests (Backend Cucumber & Frontend Jest) are passing 100%.

### File List
- `backend/src/main/java/com/parcelflow/infrastructure/api/security/ApiKeyFilter.java` (New)
- `backend/src/test/java/com/parcelflow/steps/SecuritySteps.java` (New)
- `backend/src/test/resources/features/Security.feature` (Modified)
- `backend/src/test/java/com/parcelflow/CucumberTest.java` (Modified)
- `backend/src/test/java/com/parcelflow/steps/CucumberConfiguration.java` (Modified)
- `backend/src/test/resources/application-test.properties` (Modified)
- `frontend/src/core/api/config.ts` (New)
- `frontend/src/core/auth/authStore.ts` (New)
- `frontend/app/login.tsx` (New)
- `frontend/app/_layout.tsx` (Modified)
- `frontend/src/hooks/useDashboard.ts` (Modified)
- `frontend/src/NominalFlow.test.tsx` (Modified)

### Change Log
- 2026-01-29: Initial implementation completed. (Dev Agent)
- 2026-01-29: Code Review fixes applied (AsyncStorage, RFC 7807, Test reliability, HTTPS check, API URL centralization). (Dev Agent)
- 2026-01-29: CI Fixes: Resolved 401 failures in ParcelControllerTest by adding missing security headers. (Dev Agent)

