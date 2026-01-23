# 📦 Parcel-Flow

**Parcel-Flow** est une application mobile conçue pour simplifier le retrait de colis (Vinted, Mondial Relay, etc.) en extrayant intelligemment les informations des emails de livraison via l'IA.

Ce projet sert de **Dojo Technique** pour démontrer une mise en œuvre rigoureuse de l'**Architecture Hexagonale** et du développement piloté par les tests (**ATDD**).

---

## 🚀 État du Projet

- **Statut :** En cours de développement (Epic 1 : Foundation & Ingestion)
- **Dernière Story Terminée :** 1.3 - Adapter Gmail (Client d'Infrastructure)
- **Couverture de tests :** Tests d'acceptation Gherkin (Cucumber) + Tests unitaires.

---

## 🛠 Stack Technique

### Backend
- **Langage :** Java 21 (LTS)
- **Framework :** Spring Boot 3.3
- **IA :** Spring AI + Gemini 2.0 Flash (extraction de métadonnées)
- **API Externes :** Google Gmail API (OAuth2)
- **Base de données :** PostgreSQL 16
- **Architecture :** Hexagonale Stricte (Ports & Adapters)
- **Qualité :** ArchUnit (contrôle de la pureté du domaine)

### Frontend
- **Framework :** React Native (Expo SDK 52)
- **Langage :** TypeScript 5+
- **UI :** React Native Paper 5.x (Material Design 3)
- **Gestion d'état :** Zustand

---

## 🏗 Architecture

Le projet suit les principes de l'**Architecture Hexagonale** pour garantir un domaine métier pur et indépendant des frameworks :

```text
parcelflow/
├── backend/
│   ├── src/main/java/com/parcelflow/
│   │   ├── domain/          # 🛡️ Cœur Pur (Sans Spring, sans réflexion)
│   │   │   ├── model/       # Entités & Value Objects
│   │   │   └── ports/       # Interfaces (In/Out)
│   │   ├── application/     # ⚙️ Orchestration (Use Cases)
│   │   └── infrastructure/  # 🔌 Adaptateurs (Spring, DB, AI, Mail)
```

---

## 🚦 Démarrage Rapide

### Prérequis
- Java 21+
- Node.js 20+
- Docker & Docker Compose

### Installation & Tests

1. **Cloner le dépôt**
2. **Backend :**
   ```bash
   cd backend
   mvn clean install
   mvn test  # Lance les tests unitaires et Cucumber
   ```
3. **Frontend :**
   ```bash
   cd frontend
   npm install
   npx expo start
   ```
4. **Infrastructure (Postgres) :**
   ```bash
   docker-compose up -d
   ```

---

## 📝 Licence
Ce projet est sous licence MIT. Voir le fichier [LICENSE](LICENSE) pour plus de détails.