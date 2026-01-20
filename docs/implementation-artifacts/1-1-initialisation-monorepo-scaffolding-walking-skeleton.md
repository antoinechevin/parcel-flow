# Story 1.1: Initialisation Monorepo & Scaffolding Walking Skeleton

Status: ready-for-dev

## Story

As a **Lead Developer**,
I want **to initialize the Monorepo with a strict folder structure and a "Walking Skeleton"**,
so that **the team can start working on a solid technical foundation that respects the Hexagonal Architecture.**

## Acceptance Criteria

1. **Monorepo Structure**: The following directories exist: `backend/`, `frontend/`, `infra/`, `docs/`, `.devcontainer/`.
2. **Devcontainer Configuration**: `devcontainer.json` is configured to support Java 21, Node.js 22, and Docker-in-Docker.
3. **Backend Skeleton**: 
    - Spring Boot 3.3+ project initialized in `backend/`.
    - Java 21 configured in `pom.xml`.
    - Basic "Hello World" REST endpoint available.
    - ArchUnit dependency added and first test checking for "No Spring in Domain" exists.
4. **Frontend Skeleton**:
    - Expo SDK 52 project initialized in `frontend/`.
    - React Native Paper installed and configured with a basic theme.
    - Expo Router configured with a default index page.
5. **CI/CD Foundation**: `.github/workflows/ci.yml` exists and performs compilation for both modules.
6. **Docker Foundation**: `docker-compose.yml` exists with a PostgreSQL 16 service.

## Tasks / Subtasks

- [ ] **Infrastructure Setup**
  - [ ] Initialize git repository (already done, but verify state).
  - [ ] Create folder structure (including `.devcontainer/`).
  - [ ] Configure `devcontainer.json` for GitHub Codespaces.
  - [ ] Create `docker-compose.yml` with Postgres 16.
- [ ] **Backend Initialization**
  - [ ] Scaffold Spring Boot project (Java 21, Maven).
  - [ ] Add dependencies: Spring Web, Spring AI (Gemini), Spring Data JPA, ArchUnit, Testcontainers.
  - [ ] Implement `ArchitectureTest.java` (ArchUnit) to enforce Domain Purity.
  - [ ] Create a "Heartbeat" Controller.
- [ ] **Frontend Initialization**
  - [ ] Scaffold Expo app (TypeScript).
  - [ ] Install `react-native-paper`, `zustand`, `expo-router`.
  - [ ] Setup basic Material 3 Theme provider.
- [ ] **Continuous Integration**
  - [ ] Create `.github/workflows/build.yml` for Maven and Expo builds.

## Dev Notes

### Architecture Compliance
- **Domain Purity**: Ensure the `backend/src/main/java/com/parcelflow/domain` package is created (even if empty) to allow ArchUnit to scan it.
- **Hexagonal Boundaries**: No JPA annotations in the domain.

### Testing
- Verify that `mvn test` runs successfully.
- Verify that `npm test` (or `jest`) runs successfully.

### References
- [Source: docs/architecture.md#Section 6] - Monorepo Structure
- [Source: docs/project-context.md] - Technology Stack

## Dev Agent Record

### Agent Model Used
Gemini 2.0 Flash