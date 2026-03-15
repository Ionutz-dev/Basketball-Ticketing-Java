# Basketball Ticket Sales System

A multi-client application for managing basketball ticket sales with real-time updates, multiple communication protocols, and concurrent user support.

## Project Overview

This system provides:

- **Multiple Client Options**: JavaFX UI client, gRPC client, Java CLI REST client, and a React web client
- **Real-Time Updates**: Live ticket availability pushed to all connected clients via the observer pattern
- **Multiple Communication Protocols**:
  - Custom socket-based RPC protocol
  - gRPC with server-side streaming
  - HTTP REST API (Spring Boot)
- **Concurrent User Support**: Multiple simultaneous users with synchronized server-side operations
- **Persistent Storage**:
  - SQLite via JDBC (direct SQL)
  - Hibernate ORM (auto-mapped entities)
- **Extensive Logging**: Module-specific logging using Log4j2

## Project Structure

```
Basketball-Ticketing-Java/
│
├── Model/                        # Shared domain models
│   └── src/main/java/app/model/
│       ├── Match.java            # Match entity (teamA, teamB, ticketPrice, availableSeats)
│       ├── Ticket.java           # Ticket entity (matchId, userId, customerName, seatsSold)
│       └── User.java             # User entity (username, password)
│
├── Persistence/                  # Repository interfaces and implementations
│   └── src/main/java/app/repository/
│       ├── IMatchRepository.java
│       ├── ITicketRepository.java
│       ├── IUserRepository.java
│       ├── jdbc/                 # JDBC implementations
│       ├── hibernate/            # Hibernate ORM implementations
│       └── rest/                 # REST-backed repository wrapper
│
├── Services/                     # Business service interface and exceptions
│   └── src/main/java/app/services/
│       ├── IBasketballServices.java
│       ├── IBasketballObserver.java
│       └── BasketballException.java
│
├── Networking/                   # Custom socket RPC protocol and DTOs
│   └── src/main/java/app/network/
│       ├── rpcprotocol/          # Request/Response types and serialization
│       └── utils/                # Concurrent server infrastructure
│
├── Server/                       # Spring Boot REST API and RPC servers
│   ├── src/main/java/app/server/
│   │   ├── BasketballServicesImpl.java   # Core business logic
│   │   ├── StartRpcServer.java           # Socket RPC server entry point
│   │   └── StartHibernateRpcServer.java  # Hibernate-backed RPC server
│   ├── src/main/java/app/rest/
│   │   ├── MatchRestController.java      # REST CRUD endpoints
│   │   └── StartRestServices.java        # Spring Boot configuration
│   └── src/main/resources/
│       ├── application.properties        # Spring Boot config (port 8080)
│       └── appserver.properties          # RPC server config (port 55556) and DB path
│
├── Client/                       # JavaFX RPC-based GUI client
│   └── src/main/java/app/client/gui/
│       ├── LoginController.java
│       ├── MainController.java
│       ├── SceneManager.java
│       └── Util.java
│
├── GrpcClient/                   # gRPC-based client with streaming support
│   ├── src/main/java/app/grpcclient/
│   │   ├── GrpcLoginController.java
│   │   └── GrpcMainController.java
│   └── src/main/proto/
│       └── ticket.proto          # Protobuf service definitions
│
├── JavaRestClient/               # Java CLI REST client (Apache HttpClient)
│   └── src/main/java/app/restclient/
│       └── MatchClient.java      # Interactive menu-driven console client
│
└── ReactClient/                  # React web client (Vite)
    ├── package.json
    ├── vite.config.js
    └── src/
        ├── MatchApp.jsx          # Main component with state management
        ├── MatchForm.jsx         # Create/update form
        ├── MatchTable.jsx        # Data display table
        ├── index.css             # Global styles
        └── utils/
            ├── consts.js         # API base URL configuration
            └── rest-calls.js     # Fetch-based REST wrappers
```

## Key Features

- **User Authentication**: Login/logout with server-side credential validation
- **Live Match Updates**: Available seat counts pushed to all connected clients on every ticket sale
- **Ticket Selling**: Sell tickets for a match and automatically decrement available seats
- **Concurrent Access**: Synchronized server methods handling multiple simultaneous users
- **Multi-Protocol Support**: RPC (sockets), gRPC (streaming), and HTTP REST endpoints
- **Dual Persistence**: Switch between JDBC and Hibernate ORM without changing service logic
- **Structured Logging**: Log4j2 with per-module configuration, logging all model operations

## Running the System

### Prerequisites

- Java 21+
- Gradle 8.x (or use the included `gradlew` wrapper)
- Node.js 18+ and npm (for the React client)
- SQLite database file configured in `Server/src/main/resources/appserver.properties`

### 1. Build the Project

```bash
./gradlew build
```

### 2. Start the Server

**REST API** (Spring Boot on port 8080):

```bash
./gradlew :Server:bootRun
```

**Socket RPC Server** (port 55556):

```bash
./gradlew :Server:run -PmainClass=StartRpcServer
```

**Hibernate RPC Server**:

```bash
./gradlew :Server:run -PmainClass=StartHibernateRpcServer
```

### 3. Launch Clients

**JavaFX RPC Client**:

```bash
./gradlew :Client:run
```

**gRPC Client**:

```bash
./gradlew :GrpcClient:run
```

**Java CLI REST Client**:

```bash
./gradlew :JavaRestClient:run
```

**React Web Client**:

```bash
cd ReactClient
npm install
npm run dev    # available at http://localhost:3000
```

## REST API Endpoints

Base URL: `http://localhost:8080/basketball/api`

| Method | Endpoint         | Description              |
|--------|------------------|--------------------------|
| GET    | `/matches`       | List all matches         |
| GET    | `/matches/{id}`  | Get match by ID          |
| POST   | `/matches`       | Create a new match       |
| PUT    | `/matches/{id}`  | Update an existing match |
| DELETE | `/matches/{id}`  | Delete a match           |

**Request/Response JSON** (create/update):

```json
{
  "teamA": "Team A",
  "teamB": "Team B",
  "ticketPrice": 75.0,
  "availableSeats": 100
}
```

## gRPC Service

Defined in `GrpcClient/src/main/proto/ticket.proto`:

| Method         | Type            | Description                                   |
|----------------|-----------------|-----------------------------------------------|
| `Login`        | Unary           | Authenticate a user                           |
| `GetAllMatches`| Unary           | Retrieve all matches                          |
| `SellTicket`   | Unary           | Sell a ticket for a match                     |
| `WatchMatches` | Server streaming| Stream real-time match updates to the client  |

## Socket RPC Protocol

The custom RPC layer (port 55556) uses Java object serialization over TCP sockets.

Supported request types:

| Request Type           | Description                              |
|------------------------|------------------------------------------|
| `LOGIN`                | Authenticate user credentials            |
| `LOGOUT`               | End user session                         |
| `GET_AVAILABLE_MATCHES`| Fetch matches with available seats > 0   |
| `SELL_TICKET`          | Sell tickets and notify all clients      |

## Persistence Options

Both JDBC and Hibernate repositories implement the same interfaces (`IMatchRepository`, `ITicketRepository`, `IUserRepository`) and can be swapped by changing the bean wiring in `Server/src/main/java/app/rest/StartRestServices.java`.

| Option    | Implementation classes                          | Notes                                 |
|-----------|-------------------------------------------------|---------------------------------------|
| JDBC      | `MatchRepositoryJdbc`, `TicketRepositoryJdbc`   | Direct SQL with `JdbcUtils`            |
| Hibernate | `MatchRepositoryHibernate`, `TicketRepositoryHibernate` | ORM via `HibernateUtils`     |

**Database:** SQLite — path configured in `Server/src/main/resources/appserver.properties`:

```properties
jdbc.driver=org.sqlite.JDBC
jdbc.url=jdbc:sqlite:/path/to/identifier.sqlite
app.server.port=55556
```

**Tables:** `Matches`, `Tickets`, `Users`

## Technologies

| Layer        | Technology                                      |
|--------------|-------------------------------------------------|
| Language     | Java 21                                         |
| Build        | Gradle 8.12                                     |
| REST Server  | Spring Boot 3.2.3                               |
| ORM          | Hibernate 6.4.4                                 |
| Database     | SQLite 3 (via JDBC)                             |
| RPC          | Custom socket protocol (Java serialization)     |
| Streaming    | gRPC + Protocol Buffers                         |
| GUI Client   | JavaFX 21                                       |
| Web Client   | React 19 + Vite 6                               |
| Logging      | Log4j2 2.23.1                                   |
| HTTP Client  | Apache HttpClient 4.5.14 (JavaRestClient)       |
