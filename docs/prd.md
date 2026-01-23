# Parcel-Flow Product Requirements Document (PRD)

| Méta-donnée | Détail |
| :--- | :--- |
| **Projet** | Parcel-Flow (Portfolio Showcase) |
| **Version** | 1.0.0 (Validée pour Dév) |
| **Statut** | **APPROVED** |
| **PM** | John (Agent BMad) |
| **Tech Lead** | Utilisateur (Supervisor) |

## 1. Objectifs et Contexte

### Objectifs
* **Utilité Produit (Pain Killer) :** Résoudre la friction critique de la récupération de colis (notamment Vinted/Mondial Relay) en centralisant les codes et les **dates limites** pour éviter les retours involontaires.
* **Expérience Utilisateur "Guichet" :** Fournir un mode "Zéro Stress" au point de retrait : accès hors ligne, luminosité max, code en gros caractères, sans fouille dans l'historique mail.
* **Démonstration d'Expertise (Choix Conscients) :** Utiliser ce périmètre fonctionnel réduit comme un "Dojo" pour implémenter une **Architecture Hexagonale** stricte et du **Spring Boot 3.x** moderne. Le but est de prouver la capacité à gérer la complexité logicielle, quitte à "sur-ingénier" volontairement le backend pour les besoins du portfolio.
* **Privacy by Design :** Appliquer un principe de minimisation stricte des données. Le système agit comme un "passe-plat" : il analyse, extrait les métadonnées utiles (code, lieu, date), et supprime immédiatement le contenu brut de l'email.

### Contexte
L'expérience logistique actuelle pour les particuliers est dégradée. Les informations sont noyées dans des flux d'emails génériques qui ne mettent pas en avant l'urgence (**Date Limite de Garde**).
Cela entraîne deux problèmes majeurs :
1.  **Le retour expéditeur accidentel :** Oubli du colis car l'email de rappel est perdu.
2.  **La friction sociale au guichet :** Difficulté à retrouver le bon QR code ou PIN au moment critique.

**Parcel-Flow** résout cela via une approche hybride : une IA (Gemini 3 via Spring AI) pour structurer le chaos des emails entrants, et une app mobile réactive (React Native) pour la restitution.

### Historique des Modifications (Change Log)
| Date | Version | Description | Auteur |
|------|---------|-------------|--------|
| 2026-01-15 | 1.0 | Version validée avec stratégie ATDD et React Native Paper | John (PM) |

---

## 2. Exigences (Requirements)

### 2.1 Exigences Fonctionnelles (FR)

#### FR1 : Ingestion Intelligente (The "Pull" Strategy)
* **FR1.1 :** Le système doit se connecter à l'API Gmail via OAuth2 (Scope Readonly restreint).
* **FR1.2 :** Le système doit interroger périodiquement (Job planifié) les emails correspondant à une requête stricte (ex: `subject:(colis OR livraison) is:unread`).
* **FR1.3 :** Le système doit marquer les emails comme "traités" (ou lus) pour éviter les doublons.

#### FR2 : Extraction par IA (Le Cœur du Système)
* **FR2.2 - Prompting Contextuel :** Le système utilise un prompt générique pour extraire : Code de retrait, Transporteur, Date limite, Lieu.
* **FR2.3 - Gestion d'Erreur :** Si le score de confiance de l'IA est bas, le colis est créé avec un statut "A vérifier" et un lien vers l'email.

#### FR3 : Consultation Mobile "Zéro Stress"
* **FR3.1 :** Liste triée par **Urgence** (Date limite la plus proche en premier).
* **FR3.2 - Mode Guichet :** Au clic, afficher le Code de Retrait en très gros caractères (50% écran) et forcer la luminosité au maximum (via API Native).
* **FR3.3 - Deep Link de Secours (Fallback) :** Bouton "Ouvrir l'email original" qui lance l'application Gmail native sur le message exact.

#### FR4 : Cycle de Vie & Archives
* **FR4.1 :** L'utilisateur peut archiver un colis manuellement ("Swipe-to-archive").
* **FR4.2 :** Une action d'archivage déclenche un Toast "Annuler" (Undo) pendant 3 à 5 secondes.
* **FR4.3 :** Accès à l'historique des colis archivés.

---

### 2.2 Exigences Non-Fonctionnelles (NFR)

#### NFR1 : Privacy & Sécurité
* **NFR1.1 - Éphémérité :** Le corps brut de l'email ne doit jamais être persisté en base de données.
* **NFR1.2 - Politique de Rétention :** Les colis archivés sont supprimés définitivement de la base de données après 30 jours (Job Automatique).
* **NFR1.3 - Isolation :** Le token d'accès Gmail est stocké de manière sécurisée (Vault ou Env Var chiffrée).

#### NFR2 : Robustesse Technique (Clean Arch)
* **NFR2.1 :** Le Domaine métier doit être pur (aucune dépendance Spring, Gmail ou SQL).
* **NFR2.2 :** Les interactions externes sont isolées dans des "Adapters".

#### NFR3 : Expérience Mobile
* **NFR3.1 - Offline Read :** L'application doit mettre en cache la dernière liste connue pour consultation sans réseau.
* **NFR3.2 - Performance :** Lancement de l'application en < 2 secondes (optimisation JS bundle).

---

## 3. Objectifs de l'Interface Utilisateur (UI Goals)

### Vision UX Globale
Interface utilitaire, minimaliste et tactique. Pas de fioritures e-commerce.

### Stratégie Design System (Portfolio Consistency)
* **Fondation :** **React Native Paper** (Material Design 3).
* **Justification :** Accessibilité native, Dark Mode automatique, et excellente compatibilité avec la génération de code par IA (Vibe-Coding).
* **Theming :** Utilisation stricte de Design Tokens (Couleurs sémantiques, Typographie échelle) définis dans un fichier `theme.ts` central.

### Écrans Principaux
1.  **Dashboard (Liste) :** Composant `FlashList` (Performance). Items avec indicateurs visuels d'urgence (Code couleur Feu Tricolore basé sur le thème). Empty State rassurant.
2.  **Mode Guichet (Modal) :** Affichage géant du code. Contrôle luminosité hardware.
3.  **Historique :** Liste avec opacité réduite.

### Accessibilité
* Support natif du **Dark Mode** (hérité du système).
* Contraste AA minimum garanti par Material Design 3.

---

## 4. Hypothèses Techniques & Stack

### Architecture Globale
* **Style :** **Architecture Hexagonale** (Ports & Adapters) stricte.
* **Motivation :** Isolation totale du Domaine métier pour permettre des tests ultra-rapides et fiables.

### Backend (Le Cœur "Expert")
* **Langage :** Java 21 (LTS).
* **Framework :** Spring Boot 3.3+.
* **AI Integration :** **Spring AI** (Abstraction standard).
* **Méthodologie :** **ATDD** (Acceptance Test-Driven Development).
* **Tests :**
    * **Acceptance :** **Cucumber JVM** (Gherkin). Chaque Story a son `.feature`. Le développement est piloté par ces tests.
    * **Intégration :** **Testcontainers** (Postgres/Gmail).
    * **Architecture :** ArchUnit (Respect de l'Hexagone).

### Frontend (Le Cœur "Vibe-Coding")
* **Tech :** **React Native** via **Expo** (Managed Workflow).
* **UI Library :** React Native Paper (MD3).
* **State Management :** Zustand (Simple & Léger).

### Infrastructure & DevOps (Le Workflow "Superviseur")
* **Base de données :** PostgreSQL.
* **Hébergement :**
    * Backend : **Railway** (Support Docker & PR Environments).
    * Frontend : **Expo EAS** (Preview Channels).
* **CI/CD :** **GitHub Actions**.
* **Workflow PR (Definition of Done) :**
    1.  Tests Auto (Gherkin Vert 🟢).
    2.  Déploiement Environnement Preview (Back + Front).
    3.  Bot commente la PR avec **QR Code Expo** pour test sur mobile réel.

---

## 5. Liste des Épics (Roadmap)

### Epic 1 : La Fondation "Clean Arch" & DevOps
* **Objectif :** Mettre en place le Monorepo, le "Hello World" Backend & Frontend, et le Pipeline CI/CD complet avec Preview Apps.
* **Livrable :** Une PR technique validée par QR Code.

### Epic 2 : Le "Videur" (Ingestion & IA)
* **Objectif :** Cœur Backend. Connecter l'API Gmail et l'extraction Gemini.
* **Focus :** Adapters `GmailProvider` et `GeminiExtractor`. Tests ATDD d'extraction.

### Epic 3 : Le "Dashboard" (API & UI Mobile)
* **Objectif :** Exposer les données et afficher la liste triée sur mobile.
* **Focus :** API REST, Persistance Postgres, Intégration React Native Paper.

### Epic 4 : L'Expérience "Guichet" (Finitions)
* **Objectif :** UX avancée (Luminosité, Offline) et Cycle de vie (Archive, Job de suppression).
* **Focus :** API Natives Expo et Scheduler Spring.