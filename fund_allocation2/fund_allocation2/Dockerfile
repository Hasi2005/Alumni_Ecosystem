# Multi-stage build for fund_allocation2 (Java 17)
FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace

COPY mvnw mvnw
COPY .mvn .mvn
COPY pom.xml pom.xml
COPY src src

RUN chmod +x mvnw && ./mvnw -q -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/target/fund_allocation2-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=5s --retries=5 CMD bash -c "</dev/tcp/127.0.0.1/8080" || exit 1
ENTRYPOINT ["java","-jar","/app/app.jar"]

