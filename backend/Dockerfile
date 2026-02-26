# Stage 1: Build
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
# Utilisation du cache pour les dépendances Maven
RUN --mount=type=cache,target=/root/.m2 mvn dependency:go-offline
COPY src ./src
# Utilisation du cache pour le packaging
RUN --mount=type=cache,target=/root/.m2 mvn package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:21-jre-alpine
# Ajout de curl pour le healthcheck
RUN apk add --no-cache curl
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
# Définition du healthcheck
HEALTHCHECK --interval=30s --timeout=10s --retries=3 \
  CMD curl -f http://localhost:8080/api/health || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
