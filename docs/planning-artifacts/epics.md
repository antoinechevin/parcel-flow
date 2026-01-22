---
stepsCompleted:
  - step-01-validate-prerequisites
  - step-02-design-epics
  - step-03-create-stories
  - step-04-final-validation
inputDocuments:
  - docs/prd.md
  - docs/architecture.md
---

# Parcel-Flow - Epic Breakdown

## Overview

This document provides the complete epic and story breakdown for Parcel-Flow, decomposing the requirements from the PRD, UX Design if it exists, and Architecture requirements into implementable stories.

## Requirements Inventory

### Functional Requirements

FR1: Ingestion Intelligente (The "Pull" Strategy)
- FR1.1: Le système doit se connecter à l'API Gmail via OAuth2 (Scope Readonly restreint).
- FR1.2: Le système doit interroger périodiquement (Job planifié) les emails correspondant à une requête stricte (ex: `subject:(colis OR livraison) is:unread`).
- FR1.3: Le système doit marquer les emails comme "traités" (ou lus) pour éviter les doublons.

FR2: Extraction par IA (Le Cœur du Système)
- FR2.1 - Sanitization: Avant tout envoi à l'IA, le système DOIT anonymiser les données sensibles via Regex (suppression des adresses postales, numéros de téléphone) pour respecter le principe de Data Minimization.
- FR2.2 - Prompting Contextuel: Le système utilise un prompt générique pour extraire : Code de retrait, Transporteur, Date limite, Lieu.
- FR2.3 - Gestion d'Erreur: Si le score de confiance de l'IA est bas, le colis est créé avec un statut "A vérifier" et un lien vers l'email.

FR3: Consultation Mobile "Zéro Stress"
- FR3.1: Liste triée par Urgence (Date limite la plus proche en premier).
- FR3.2 - Mode Guichet: Au clic, afficher le Code de Retrait en très gros caractères (50% écran) et forcer la luminosité au maximum (via API Native).
- FR3.3 - Deep Link de Secours (Fallback): Bouton "Ouvrir l'email original" qui lance l'application Gmail native sur le message exact.

FR4: Cycle de Vie & Archives
- FR4.1: L'utilisateur peut archiver un colis manuellement ("Swipe-to-archive").
- FR4.2: Une action d'archivage déclenche un Toast "Annuler" (Undo) pendant 3 à 5 secondes.
- FR4.3: Accès à l'historique des colis archivés.

### NonFunctional Requirements

NFR1: Privacy & Sécurité
- NFR1.1 - Éphémérité: Le corps brut de l'email ne doit jamais être persisté en base de données.
- NFR1.2 - Politique de Rétention: Les colis archivés sont supprimés définitivement de la base de données après 30 jours (Job Automatique).
- NFR1.3 - Isolation: Le token d'accès Gmail est stocké de manière sécurisée (Vault ou Env Var chiffrée).

NFR2: Robustesse Technique (Clean Arch)
- NFR2.1: Le Domaine métier doit être pur (aucune dépendance Spring, Gmail ou SQL).
- NFR2.2: Les interactions externes sont isolées dans des "Adapters".

NFR3: Expérience Mobile
- NFR3.1 - Offline Read: L'application doit mettre en cache la dernière liste connue pour consultation sans réseau.
- NFR3.2 - Performance: Lancement de l'application en < 2 secondes (optimisation JS bundle).

### Additional Requirements

From Architecture:
- **Monorepo Structure**: Implement strict folder structure (backend, frontend, infra).
- **Backend Architecture**: Hexagonal Architecture (Ports & Adapters). Domain must be pure Java.
- **Backend Tech**: Java 21, Spring Boot 3.3+, Spring AI.
- **Frontend Tech**: React Native (Expo), React Native Paper, Zustand.
- **Testing**: ATDD with Cucumber (Gherkin), Testcontainers, ArchUnit.
- **Infrastructure**: PostgreSQL, Docker, GitHub Actions (CI/CD).
- **API**: REST Controllers.

From UX (Inferred):
- **UI Framework**: React Native Paper (Material Design 3).
- **Theme**: Strict use of Design Tokens (Colors, Typography) in `theme.ts`.
- **Dark Mode**: Native support.
- **Dashboard**: FlashList for performance, Traffic light color coding for urgency.
- **Guichet Mode**: Max brightness control, large text.

### FR Coverage Map

FR1.1: Epic 1 - Connexion API Gmail OAuth2
FR1.2: Epic 1 - Polling périodique des emails
FR1.3: Epic 1 - Marquage des emails traités
FR2.1: Epic 2 - Sanitization et anonymisation
FR2.2: Epic 2 - Extraction par Gemini
FR2.3: Epic 2 - Gestion d'erreur et fallback
FR3.1: Epic 3 - Liste triée par urgence
FR3.2: Epic 4 - Mode Guichet (Luminosité, Gros caractères)
FR3.3: Epic 4 - Deep Link vers Gmail
FR4.1: Epic 4 - Archivage manuel
FR4.2: Epic 4 - Toast "Undo"
FR4.3: Epic 4 - Historique des archives
NFR1.1: Epic 2 - Non-persistance du corps d'email
NFR1.2: Epic 4 - Suppression automatique après 30 jours
NFR1.3: Epic 1 - Sécurité du Token
NFR2.1: Epic 1 - Architecture Hexagonale pure
NFR2.2: Epic 1 - Isolation des Adapters
NFR3.1: Epic 3 - Mode Offline
NFR3.2: Epic 3 - Performance lancement

## Epic List

### Epic 1: La Fondation "Clean Arch" & Connexion Gmail
Mettre en place l'infrastructure technique (Monorepo, CI/CD, Architecture Hexagonale) et permettre au système de se connecter à Gmail de manière sécurisée pour identifier les emails de colis.
**FRs covered:** FR1.1, FR1.2, FR1.3, NFR1.3, NFR2.1, NFR2.2

### Epic 2: Le Cœur d'Extraction IA & Protection de la Vie Privée
Implémenter la logique d'extraction des données par Gemini en garantissant l'anonymisation stricte des données sensibles avant traitement.
**FRs covered:** FR2.1, FR2.2, FR2.3, NFR1.1

### Epic 3: Le Dashboard Mobile & Consultation "Zéro Stress"
Créer l'interface mobile pour afficher la liste des colis triée par urgence, avec une gestion performante et un accès hors ligne.
**FRs covered:** FR3.1, NFR3.1, NFR3.2

### Epic 4: L'Expérience "Guichet" & Cycle de Vie
Peaufiner l'expérience utilisateur au point de retrait (luminosité max, deep link) et gérer le cycle de vie complet des données (archivage, suppression automatique).
**FRs covered:** FR3.2, FR3.3, FR4.1, FR4.2, FR4.3, NFR1.2

## Epic 1: La Fondation "Clean Arch" & Connexion Gmail

Mettre en place l'infrastructure technique (Monorepo, CI/CD, Architecture Hexagonale) et permettre au système de se connecter à Gmail de manière sécurisée pour identifier les emails de colis.

### Story 1.1: Initialisation Monorepo & Scaffolding Walking Skeleton

As a Lead Developer,
I want to initialize the Monorepo with the strict folder structure and a "Walking Skeleton" of the Backend and Frontend,
So that the team can start working on a solid technical foundation that respects the defined architecture.

**Acceptance Criteria:**

**Given** an empty git repository
**When** I run the setup scripts
**Then** I should see the folders `backend`, `frontend`, and `infra` created.
**And** the backend project uses Spring Boot 3.3+ and Java 21.
**And** the frontend project uses Expo and React Native Paper.
**And** the CI pipeline workflow runs and passes for both.

### Story 1.2.1: Visualisation Liste Simple (MVP)

As a User,
I want to see a list of my active parcels on my phone,
so that I can track what I need to pick up.

**Acceptance Criteria:**

- Backend: GET /api/parcels returns a flat list of parcels.
- Frontend: ParcelListScreen displays parcels in a flat list.
- ATDD: DashboardList.feature passes.

### Story 1.2.2: Regroupement par Point de Retrait

As a User,
I want my parcels grouped by pickup location on my dashboard,
so that I can see where I need to go.

**Acceptance Criteria:**

- Backend: RetrieveDashboardUseCase aggregates parcels by PickupPoint.
- Frontend: LocationGroupCard displays grouped parcels.
- ATDD: DashboardAggregation.feature passes.

### Story 1.2.3: Indicateurs d'Urgence

As a User,
I want to see urgency indicators on my pickup locations,
so that I don't miss a deadline.

**Acceptance Criteria:**

- Backend: Groups are sorted by Urgency (HIGH first).
- Frontend: LocationGroupCard displays colored urgency indicators.
- ATDD: DashboardUrgency.feature passes.

### Story 1.3: Adapter Gmail (Client d'Infrastructure)

As a Developer,
I want a Gmail client that can list and read unread delivery emails,
so that the system can fetch data from the outside world using a standardized Port.

**Acceptance Criteria:**

- MailSourcePort interface exists.
- GmailInboundAdapter implements MailSourcePort.
- Successfully connects to Gmail API via OAuth2.

### Story 1.4: Polling Job & Orchestration

As a System,
I want to periodically trigger the mail checking process,
so that new delivery emails are automatically processed without manual intervention.

**Acceptance Criteria:**

- Spring @Scheduled task runs at configurable interval.
- Job calls MailSourcePort and markAsRead after processing.

## Epic 2: Le Cœur d'Extraction IA & Protection de la Vie Privée

Implémenter la logique d'extraction des données par Gemini en garantissant l'anonymisation stricte des données sensibles avant traitement.

### Story 2.1: Sanitization & Anonymisation (Data Minimization)

As a Privacy-Conscious User,
I want my sensitive personal data (phone numbers, addresses) removed from the email body,
So that only necessary metadata is processed by the AI, respecting my privacy.

**Acceptance Criteria:**

**Given** an email body containing a phone number or address
**When** the sanitization service runs
**Then** sensitive data is replaced with masking placeholders (e.g., `[PHONE_REMOVED]`).
**And** the original raw email body is NOT saved to the database.

### Story 2.2: Extraction de Métadonnées avec Gemini

As a User,
I want the system to extract the tracking code, carrier, and expiration date from the sanitized email,
So that structured parcel information is created automatically.

**Acceptance Criteria:**

**Given** a sanitized email text
**When** sent to the Gemini Adapter via Spring AI
**Then** a structured `ParcelMetadata` object is returned (Code, Carrier, Date, Location).
**And** a new Parcel is created in the system with this extracted data.

### Story 2.3: Gestion d'Erreur & Fallback Extraction

As a User,
I want the system to flag parcels that couldn't be fully extracted,
So that I don't miss a delivery due to an AI error.

**Acceptance Criteria:**

**Given** an email with low AI extraction confidence
**When** the parcel is created
**Then** its status is set to "TO_VERIFY".
**And** the parcel record includes a reference/link to the original email for manual check.

## Epic 3: Le Dashboard Mobile & Consultation "Zéro Stress"

Créer l'interface mobile pour afficher la liste des colis triée par urgence, avec une gestion performante et un accès hors ligne.

### Story 3.1: Dashboard Mobile avec Liste Triée

As a User,
I want to see my active parcels in a list sorted by urgency (soonest expiration date first),
So that I know exactly which parcel to pick up first.

**Acceptance Criteria:**

**Given** a list of active parcels
**When** viewing the Dashboard
**Then** parcels are sorted by expiration date (descending).
**And** visual indicators (Traffic light colors) show the level of urgency.
**And** the list scrolling is performant (using FlashList).

### Story 3.2: Cache Local & Mode Offline

As a User,
I want to access my parcel list even when I have no internet connection,
So that I am never blocked at the counter.

**Acceptance Criteria:**

**Given** a previously loaded parcel list
**When** the device is offline
**Then** the application displays the cached list.
**And** an "Offline Mode" indicator is visible to the user.

## Epic 4: L'Expérience "Guichet" & Cycle de Vie

Peaufiner l'expérience utilisateur au point de retrait (luminosité max, deep link) et gérer le cycle de vie complet des données (archivage, suppression automatique).

### Story 4.1: Mode Guichet (Luminosité & Zoom Code)

As a User,
I want to tap a parcel and see its tracking code in giant characters with maximum screen brightness,
So that the shopkeeper can easily scan or read it.

**Acceptance Criteria:**

**Given** a selected parcel
**When** entering "Counter Mode"
**Then** the tracking code is displayed in large font (approx 50% screen).
**And** the device screen brightness is set to 100%.
**And** brightness returns to previous level when closing the mode.

### Story 4.2: Archivage avec Option Annuler (Undo)

As a User,
I want to swipe a parcel to archive it and have a few seconds to undo my action,
So that I don't lose data by mistake.

**Acceptance Criteria:**

**Given** an active parcel
**When** I perform a "Swipe-to-archive" gesture
**Then** the parcel is removed from the active list.
**And** a Toast appears for 5 seconds with an "Undo" button.
**And** clicking "Undo" restores the parcel to the active list.
**And** I can navigate to a separate view to see the list of archived parcels.

### Story 4.3: Deep Link vers Gmail Original

As a User,
I want a button to open the original email in my Gmail app,
So that I can check details that were not extracted.

**Acceptance Criteria:**

**Given** a parcel detail view
**When** I click "Open original email"
**Then** the native Gmail application opens directly to the specific email thread.

### Story 4.4: Suppression Automatique & Purge des Données

As a System,
I want to permanently delete archived parcels after 30 days,
So that I respect data retention policies.

**Acceptance Criteria:**

**Given** parcels archived for more than 30 days
**When** the daily purge job runs
**Then** these parcels are permanently deleted from the database.