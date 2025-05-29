# Use a Maven base image to build the application
FROM maven:3.8.5-openjdk-17 AS builder

# Set working directory
WORKDIR /app

# Copy pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Use a smaller base image for running the application
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=builder /app/target/order-service-0.0.1-SNAPSHOT.jar app.jar

# Expose HTTP and gRPC ports
EXPOSE 8080 9090

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]