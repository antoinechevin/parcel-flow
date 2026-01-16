# Story 1.1 : Initialisation Monorepo & Scaffolding "Walking Skeleton"

| Méta-donnée | Détail |
| :--- | :--- |
| **Epic** | Epic 1 : La Fondation "Clean Arch" & DevOps |
| **Priorité** | **CRITICAL (Blocker)** |
| **Estim.** | 2 Story Points |
| **Statut** | **DONE** |

## 1. User Story
**En tant que** Lead Developer (Supervisor),
**Je veux** initialiser le dépôt Monorepo avec une structure Hexagonale stricte pour le Backend et une application Expo pour le Frontend,
**Afin de** disposer d'une base de code propre, compilable et prête pour l'intégration continue.

## 2. Contexte & Contraintes Techniques
Cette story ne concerne **pas** encore le pipeline CI/CD (qui sera la Story 1.2), mais uniquement la structure du code et la vérification locale.

* **Structure Monorepo :**
    ```text
    parcel-flow/
    ├── backend/ (Spring Boot 3.3+, Java 21)
    │   ├── domain/ (PUR JAVA - Aucune dépendance Framework)
    │   ├── application/ (Use Cases, Ports)
    │   └── infrastructure/ (Adapters, Config Spring, Main)
    ├── frontend/ (React Native + Expo)
    ├── docker-compose.yml (DB locale)
    └── README.md
    ```
* **Backend :** Doit respecter l'Architecture Hexagonale. Le module `domain` doit être agnostique.
* **Frontend :** Expo Managed Workflow. Installation de React Native Paper.

## 3. Critères d'Acceptation (Definition of Done)

### A. Backend - Validation Comportementale (Gherkin)
*Le backend doit être validé par des tests d'acceptation automatisés (Cucumber).*

```gherkin
Feature: Health Check & Architecture Initialization

  Scenario: Verify Backend Bootstrap
    Given the Spring Boot application context is loaded
    When I request the status from the "health-check" endpoint
    Then I should receive a 200 OK response
    And the response body should contain "Parcel-Flow Backend is Running"

  Scenario: Verify Domain Isolation (Hexagonal Purity)
    Given the project structure
    When I analyze the dependencies of the "domain" module
    Then it should NOT depend on "spring-boot-starter"
    And it should NOT depend on "jakarta.persistence"
    And it should only depend on standard Java libraries
```

### B. Frontend - Validation Visuelle
* [x] L'application Expo s'initialise sans erreur (`npx expo start`).
* [x] L'écran d'accueil affiche un texte "Parcel-Flow : Ready" stylisé avec **React Native Paper** (vérification que la librairie est bien linkée).
* [x] Le linter (ESLint/Prettier) est configuré et passe sans erreur.

### C. Infrastructure Locale
* [x] Un fichier `docker-compose.yml` est présent à la racine et permet de lancer une base PostgreSQL 16 (vide pour l'instant, mais connectable).

## 4. Tâches Techniques (Pour l'Agent Dev)

1.  **[x] Initialisation Git & Monorepo :**
    * Créer le dossier racine et initialiser git.
    * Créer le `.gitignore` global (ignorant `node_modules`, `target`, `.idea`, etc.).

2.  **[x] Scaffolding Backend (Maven/Gradle) :**
    * Générer un projet Multi-module (Parent + 3 sous-modules : `domain`, `application`, `infrastructure`).
    * **Module Domain :** `pom.xml` vide de dépendances Spring. Juste JUnit/AssertJ.
    * **Module Infrastructure :** Contient la dépendance Spring Boot Starter Web et dépend de `application`.
    * **Module Application :** Dépend de `domain`.
    * Implémenter un endpoint REST simple (`/api/health`) dans `infrastructure` pour valider le démarrage.
    * Mettre en place Cucumber pour exécuter le Gherkin ci-dessus.

3.  **[x] Scaffolding Frontend :**
    * Initialiser avec `npx create-expo-app frontend --template blank-typescript`.
    * Installer `react-native-paper` et ses dépendances.
    * Configurer le `PaperProvider` dans `_layout.tsx` ou `App.tsx`.
    * Créer une page simple affichant le message de validation.

4.  **[x] Documentation :**
    * Créer un `README.md` racine expliquant comment lancer le Back et le Front localement.

## 5. Dev Agent Record

### File List
- `.gitignore`
- `README.md`
- `docker-compose.yml`
- `backend/pom.xml`
- `backend/domain/pom.xml`
- `backend/application/pom.xml`
- `backend/infrastructure/pom.xml`
- `backend/infrastructure/src/main/java/com/parcelflow/infrastructure/HealthController.java`
- `backend/infrastructure/src/test/java/com/parcelflow/infrastructure/HealthStepDefinitions.java`
- `backend/infrastructure/src/test/resources/features/health.feature`
- `frontend/package.json`
- `frontend/App.tsx`
- `frontend/app.json`

### Change Log
- Initialized monorepo structure with git and .gitignore
- Created Spring Boot multi-module backend (domain, application, infrastructure)
- Implemented Hexagonal Architecture constraints in pom.xml files
- Added HealthController and Cucumber tests for backend validation
- Initialized Expo frontend with React Native Paper
- Configured frontend App.tsx to display "Parcel-Flow : Ready" (Restored during AI Review)
- Added docker-compose.yml for PostgreSQL 16
- Updated documentation in README.md

### AI Code Review Fixes (2026-01-16)
- **AC Fix**: Restored "Parcel-Flow : Ready" text in `DashboardScreen.tsx` to satisfy Story 1.1 requirements.
- **Code Quality**: Refactored `HealthStepDefinitions.java` to robustly locate `pom.xml` by walking up the directory tree to find the project root, fixing the brittle relative path issue.
- **Verification**: Verified that despite workspace pollution from Story 1.3, the foundational 1.1 components remain functional.
- **Note**: Acknowledged presence of untracked files from concurrent development. Atomic delivery is recommended for future stories.
