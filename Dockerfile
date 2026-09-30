# Stage 1: Build stage using Maven and Eclipse Temurin JDK 21
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copy pom.xml and download dependencies first for caching
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build application JAR
COPY src ./src
RUN mvn package -DskipTests

# Stage 2: Runtime stage using lightweight JRE 21
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy built jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Render dynamically sets PORT; default fallback 8080
ENV PORT=8080
EXPOSE ${PORT}

# Run Spring Boot JAR
ENTRYPOINT ["sh", "-c", "java -jar app.jar"]
