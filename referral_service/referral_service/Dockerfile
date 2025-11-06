# Multi-stage build for referral_service (Java 17)
# Use Maven image because this module lacks mvnw wrapper in repo
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace

COPY pom.xml .
COPY src ./src

RUN mvn -q -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/target/referral_service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8084
ENTRYPOINT ["java","-jar","/app/app.jar"]
