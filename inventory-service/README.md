# Inventory Service

This is the **Inventory Service** for the Neptune MFB Order Processing System. It is a Spring Boot microservice responsible for managing product inventory, exposing both REST and gRPC APIs for integration with other services.

---

## Features

- Manage products and their stock quantities
- Retrieve all products via REST API
- Check and update product stock via gRPC
- JPA-based persistence (H2 in-memory database by default)
- Easily extensible for integration with other microservices

---

## Tech Stack

- Java 17
- Spring Boot 3.x
- Spring Data JPA
- H2 Database (for development/testing)
- gRPC (via grpc-spring-boot-starter)
- Maven

---

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.6+
- (Optional) Docker, if you wish to containerize

### Clone the Repository

```bash
  git clone https://github.com/your-org/neptune-mfb-order-processing-system.git
```

```bash
  cd neptune-mfb-order-processing-system/inventory-service
```

### Build the project
```bash
  mvn clean install
````

### Run the application
```bash
  mvn spring-boot:run
```

The service will start on: http://localhost:8081

### API Endpoints:

#### Method: GET

Endpoint: ```http://localhost:8081/inventory/products```

Response: JSON array of all products
#### Example:
```
[
  {
    "id": 1,
    "name": "Product A",
    "stockQuantity": 100
  }
]
```

### gRPC Endpoint:

gRPC API Proto files are located in ```src/main/resources/proto```.
Service exposes methods for checking and updating product stock
See proto definitions and generated code for details. 

gRPC configuration is managed via application.properties and Maven plugins

### Database
Uses an H2 database by default (for development).
To use a different database, update the application.properties file.

### CORS
Configured for http://localhost:4200 (Angular frontend).

### Project Structure

```
inventory-service/
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── example
│   │   │           └── inventoryservice
│   │   │               ├── controller
│   │   │               │    └── InventoryController.java    # REST Controller
│   │   │               ├── entity
│   │   │               │    └── Product.java                # Entity
│   │   │               ├── grpc
│   │   │               │    └── InventoryGrpcService.java   # gRPC Service
│   │   │               ├── repository
│   │   │               │    └── ProductRepository.java      # Repository
│   │   │               └── service
│   │   │                   └── InventoryService.java        # Business logic
│   │   └── resources
│   │       ├──application.properties                        # Configuration
│   │       └── proto
│   │           └── product.proto                            # gRPC API definitions
│   └── test
│       └── java
│          └── com
│              └── example
│                  └── inventoryservice
│                      ├── controller
│                      │    └── InventoryControllerTest.java # Controller tests
│                      ├── entity
│                      │    └── ProductTest.java             # Entity tests
│                      ├── service
│                      │    └── InventoryServiceTest.java    # Business logic tests
│                      └── grpc
│                          └── InventoryGrpcServiceTest.java # gRPC Service Test
├── Dockerfile                                               # Dockerfile
├── pom.xml                                                  # Maven Build
└── README.md                                                # Readme
```

### Testing
Run unit and integration tests with:
```bash
  mvn test
```

### License
This project is for demo purposes.