# 🏀 Basketball Ticket Sales System

This project is a **multi-client application** for managing basketball ticket sales with **real-time updates**, **server-client communication** over multiple protocols, and **concurrent user support**.

## 📋 Project Overview

This system provides:

* **Multiple Client Options**: JavaFX UI client, gRPC client, REST API client, and a React web client
* **Real-Time Updates**: Live ticket availability across all connected clients
* **Multiple Communication Protocols**:

  * Sockets with custom RPC protocol
  * gRPC with streaming capabilities
  * REST API services (Spring Boot)
* **Concurrent User Support**: Multiple simultaneous users interacting with the system
* **Persistent Storage**:

  * SQLite via JDBC
  * Hibernate ORM
* **Extensive Logging**: Module-specific logging using Log4j2

## 📁 Project Structure

```
JavaApp/                          ← Parent multi-module project
│
├── Client/                       # JavaFX RPC-based Client
│   ├── src/main/java/app/client/gui/
│   │   ├── LoginController.java
│   │   ├── MainController.java
│   │   ├── SceneManager.java
│   │   └── Util.java
│   └── src/main/resources/       # FXML layouts
│
├── GrpcClient/                   # gRPC-based Client
│   ├── src/main/java/app/grpcclient/
│   ├── src/main/proto/           # .proto definitions
│   └── src/main/resources/
│
├── JavaRestClient/               # Java CLI REST Client
│   └── src/main/java/app/restclient/
│       └── MatchClient.java
│
├── ReactClient/                  # React web client (Vite)
│   ├── package.json
│   ├── vite.config.js            # configured to use port 3000
│   └── src/
│       ├── MatchApp.jsx          # Main React component
│       ├── MatchForm.jsx         # Create/update form
│       ├── MatchTable.jsx        # Data table
│       ├── index.css             # Global styles
│       └── utils/                # REST helper functions
│           ├── consts.js         # API base URL
│           └── rest-calls.js     # fetch wrappers
│
├── Server/                       # Spring Boot REST & RPC servers
│   ├── src/main/java/app/server/
│   │   ├── BasketballServicesImpl.java
│   │   ├── StartRpcServer.java
│   │   └── StartHibernateRpcServer.java
│   ├── src/main/java/app/rest/   # REST controllers & config
│   │   ├── MatchRestController.java
│   │   └── StartRestServices.java
│   └── src/main/resources/
│
├── Model/                        # Shared domain models
│   └── src/main/java/app/model/
│
├── Persistence/                  # Repository implementations
│   ├── src/main/java/app/repository/
│   │   ├── IMatchRepository.java
│   │   ├── ITicketRepository.java
│   │   ├── IUserRepository.java
│   │   ├── jdbc/                 # JDBC implementations
│   │   ├── hibernate/            # Hibernate implementations
│   │   └── rest/                 # REST repository wrapper
│
├── Services/                     # Business service interfaces
│   └── src/main/java/app/services/
│
└── Networking/                   # Custom socket RPC protocol
    └── src/main/java/app/network/
```

## ✨ Key Features

* 🔒 **User Authentication**: Login/logout with server-side validation
* 📋 **Live Match & Ticket Updates**: Seats availability updates push to all clients
* 🎟️ **Ticket Selling**: Sell tickets, update available seats
* 🔄 **Concurrent Access**: Handle multiple users concurrently
* 🌐 **Multi-Protocol Support**: RPC, gRPC, and REST endpoints
* 📊 **Logging**: Detailed logs via Log4j2

## 🚀 Running the System

### 1. Build & Start the Server

```bash
cd JavaApp/Server
mvn clean install
mvn spring-boot:run        # starts REST API on port 8080
# OR run RPC servers:
# mvn exec:java -Dexec.mainClass="app.server.StartRpcServer"
# mvn exec:java -Dexec.mainClass="app.server.StartHibernateRpcServer"
```

### 2. Launch Clients

* **JavaFX RPC Client**:

  ```bash
  cd JavaApp/Client
  mvn clean javafx:run
  ```
* **gRPC Client**:

  ```bash
  cd JavaApp/GrpcClient
  mvn clean exec:java -Dexec.mainClass="app.grpcclient.YourGrpcMain"
  ```
* **Java REST Client**:

  ```bash
  cd JavaApp/JavaRestClient
  mvn clean exec:java -Dexec.mainClass="app.restclient.MatchClient"
  ```
* **React Web Client**:

  ```bash
  cd JavaApp/ReactClient
  npm install
  npm run dev        # launches at http://localhost:3000
  ```

## 📑 REST API Endpoints

| Method | Endpoint                       | Description              |
| ------ | ------------------------------ | ------------------------ |
| GET    | `/basketball/api/matches`      | List all matches         |
| GET    | `/basketball/api/matches/{id}` | Get match by ID          |
| POST   | `/basketball/api/matches`      | Create a new match       |
| PUT    | `/basketball/api/matches/{id}` | Update an existing match |
| DELETE | `/basketball/api/matches/{id}` | Delete a match           |

**JSON Body** (create/update):

```json
{
  "teamA": "Team A",
  "teamB": "Team B",
  "ticketPrice": 75.0,
  "availableSeats": 100
}
```

## 🛢️ Persistence Options

* **SQLite via JDBC**: Direct SQL with `Matches` table
* **Hibernate ORM**: Entities auto-mapped, schema management

Switch implementations by configuring beans in `Server/StartRestServices.java` (or using profiles).

## 🛠️ UI Enhancements (React Client)

* **Modern card layout** with light theme & shadows
* **Responsive form grid** for inputs
* **Styled buttons** for primary (blue), secondary (gray), info (teal), and danger (red)
* **Striped, hoverable table** with colored header

## 📦 Technologies

* Java 21, JavaFX 21
* Spring Boot 3
* SQLite (JDBC), Hibernate 6
* gRPC + Protobuf
* React 18 + Vite
* Log4j2
* Maven & npm

## ✅ Checklist for the Assignment

* [x] RESTful API with full CRUD
* [x] Spring Boot REST services with CORS
* [x] React web client with Vite
* [x] UI styling and layout improvements
* [x] JDBC & Hibernate persistence
* [x] Multiple client implementations secured and logged
