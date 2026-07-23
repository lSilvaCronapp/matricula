# syntax=docker/dockerfile:1

FROM maven:3.9.10-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY pom.xml .
COPY src ./src

RUN mvn -B -DskipTests package

FROM eclipse-temurin:21-jre-jammy AS runtime
WORKDIR /app

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd --system spring \
    && useradd --system --gid spring spring

USER spring:spring

COPY --from=build /workspace/target/*.jar app.jar

EXPOSE 8080

HEALTHCHECK --interval=5s --timeout=5s --start-period=40s --retries=12 \
  CMD curl -fsS "http://127.0.0.1:8080/api/v1/cursos?page=0&size=1" >/dev/null || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]

