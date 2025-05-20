# 🏀 Basketball Ticket Sales System

This project is a **multi-client application** for managing basketball ticket sales with **real-time updates**, **server-client communication** over multiple protocols, and **concurrent user support**.

## 📋 Project Overview

This system provides:
- **Multiple Client Options**: JavaFX UI, gRPC, and REST API clients
- **Real-Time Updates**: Live ticket availability across all connected clients
- **Multiple Communication Protocols**:
  - Sockets with custom RPC protocol
  - gRPC with streaming capabilities
  - REST API services
- **Comprehensive Persistence Layer**:
  - JDBC direct SQL operations
  - Hibernate ORM mappings
- **Extensive Logging**: Module-specific logging with Log4j2

## 📁 Project Structure

```
JavaApp/
│
├── Client/                          # JavaFX Client (RPC-based)
│   ├── src/main/java/app/client/gui/
│   │   ├── LoginController.java     # Handle user authentication
│   │   ├── MainController.java      # Manage ticket sales operations
│   │   ├── SceneManager.java        # Control UI navigation
│   │   └── Util.java                # UI utility functions
│   └── src/main/resources/          # JavaFX UI layouts
│
├── GrpcClient/                      # gRPC-based Client
│   ├── src/main/java/app/grpcclient/
│   ├── src/main/proto/              # Protocol Buffers definitions
│   └── src/main/resources/
│
├── JavaRestClient/                  # Java REST API Client
│   └── src/main/java/app/restclient/
│       └── MatchClient.java         # HTTP client for REST API operations
│
├── Server/                          # Server module
│   ├── src/main/java/app/server/
│   │   ├── BasketballServicesImpl.java
│   │   ├── StartRpcServer.java
│   │   └── StartHibernateRpcServer.java
│   ├── src/main/java/app/rest/      # REST API controllers
│   │   ├── MatchRestController.java # Match REST endpoints
│   │   └── StartRestServices.java   # Spring Boot app initialization
│   └── src/main/resources/
│
├── Model/                           # Shared domain models
│   └── src/main/java/app/model/
│
├── Persistence/                     # Database repositories
│   ├── src/main/java/app/repository/
│   │   ├── IMatchRepository.java    # Match repository interface
│   │   ├── ITicketRepository.java
│   │   ├── IUserRepository.java
│   │   ├── jdbc/                    # JDBC implementations
│   │   ├── hibernate/               # Hibernate ORM implementations
│   │   └── rest/                    # REST repository implementations
│
├── Services/                        # Service interfaces
│   └── src/main/java/app/services/
│
└── Networking/                      # Network protocols implementation
    └── src/main/java/app/network/
```

## ✨ Features

* 🔒 **User Authentication**: Login/logout with server-side validation
* 📋 **Real-Time Match Updates**: Live ticket availability across clients
* 🎟️ **Ticket Selling**: Sales operations with seat management
* 🔄 **Concurrent User Support**: Multiple simultaneous users
* 📊 **Comprehensive Logging**: Module-specific logging with Log4j2

## 🚀 Communication Protocols

This project implements three different communication protocols:

### 1. Custom RPC Protocol (Sockets)
* Implementation in `Networking/src/main/java/app/network/rpcprotocol/`
* Custom request/response cycle with Java serialization
* Observer pattern for real-time updates

### 2. gRPC
* Protocol definitions in `GrpcClient/src/main/proto/ticket.proto`
* Client implementation in `GrpcClient/src/main/java/app/grpcclient/`
* Server-streaming capabilities for real-time updates

### 3. REST API
* Controller in `Server/src/main/java/app/rest/MatchRestController.java`
* Standard HTTP operations (GET, POST, PUT, DELETE)
* Spring Boot-based implementation

## 📊 REST Services Operations

The REST API provides the following operations for Match entities:

| HTTP Method | Endpoint | Description |
|-------------|----------|-------------|
| GET | `/basketball/api/matches` | Get all matches |
| GET | `/basketball/api/matches/{id}` | Get match by ID |
| POST | `/basketball/api/matches` | Create a new match |
| PUT | `/basketball/api/matches/{id}` | Update a match |
| DELETE | `/basketball/api/matches/{id}` | Delete a match |

## 🛢️ Persistence Layer

The application supports two different persistence mechanisms:

1. **JDBC Repository**: Direct SQL operations through JDBC
  - Faster for simple operations
  - Lighter memory footprint
  - More control over SQL statements

2. **Hibernate ORM**: Object-Relational Mapping
  - Automatic SQL generation
  - Entity relationship management
  - Session-based transaction handling
  - Schema creation/validation

Both implementations use the same repository interfaces, allowing seamless switching between the two.

## 🚀 How to Run

### 1. Start the Server

For JDBC-based persistence:
```bash
cd Server
./gradlew run
```

For Hibernate ORM-based persistence:
```bash
cd Server
./gradlew run --args="hibernate"
```

For REST services:
```bash
cd Server
./gradlew bootRun
```

### 2. Run the Socket-based Client
```bash
cd Client
./gradlew run
```

### 3. Run the gRPC Client
```bash
cd GrpcClient
./gradlew run
```

### 4. Run the Java REST Client
```bash
cd JavaRestClient
./gradlew run
```

## 🧪 Testing the REST API

You can test the REST API using:

1. **JavaRestClient**: A command-line client for testing all REST operations
2. **Postman or similar REST client**: Import the following endpoints:
  - GET http://localhost:8080/basketball/api/matches
  - GET http://localhost:8080/basketball/api/matches/{id}
  - POST http://localhost:8080/basketball/api/matches
  - PUT http://localhost:8080/basketball/api/matches/{id}
  - DELETE http://localhost:8080/basketball/api/matches/{id}

Example JSON for creating/updating a match:
```json
{
  "teamA": "Team A",
  "teamB": "Team B",
  "ticketPrice": 75.0,
  "availableSeats": 100
}
```

## 📦 Requirements Implemented

* ✅ RESTful API with all required operations (create, update, delete, lookup by id, show all)
* ✅ Spring Boot-based REST services
* ✅ Multiple client implementations (Java, C#)
* ✅ Proper error handling and status codes
* ✅ ID generation on server side for new resources
* ✅ Dual persistence implementations (JDBC and Hibernate)
* ✅ Comprehensive logging system
* ✅ Modular architecture with clear separation of concerns

## ⚙️ Technologies Used

* Java 21
* JavaFX 21
* Spring Boot 3.2.3
* Gradle (multi-module)
* SQLite
* Hibernate ORM 6.4.4
* gRPC + Protobuf
* Apache HttpComponents
* Log4j2
* Java Concurrency APIs