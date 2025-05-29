# Neptune MFB Order Processing System

This repository contains the **Neptune MFB Order Processing System**, a microservices-based platform for managing orders and inventory. The system is designed for modularity, scalability, and ease of integration, using modern Java, Spring Boot, Angular, and gRPC technologies.

---

## Project Structure

```
├── order-frontend/      # Angular frontend application
├── order-service/       # Spring Boot microservice for order management
├── inventory-service/   # Spring Boot microservice for inventory management
├── docker-compose.yml   # Docker Compose configuration
├── init-db.sh           # Script to initialize the database
└── README.md            # This file
```


---

## Overview

The Neptune MFB Order Processing System consists of:

- **Order Frontend:** An Angular-based UI for managing orders and products.
- **Order Service:** A Spring Boot microservice responsible for order creation, retrieval, and integration with inventory.
- **Inventory Service:** A Spring Boot microservice managing product inventory, exposing REST and gRPC APIs.

The services communicate primarily via gRPC for efficient, type-safe inter-service calls.

---

## Architecture

- **Frontend:** Angular 20, communicates with Order Service via REST.
- **Order Service:** Java 17, Spring Boot 3.x, exposes REST API, communicates with Inventory Service via gRPC, persists data with H2 (in-memory) using JPA.
- **Inventory Service:** Java 17, Spring Boot 3.x, exposes REST and gRPC APIs, manages inventory with H2 (in-memory) using JPA.

---

## Technologies Used

- Java 17
- Spring Boot 3.x
- Spring Data JPA
- PostgreSQL Database (for development/testing)
- gRPC (via grpc-spring-boot-starter)
- Protocol Buffers
- Angular 20
- Maven 3.8.x
- (Optional) Docker

---

## Getting Started

### Prerequisites

- Java 17+
- Node.js 18+ and npm (for frontend)
- Maven 3.8.x+
- (Optional) Docker

### Running the Services

1. **Inventory Service**

   ```bash
   cd inventory-service
   mvn spring-boot:run
   ```

2. **Order Service**

   ```bash
   cd order-service
   mvn spring-boot:run
   ```

3. **Frontend**

   ```bash
   cd order-frontend
   npm install
   ng serve
   ```
   The frontend will be available at ```http://localhost:4200```.
---

## Deployment

1. **Build Docker Images**

   ```bash
   docker-compose build
   ```

2. **Run Docker Compose**

   ```bash
   docker-compose up
   ```

---


### Development Notes

Please refer to the **README.md** files in the individual service directories for more detailed development notes.

### License
This project is for demo purposes.