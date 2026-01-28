# Story 1.5: Déploiement Continu sur Railway

Status: done

## Story

As a **Developer**,
I want **to set up a continuous deployment pipeline to Railway**,
so that **the backend is automatically deployed and accessible via a public URL after each merge on main.**

## Acceptance Criteria

1.  **Backend Containerization**: Un `Dockerfile` multi-stage est présent à la racine du backend (Build via Maven, Run via JRE 21 alpine).
2.  **Frontend Containerization**: Un `Dockerfile` multi-stage est présent à la racine du frontend (Build via Expo Export, Run via Nginx).
3.  **Infrastructure Provisioning**: Une base de données PostgreSQL est provisionnée sur Railway.
4.  **Secrets & Config**: Toutes les variables d'environnement sensibles (Gmail API, DB Credentials) sont configurées dans Railway.
5.  **Railway CLI/Config**: Un fichier `railway.json` (ou configuration via dashboard) définit le service backend. Le frontend est configuré via son Dockerfile.
6.  **CI/CD Pipeline**: Le workflow GitHub Actions existant est enrichi pour déclencher un déploiement sur Railway uniquement après le succès des tests sur la branche `main`.
7.  **Observability**: 
    - Le backend expose un endpoint de healthcheck (ou au moins répond sur `/api/parcels`).
    - Le frontend est accessible via une URL publique.

## Tasks / Subtasks

- [x] **Infrastructure: Containerization (Backend)**
    - [x] Créer `backend/Dockerfile`.
    - [x] Créer `.dockerignore` pour optimiser le build.
- [x] **Infrastructure: Containerization (Frontend)**
    - [x] Créer `frontend/Dockerfile` (Node builder -> Nginx runner).
    - [x] Créer `frontend/.dockerignore`.
- [ ] **Cloud: Railway Setup**
    - [ ] Créer le projet sur Railway.
    - [ ] Ajouter un service PostgreSQL.
    - [ ] Ajouter le service Backend (Root: `/backend`).
    - [ ] Ajouter le service Frontend (Root: `/frontend`).
    - [ ] Configurer les variables d'environnement (Spring Datasource, Gmail API).
- [x] **DevOps: GitHub Actions**
    - [ ] Ajouter le secret `RAILWAY_TOKEN` dans GitHub.
    - [x] Modifier `.github/workflows/ci.yml` pour inclure l'étape de déploiement Railway.
- [ ] **Verification**
    - [ ] Vérifier le déploiement automatique du Backend et du Frontend après le prochain push.

## Dev Notes

### Architecture Patterns
- **Twelve-Factor App**: Utilisation stricte des variables d'environnement pour la configuration.
- **Multi-stage Build**: Garantir une image finale légère et sécurisée (pour Back et Front).

### Source Tree Components
- `backend/Dockerfile`
- `frontend/Dockerfile`
- `.github/workflows/ci.yml`
- `backend/railway.json`
- `backend/src/main/java/com/parcelflow/infrastructure/api/HealthCheckController.java`

### References
- [Source: docs/architecture.md#5. Infrastructure & DevOps Strategy]
- [Railway Documentation: Deploying a Spring Boot App](https://docs.railway.app/deploy/deployments)

## Dev Agent Record

### Implementation Plan
1.  **Containerization**: Create `Dockerfile` (multi-stage) and `.dockerignore` for `backend`.
2.  **Railway Config**: Create `backend/railway.json` for service definition and healthcheck.
3.  **CI/CD**: Update `.github/workflows/ci.yml` with a `deploy` job using Railway CLI.

### Completion Notes
- Created `backend/Dockerfile` using `maven:3.9.9-eclipse-temurin-21-alpine` for build and `eclipse-temurin:21-jre-alpine` for runtime.
- Created `backend/.dockerignore` to minimize build context.
- Created `backend/railway.json` with healthcheck on `/api/parcels`.
- Updated `.github/workflows/ci.yml` to include Railway deployment on `main` branch push.
- **Manual Actions Required**:
    - Create project and PostgreSQL service on Railway dashboard.
    - Configure environment variables (`SPRING_DATASOURCE_URL`, `GMAIL_CREDENTIALS`, etc.) in Railway.
    - Add `RAILWAY_TOKEN` as a GitHub Secret.

### Agent Model Used
Gemini 2.0 Flash (via SM Agent Bob)
