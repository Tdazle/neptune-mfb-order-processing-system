# Neptune MFB Order Processing System

## Overview

The Neptune MFB Order Processing System is a microservice-based application designed to manage customer orders and interact with an inventory service for stock validation and updates. The system is built using Spring Boot, Java 17, and leverages gRPC for inter-service communication. It is intended to be scalable, modular, and easily integrable with other microservices.

## Features

- **Order Management:** Create and retrieve orders.
- **Inventory Integration:** Checks and updates product stock via gRPC calls to the Inventory Service.
- **RESTful API:** Exposes endpoints for order operations.
- **Database Support:** Uses JPA with H2 (in-memory) for persistence.
- **Error Handling:** Handles invalid orders and gRPC errors gracefully.
- **Protobuf/gRPC:** Uses Protocol Buffers for service definitions and communication.

## Architecture

- **Order Service:** Handles order creation and retrieval, communicates with Inventory Service via gRPC.
- **Inventory Service (External):** Provides stock checking and updating functionality.
- **Database:** Stores order data.
- **gRPC:** Used for communication between Order Service and Inventory Service.

### High-Level Diagram

```
graph TD
    UI[Frontend (e.g., Angular)]
    UI -->|REST| OrderService
    OrderService -->|gRPC| InventoryService
    OrderService -->|JPA| Database[(H2 DB)]
```

### Technologies Used
Java 17 <br>
Spring Boot 3.4.x <br>
Spring Data JPA <br>
gRPC (via grpc-spring-boot-starter) <br>
Protocol Buffers (via protobuf-maven-plugin) <br>
H2 Database (runtime) <br>
Maven 3.8.x <br>
(Optional) Docker, if you wish to containerize

### Setup & Build
Clone the repository:
```bash
    git clone https://github.com/Tdazle/neptune-mfb-order-processing-system.git
```
```bash
  cd neptune-mfb-order-processing-system/order-service
```

### Build the project
```bash
  docker-compose build
````

### Run the application
```bash
  docker-compose up
```

The service will start on: http://localhost:8080

### gRPC Inventory Service:
Ensure the Inventory Service is running and accessible via gRPC.

The Order Service expects a gRPC endpoint named inventory-service (see @GrpcClient("inventory-service")).

### API Endpoints to create an order:

#### Method: POST

Endpoint: ```http://localhost:8080/orders```

#### Request Body:
```
{
  "product": "Widget",
  "quantity": 5
}
```


#### Response:
``` 200 OK```  - with created order object if successful 

```400 Bad Request``` - with error message if invalid


#### Method: GET

Endpoint: ```http://localhost:8080/orders```

#### Response:
``` 200 OK``` - with list of all orders

#### Example: Order Entity
```
{
  "id": 1,
  "product": "Widget",
  "quantity": 5,
  "status": "CREATED"
}
```

Development Notes
Protobuf Files: Located in src/main/resources/proto/. Use mvn compile to generate Java classes.
gRPC Integration: Uses @GrpcClient for dependency injection of the gRPC stub.

### Database

Uses H2 database by default for development/testing. This can be changed in application.properties to use a different database of your choice.

### CORS 
Configured for http://localhost:4200 (Angular frontend).

### Project Structure

```
order-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── orderservice/
│   │   │               ├── controller/
│   │   │               │   └── OrderController.java      # Rest Controller
│   │   │               ├── entity/
│   │   │               │   └── Order.java                # Order Entity
│   │   │               ├── grpc/
│   │   │               │   └── OrderGrpcService.java     # gRPC Service
│   │   │               ├── repository/
│   │   │               │   └── OrderRepository.java      # JPA Repository
│   │   │               └── service/
│   │   │                   └── OrderService.java         # Business Logic
│   │   └── resources/
│   │       ├── application.properties                    # Config
│   │       └── proto/
│   │           ├── order.proto                           # gRPC Definition
│   │           └── inventory.proto                       # gRPC Definition
│   └── test/
│       └── java/
│           └── com/
│               └── example/
│                   └── orderservice/
│                       ├── controller/
│                       │   └── OrderControllerTest.java  # Rest Controller Test
│                       ├── entity/
│                       │   └── OrderEntityTest.java      # Order Entity Test
│                       ├── grpc/
│                       │   └── OrderGrpcServiceTest.java # gRPC Service Test
│                       └── service/
│                           └── OrderServiceTest.java     # Business Logic Test
├── Dockerfile                                            # Dockerfile
├── pom.xml                                               # Maven Build
└── README.md                                             # Readme
```

### Testing
Run unit and integration tests with:
```bash
  mvn test
```

### Extending the System
Add new endpoints in OrderController.
Extend order logic in OrderService.
Update Protobuf definitions for new gRPC methods.

### License
This project is for demo purposes.