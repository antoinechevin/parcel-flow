---
stepsCompleted:
  - step-01-validate-prerequisites
  - step-02-design-epics
  - step-03-create-stories
  - step-04-final-validation
inputDocuments:
  - docs/prd.md
  - docs/architecture.md
  - docs/planning-artifacts/ux-design-specification.md
  - docs/implementation-artifacts/
---

# Parcel-Flow - Epic Breakdown

## Overview

This document provides the complete epic and story breakdown for Parcel-Flow, decomposing the requirements from the PRD, UX Design, and Architecture requirements into implementable stories. It tracks the progress from initial scaffolding to advanced mobile features.

## Requirements Inventory

### Functional Requirements

- FR1.1: Connexion à l'API Gmail via OAuth2 (Scope Readonly).
- FR1.2: Interrogation périodique (Job) avec requêtes ciblées par provider.
- FR1.3: Marquage des emails traités comme "lus".
- FR2.2: Extraction déterministe (Regex) pour Chronopost, Mondial Relay, Vinted Go.
- FR2.3: Gestion des échecs d'extraction (Statut TO_VERIFY).
- FR3.1: Liste mobile triée par Urgence (Date limite la plus proche).
- FR3.2: Mode Guichet (Gros caractères + Luminosité Forcée à 100%).
- FR3.3: Deep Link "Ouvrir l'email original" vers l'app Gmail.
- FR4.1: Archivage manuel via geste "Swipe-to-archive".
- FR4.2: Feedback "Undo" (Toast) pendant 3-5 secondes après archivage.
- FR4.3: Vue Historique des colis archivés.
- FR4.4: Gestion automatique de l'expiration (Statut EXPIRED).

### NonFunctional Requirements

- NFR1.1: Non-persistance du corps brut de l'email (Éphémérité).
- NFR1.2: Purge automatique des archives après 30 jours.
- NFR1.3: Stockage sécurisé des tokens OAuth.
- NFR2.1: Pureté du Domaine (Pas de Spring/JPA/Reflection dans `domain`).
- NFR2.2: Isolation stricte via Adapters (Hexagonal Architecture).
- NFR3.1: Mode Offline (Cache local de la dernière liste).
- NFR3.2: Performance de lancement (< 2 secondes).

### Additional Requirements

**From Architecture:**
- Java 21 / Spring Boot 3.3.
- React Native Expo SDK 52.
- ArchUnit pour garantir la pureté hexagonale.
- ATDD piloté par Gherkin (Cucumber JVM).
- PostgreSQL 16.
- Structure Monorepo stricte.

**From UX Design:**
- Thème Material Design 3 (React Native Paper).
- Mode Dark OLED (#000000).
- Système de couleur "Traffic Light" pour l'urgence.
- Opération à une main (Zone interactive en bas).
- Feedback haptique lors des actions clés (Swipe, Refresh).

### FR Coverage Map

FR1.1: Epic 1 - Connexion OAuth2 Gmail
FR1.2: Epic 1 - Polling périodique des emails
FR1.3: Epic 1 - Marquage des emails traités
FR2.2: Epic 2 - Extraction Regex (Chronopost, MR, Vinted)
FR2.3: Epic 2 - Statut TO_VERIFY en cas d'échec
FR3.1: Epic 3 - Tri par urgence sur le Dashboard
FR3.2: Epic 4 - Mode Guichet (Luminosité & Zoom)
FR3.3: Epic 4 - Deep Link vers Gmail
FR4.1: Epic 4 - Geste Swipe-to-archive
FR4.2: Epic 4 - Toast Undo après archivage
FR4.3: Epic 4 - Vue Historique des archives
FR4.4: Epic 3 - Gestion automatique de l'expiration

## Epic List

### Epic 1 : Ingestion Automatique des Colis
Permettre au système de se connecter en toute sécurité au compte Gmail de l'utilisateur pour identifier et récupérer automatiquement les emails de livraison dès qu'ils arrivent.
**FRs couverts :** FR1.1, FR1.2, FR1.3

### Epic 2 : Extraction Intelligente & Confidentialité
Transformer le chaos des emails bruts en données structurées (Code, Lieu, Date limite) par transporteur, tout en garantissant que le contenu privé des emails est immédiatement supprimé après analyse.
**FRs couverts :** FR2.2, FR2.3, NFR1.1

### Epic 3 : Dashboard Mobile "Zéro Stress"
Offrir une vue d'ensemble des colis actifs, triés par priorité d'urgence et accessibles instantanément, même dans les zones sans réseau (point relais en sous-sol).
**FRs couverts :** FR3.1, FR4.4, NFR3.1, NFR3.2

## Epic 1 : Ingestion Automatique des Colis
Permettre au système de se connecter en toute sécurité au compte Gmail de l'utilisateur pour identifier et récupérer automatiquement les emails de livraison dès qu'ils arrivent.
**FRs couverts :** FR1.1, FR1.2, FR1.3

### Story 1.1 : Connexion OAuth2 Gmail (Standard)

As a System,
I want to connect to Gmail API with a restricted readonly scope,
So that I can access delivery emails without compromising user privacy.

**Acceptance Criteria:**

**Given** valid OAuth2 credentials
**When** the system requests access
**Then** a secure token is obtained and stored safely
**And** the scope is limited to readonly access to emails

### Story 1.2 : Polling ciblé & Marquage (Job)

As a System,
I want to poll Gmail for specific queries (e.g. `from:chronopost`) and mark them as read,
So that I only process relevant emails once.

**Acceptance Criteria:**

**Given** a list of provider queries (Chronopost, Mondial Relay, Vinted)
**When** the scheduled job runs
**Then** matching unread emails are fetched
**And** processed emails are marked as "read" or "processed" in Gmail to avoid duplicates

### Story 1.3 : Déploiement Continu Mobile (EAS & GitHub Actions)

As a Developer,
I want to automate the mobile app deployment through GitHub Actions using EAS,
So that every change on the main branch is automatically buildable or updatable without manual intervention.

**Acceptance Criteria:**

**Given** a push to the main branch
**When** the GitHub Action runs
**Then** it executes `eas update` for over-the-air (OTA) updates on the production channel
**And** it supports manual triggers for full `eas build` when native changes are detected
**And** it uses a secure `EXPO_TOKEN` stored in GitHub Secrets

## Epic 2 : Extraction Intelligente & Confidentialité
Transformer le chaos des emails bruts en données structurées (Code, Lieu, Date limite) par transporteur, tout en garantissant que le contenu privé des emails est immédiatement supprimé après analyse.
**FRs couverts :** FR2.2, FR2.3, NFR1.1

### Story 2.1 : Extraction déterministe par Provider

As a System,
I want to use specific Regex strategies for Chronopost, Mondial Relay and Vinted Go,
So that I extract Tracking Code, Pickup Location, and Deadline accurately.

**Acceptance Criteria:**

**Given** a raw email text from a recognized provider
**When** the corresponding provider strategy is applied
**Then** a `Parcel` record is created with extracted metadata (Code, Location, Deadline)
**And** the raw email body is NEVER saved in the database to ensure privacy

### Story 2.2 : Gestion des échecs (Statut TO_VERIFY)

As a System,
I want to flag parcels as `TO_VERIFY` if extraction fails,
So that the user knows a manual check is needed.

**Acceptance Criteria:**

**Given** an email that doesn't match any known Regex pattern
**When** the processing job runs
**Then** a parcel record is created with status `TO_VERIFY`
**And** the record includes a reference/link to the original email for manual consultation

## Epic 3 : Dashboard Mobile "Zéro Stress"
Offrir une vue d'ensemble des colis actifs, triés par priorité d'urgence et accessibles instantanément, même dans les zones sans réseau (point relais en sous-sol).
**FRs couverts :** FR3.1, FR4.4, NFR3.1, NFR3.2

### Story 3.1 : Dashboard Trié par Urgence

As a User,
I want to see my parcels sorted by deadline with clear visual priority,
So that I can plan my pickup tour efficiently.

**Acceptance Criteria:**

**Given** a list of active parcels
**When** viewing the Dashboard
**Then** parcels are grouped by location (PickupPoint)
**And** groups are sorted by the most urgent parcel deadline (Soonest first)
**And** "Expired" parcels are displayed at the bottom of the list with reduced opacity
**And** "Traffic Light" colors (Red/Orange/Green) are applied based on the deadline

### Story 3.2 : Cache Local (Offline Mode)

As a User,
I want to access my last synced list even without an internet connection,
So that I am never blocked at the counter in a "dead zone".

**Acceptance Criteria:**

**Given** a previously successful sync
**When** the device is offline
**Then** the application displays the cached version of the parcel list
**And** an "Offline Mode" banner or indicator is clearly visible
**And** the UI remains responsive and allows opening the "Guichet Mode" for cached parcels

## Epic 4 : Expérience Guichet & Fin de Cycle
Optimiser le moment critique du retrait (Luminosité max, codes géants) et permettre de clore proprement le cycle de vie du colis par un geste simple et réversible (Swipe-to-archive).
**FRs couverts :** FR3.2, FR3.3, FR4.1, FR4.2, FR4.3, NFR1.2

### Story 4.1 : Mode Guichet (Luminosité Native & Zoom)

As a User,
I want the screen to automatically switch to maximum brightness when showing a withdrawal code,
So that the shopkeeper's scanner can read it without friction.

**Acceptance Criteria:**

**Given** I open the "Guichet Mode" for a specific parcel
**When** the modal becomes visible
**Then** the device screen brightness is set to 100% (using expo-brightness)
**And** the withdrawal code is displayed in large, high-contrast characters
**And** the brightness returns to its original system level when the modal is closed

### Story 4.2 : Archivage par Swipe & Undo

As a User,
I want to archive a parcel with a natural swipe gesture and have a safety net to undo it,
So that my list stays clean with minimal effort and no fear of mistakes.

**Acceptance Criteria:**

**Given** I am on the dashboard list
**When** I perform a "Swipe-to-left" gesture on a parcel item
**Then** a subtle haptic feedback is triggered
**And** the parcel is immediately removed from the active list
**And** a Snackbar appears for 5 seconds with an "UNDO" (ANNULER) button
**And** clicking "UNDO" restores the parcel to its exact previous position in the list

### Story 4.3 : Deep Link Gmail & Historique des Archives

As a User,
I want to be able to open the original email in Gmail or consult my archived parcels,
So that I have a fallback for complex cases or to verify past deliveries.

**Acceptance Criteria:**

**Given** a parcel detail or "Guichet Mode" view
**When** I click the "Open original email" button
**Then** the native Gmail app opens directly on the corresponding email thread
**And** I can navigate to a dedicated "Archives" screen to see the history of all archived parcels (FR4.3)
