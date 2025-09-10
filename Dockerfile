# Use an official OpenJDK 17 image as base
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml first for dependency caching
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Make sure wrapper is executable
RUN chmod +x mvnw

# Download dependencies (this layer will be cached unless pom.xml changes)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Package the application (skip tests to speed up build)
RUN ./mvnw clean package -DskipTests

# Use a slim runtime for running the JAR
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy only the built jar from previous stage
COPY --from=0 /app/target/*.jar app.jar

# Expose port (default Spring Boot is 8080, but Render usually sets PORT env)
EXPOSE 8080

# Run with JVM options if needed (enable SSL, trust certs for Aiven)
ENTRYPOINT ["java","-jar","app.jar"]
