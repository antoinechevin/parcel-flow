# Project Brief: Parcel-Flow (Portfolio Showcase)

## 1. Executive Summary
Application mobile personnelle d'optimisation logistique pour la récupération de colis.
**Double Objectif :**
1.  **Utilitaire :** Résoudre le problème de dispersion et d'oubli des retraits de colis (MVP centré sur les points relais, hors domicile).
2.  **Portfolio :** Démontrer l'expertise d'un **Senior Java Backend Dev / PO** capable d'allier rigueur architecturale (Spring Boot, Hexagonal) et productivité moderne (Vibe-Coding, Spring AI).

## 2. Problem Statement
* **Logistique :** Risque de retour expéditeur par oubli de la date limite, friction au guichet pour trouver le code.
* **Technique :** Les intégrations IA sont souvent "bricolées". Le défi est d'intégrer l'indéterminisme de l'IA (Gemini) dans une architecture backend robuste et testée.

## 3. Target Users
* **Utilisateur Final :** Le créateur (usage personnel, privacy-first).
* **Audience Portfolio :** CTOs, Lead Devs et Recruteurs cherchant un profil hybride "Tech Lead + Product Aware".

## 4. Proposed Solution & Core Features (MVP)

### A. L'Agrégateur Intelligent "Le Videur" (Spring AI + Gemini 3)
* **Input :** Emails transférés via règle Gmail (`subject:(colis OR...)`).
* **Traitement (Backend Java) :**
    * Orchestration via **Spring AI**.
    * Prompt Engineering strict : Rejet silencieux des livraisons domicile et pubs. Extraction structurée (JSON) pour les retraits points relais.
* **Données :** Transporteur, Lieu, Code/QR, Date limite, **Tracking ID**.

### B. Logique Métier Robuste (Dédoublonnage)
* Gestion stricte de l'unicité via le Tracking ID (Upsert).
* Mise à jour intelligente : Un email de rappel ne crée pas un doublon mais met à jour l'urgence du colis existant.

### C. Expérience Mobile "Zéro Friction"
* **Tri par Urgence :** Les colis proches de l'expiration en priorité rouge.
* **Mode Guichet :** Affichage gros caractères du code.
* **Fallback :** Bouton "Ouvrir l'email" si l'IA ne peut pas extraire le code (PDF/Image complexe).
* **Action :** "Swipe-to-Archive" pour valider le retrait.

## 5. Technical Constraints & Portfolio Stack

### Backend (La Démonstration d'Expertise)
* **Langage :** Java 21+.
* **Framework :** **Spring Boot 3.x**.
* **AI Integration :** **Spring AI** (Montrer l'usage de l'abstraction standard Java pour l'IA).
* **Architecture :** **Hexagonale (Ports & Adapters)**.
    * *Domaine :* Pur Java, contient la logique de tri, d'expiration et de dédoublonnage. Isolé du framework.
    * *Infra (Adapters) :* Gmail Ingest, Gemini Client, Persistence (Postgres/H2).
* **Qualité :** TDD (Test Driven Development) sur le domaine. Tests d'intégration sur les Adapters IA.

### Frontend (La Démonstration de Vibe-Coding)
* **Approche :** Génération assistée par IA (Cursor/v0) pilotée par des User Stories précises (compétence PO).
* **Tech :** React Native (Expo) pour un déploiement facile sur mobile perso.

### Infrastructure
* **IA :** Google Gemini 3 Flash (Free Tier).
* **Hébergement Backend :** Containerisé (Docker), déployable sur un cloud provider gratuit/pas cher (ex: Railway, Render ou un VPS personnel).

## 6. Success Metrics
* **Tech :** Couverture de tests du domaine > 80%. Architecture découplée (on peut changer de modèle d'IA sans casser le métier).
* **Produit :** Zéro doublon dans la liste, zéro colis expiré oublié.