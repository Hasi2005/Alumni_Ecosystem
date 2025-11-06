# Multi-stage build for auth-service (Java 24)
# Build stage
FROM eclipse-temurin:24-jdk AS build
WORKDIR /workspace

# Leverage Maven Wrapper included in the project
COPY mvnw mvnw
COPY .mvn .mvn
COPY pom.xml pom.xml
COPY src src

# Build the application (skip tests for faster container build)
RUN chmod +x mvnw && ./mvnw -q -DskipTests package

# Runtime stage
FROM eclipse-temurin:24-jre
WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /workspace/target/auth-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java","-jar","/app/app.jar"]
