# Multi-stage build that compiles the Spring Boot app with Maven and
# runs it from a slim Temurin JRE image.

FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace

# Copy pom first so dependency downloads get cached when only source changes
COPY Hotel-booking/pom.xml ./
RUN mvn -q -B dependency:go-offline

# Copy application sources from the nested module directory
COPY Hotel-booking/src ./src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app

# Drop privileges for the running process
RUN useradd -ms /bin/sh spring

COPY --from=build /workspace/target/*.jar app.jar
RUN chown -R spring:spring /app

EXPOSE 9090
USER spring
ENTRYPOINT ["java","-jar","app.jar"]
