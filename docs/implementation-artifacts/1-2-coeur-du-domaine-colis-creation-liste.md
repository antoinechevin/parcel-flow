# Story 1.2 Cœur du Domaine Colis (Création & Liste)

Epic 1 - Fondations & MVP
Date 2026-01-15
Status DONE

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
- [x] Domain Layer (Pure Java)
    - [x] Créer le record `ParcelId`.
    - [x] Créer l'entité `Parcel` (avec validation métier  ID et Label non vides).
    - [x] Définir le Port de sortie  `ParcelRepository` (Interface).
- [x] Application Layer
    - [x] Créer le Port d'entrée (Use Case Interface)  `CreateParcelUseCase`, `ListParcelsUseCase`.
    - [x] Implémenter les services  `ParcelService` (implémente les Use Cases).
- [x] Infrastructure Layer (Adapters)
    - [x] Persistence Implémenter `PostgresParcelRepositoryAdapter` (Entity JPA - Mapper - Domain Object).
    - [x] API Créer `ParcelController` (REST) avec DTOs (`CreateParcelRequest`, `ParcelResponse`).
    - [x] Config Mapper les Beans du domaine dans une configuration Spring (`DomainConfig`).
- [x] Tests
    - [x] Implémenter les Step Definitions Cucumber pour valider les AC.
    - [x] Ajouter un test d'architecture (ArchUnit) pour vérifier que le `Domain` ne dépend pas de `Infrastructure`.

### Frontend (React Native  Expo)
- [x] API Client
    - [x] Créer le service `apiparcels.ts` (fetchaxios).
- [x] State Management (Zustand)
    - [x] Créer le store `useParcelStore` (actions `fetchParcels`, `addParcel`).
- [x] UI (Screens)
    - [x] DashboardScreen  Afficher une `FlatList` des colis (Card avec Titre, ID, Statut).
    - [x] AddParcelModal  Formulaire (TextInput) pour ajouter un colis manuellement.
- [x] Validation Visuelle
    - [x] Vérifier le bon affichage de la liste et le rafraîchissement après ajout.

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

## 6. Dev Agent Record

### File List
- `backend/domain/src/main/java/com/parcelflow/domain/model/ParcelId.java`
- `backend/domain/src/main/java/com/parcelflow/domain/model/ParcelStatus.java`
- `backend/domain/src/main/java/com/parcelflow/domain/model/Parcel.java`
- `backend/domain/src/main/java/com/parcelflow/domain/port/ParcelRepository.java`
- `backend/domain/src/test/java/com/parcelflow/domain/model/ParcelTest.java`
- `backend/application/pom.xml`
- `backend/application/src/main/java/com/parcelflow/application/usecase/CreateParcelUseCase.java`
- `backend/application/src/main/java/com/parcelflow/application/usecase/ListParcelsUseCase.java`
- `backend/application/src/main/java/com/parcelflow/application/service/ParcelService.java`
- `backend/application/src/test/java/com/parcelflow/application/service/ParcelServiceTest.java`
- `backend/infrastructure/pom.xml`
- `backend/infrastructure/src/main/java/com/parcelflow/infrastructure/adapter/persistence/ParcelEntity.java`
- `backend/infrastructure/src/main/java/com/parcelflow/infrastructure/adapter/persistence/JpaParcelRepository.java`
- `backend/infrastructure/src/main/java/com/parcelflow/infrastructure/adapter/persistence/ParcelMapper.java`
- `backend/infrastructure/src/main/java/com/parcelflow/infrastructure/adapter/persistence/PostgresParcelRepositoryAdapter.java`
- `backend/infrastructure/src/main/java/com/parcelflow/infrastructure/config/DomainConfig.java`
- `backend/infrastructure/src/main/java/com/parcelflow/infrastructure/adapter/web/ParcelController.java`
- `backend/infrastructure/src/main/java/com/parcelflow/infrastructure/adapter/web/CreateParcelRequest.java`
- `backend/infrastructure/src/main/java/com/parcelflow/infrastructure/adapter/web/ParcelResponse.java`
- `backend/infrastructure/src/test/resources/features/parcel_management.feature`
- `backend/infrastructure/src/test/java/com/parcelflow/infrastructure/ParcelStepDefinitions.java`
- `backend/infrastructure/src/test/java/com/parcelflow/infrastructure/ArchitectureTest.java`
- `frontend/src/api/parcels.ts`
- `frontend/src/store/useParcelStore.ts`
- `frontend/src/components/DashboardScreen.tsx`
- `frontend/App.tsx`

### Change Log
- Implemented core Domain model: Parcel, ParcelId, ParcelStatus
- Added domain validations (null checks, empty checks)
- Defined ParcelRepository output port
- Added unit tests for Parcel creation and validation
- Implemented Application Layer: CreateParcelUseCase, ListParcelsUseCase, ParcelService
- Added unit tests for ParcelService using Mockito
- Implemented Infrastructure Layer: ParcelEntity, JpaParcelRepository, PostgresParcelRepositoryAdapter
- Implemented ParcelMapper to map between Entity and Domain
- Configured DomainConfig for ParcelService injection
- Implemented ParcelController with DTOs
- Added H2 dependency for testing
- Implemented Cucumber tests for end-to-end scenario validation
- Implemented Architecture Tests with ArchUnit
- Implemented Frontend API client (Axios)
- Implemented Frontend State Management (Zustand)
- Implemented DashboardScreen with List and Add Modal (React Native Paper)