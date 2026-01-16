# Parcel-Flow Monorepo

## Structure
- `backend/`: Spring Boot 3.4.1 (Java 21) with Hexagonal Architecture.
- `frontend/`: React Native (Expo).

## Prerequisites
- Java 21
- Maven 3.9+
- Node.js 20+
- Docker & Docker Compose

## Getting Started

### Backend
```bash
cd backend
mvn clean install
mvn spring-boot:run -pl infrastructure
```

### Frontend
```bash
cd frontend
npm install
npx expo start
```

### Infrastructure
```bash
docker-compose up -d
```
