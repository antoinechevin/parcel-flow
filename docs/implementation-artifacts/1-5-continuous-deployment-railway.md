# Story 1.5: Déploiement Continu sur Railway

Status: done

## Story

As a **Developer**,
I want **to set up a continuous deployment pipeline to Railway**,
so that **the backend is automatically deployed and accessible via a public URL after each merge on main.**

## Acceptance Criteria

1.  **Containerization**: Un `Dockerfile` multi-stage est présent à la racine du backend (Build via Maven, Run via JRE 21 alpine).
2.  **Infrastructure Provisioning**: Une base de données PostgreSQL est provisionnée sur Railway.
3.  **Secrets & Config**: Toutes les variables d'environnement sensibles (Gmail API, DB Credentials) sont configurées dans Railway.
4.  **Railway CLI/Config**: Un fichier `railway.json` (ou configuration via dashboard) définit le service backend.
5.  **CI/CD Pipeline**: Le workflow GitHub Actions existant est enrichi pour déclencher un déploiement sur Railway uniquement après le succès des tests sur la branche `main`.
6.  **Observability**: Le backend expose un endpoint de healthcheck (ou au moins répond sur `/api/parcels`) une fois déployé.

## Tasks / Subtasks

- [x] **Infrastructure: Containerization**
    - [x] Créer `backend/Dockerfile`.
    - [x] Créer `.dockerignore` pour optimiser le build.
- [ ] **Cloud: Railway Setup**
    - [ ] Créer le projet sur Railway.
    - [ ] Ajouter un service PostgreSQL.
    - [ ] Configurer les variables d'environnement (Spring Datasource, Gmail API).
- [x] **DevOps: GitHub Actions**
    - [ ] Ajouter le secret `RAILWAY_TOKEN` dans GitHub.
    - [x] Modifier `.github/workflows/ci.yml` pour inclure l'étape de déploiement Railway.
- [ ] **Verification**
    - [ ] Vérifier le déploiement automatique après le prochain push.

## Dev Notes

### Architecture Patterns
- **Twelve-Factor App**: Utilisation stricte des variables d'environnement pour la configuration.
- **Multi-stage Build**: Garantir une image finale légère et sécurisée.

### Source Tree Components
- `backend/Dockerfile`
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
