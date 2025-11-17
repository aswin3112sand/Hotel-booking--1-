# Build stage: compile the Spring Boot application
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml ./
COPY src ./src
RUN mvn -q -DskipTests package

# Runtime stage: lightweight JRE image
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/hotel-booking-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 9090
ENTRYPOINT ["java","-jar","/app/app.jar"]
