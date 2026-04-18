# 1. Build-Phase: Wir nutzen Maven, um den Code zu kompilieren
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Baut die .jar Datei und überspringt Tests (damit der Build schneller geht)
RUN mvn clean package -DskipTests

# 2. Run-Phase: Wir nehmen nur ein nacktes Java (ohne Maven) für den Live-Betrieb
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
# Kopiert die fertige .jar aus der Build-Phase
COPY --from=build /app/target/*.jar app.jar

# Spring Boot läuft standardmäßig auf Port 8080
EXPOSE 8080

# Startbefehl
ENTRYPOINT ["java", "-jar", "app.jar"]