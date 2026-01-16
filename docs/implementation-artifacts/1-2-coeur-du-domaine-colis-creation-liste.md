# Story 1.2 Cœur du Domaine Colis (Création & Liste)

Epic 1 - Fondations & MVP
Date 2026-01-15
Status READY FOR DEV

## 1. Description (User Story)
En tant que Utilisateur,
Je veux créer manuellement un colis et consulter la liste de mes colis suivis,
Afin de centraliser le suivi de mes commandes en un seul endroit.

## 2. Contexte & Architecture
Cette story implémente le cœur du Domaine (`Domain Layer`) et la persistance (`Infrastructure Layer`).
C'est la première implémentation de l'architecture Hexagonale stricte.

 Flux de données  API (Adapter) - Use Case (Application) - Repository Port (Domain) - Postgres (Adapter).
 Contrainte Architecturale  Le `Domain` ne doit avoir AUCUNE dépendance vers Spring Boot ou la Base de données.

## 3. Critères d'Acceptation (Gherkin - Backend Strict)

Les scénarios suivants doivent être implémentés dans `srctestresourcesfeaturesparcel_management.feature` et validés par Cucumber.

```gherkin
Feature Gestion des Colis

  Scenario Création réussie d'un colis manuel
    Given le catalogue de colis est vide
    When je crée un colis avec le numéro de suivi TRK-123456 et le label Mon Mac
    Then le colis est enregistré avec succès
    And le statut par défaut est CREATED

  Scenario Récupération de la liste des colis
    Given le catalogue contient le colis TRK-123 nommé Livres
    And le catalogue contient le colis TRK-456 nommé Clavier
    When je demande la liste de tous les colis
    Then je reçois une liste contenant 2 colis
    And la liste contient Livres et Clavier

  Scenario Refus de création si données invalides
    When je tente de créer un colis sans numéro de suivi
    Then une erreur de validation est levée Tracking ID requis
```

## 4. Tâches Techniques

### Backend (Java 21  Spring Boot  Hexagonal)
- [ ] Domain Layer (Pure Java)
    - [ ] Créer le record `ParcelId`.
    - [ ] Créer l'entité `Parcel` (avec validation métier  ID et Label non vides).
    - [ ] Définir le Port de sortie  `ParcelRepository` (Interface).
- [ ] Application Layer
    - [ ] Créer le Port d'entrée (Use Case Interface)  `CreateParcelUseCase`, `ListParcelsUseCase`.
    - [ ] Implémenter les services  `ParcelService` (implémente les Use Cases).
- [ ] Infrastructure Layer (Adapters)
    - [ ] Persistence Implémenter `PostgresParcelRepositoryAdapter` (Entity JPA - Mapper - Domain Object).
    - [ ] API Créer `ParcelController` (REST) avec DTOs (`CreateParcelRequest`, `ParcelResponse`).
    - [ ] Config Mapper les Beans du domaine dans une configuration Spring (`DomainConfig`).
- [ ] Tests
    - [ ] Implémenter les Step Definitions Cucumber pour valider les AC.
    - [ ] Ajouter un test d'architecture (ArchUnit) pour vérifier que le `Domain` ne dépend pas de `Infrastructure`.

### Frontend (React Native  Expo)
- [ ] API Client
    - [ ] Créer le service `apiparcels.ts` (fetchaxios).
- [ ] State Management (Zustand)
    - [ ] Créer le store `useParcelStore` (actions `fetchParcels`, `addParcel`).
- [ ] UI (Screens)
    - [ ] DashboardScreen  Afficher une `FlatList` des colis (Card avec Titre, ID, Statut).
    - [ ] AddParcelModal  Formulaire (TextInput) pour ajouter un colis manuellement.
- [ ] Validation Visuelle
    - [ ] Vérifier le bon affichage de la liste et le rafraîchissement après ajout.

## 5. Dev Notes (Instructions pour l'IA)

### Architecture Hexagonale (Rappel Strict)
 Interdit  Annotations JPA (`@Entity`, `@Table`) ou Spring (`@Service`) sur les classes du package `domain`.
 Requis  Utiliser des Mappers pour convertir `ParcelEntity` (JPA) - `Parcel` (Domain).
 Java 21  Utiliser les `record` pour les DTOs et les Value Objects immuables.

### Frontend
 Utiliser React Native Paper pour les composants UI (`Card`, `TextInput`, `FAB`).
 Gérer les états de chargement (`isLoading`) et d'erreur dans le store Zustand.

### Definition of Done
1.  `mvn verify` passe (Tests Unitaires + Cucumber + ArchUnit).
2.  L'application mobile affiche les données venant du Backend local.