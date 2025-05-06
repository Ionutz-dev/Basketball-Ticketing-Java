# 🏀 Basketball Ticket Sales System (Java + JavaFX + Networking)

This project is a **JavaFX-based multi-client application** for managing basketball ticket sales with **real-time updates**, **server-client communication** over both a **custom RPC protocol** and **gRPC**, and **concurrent user support**.

## 📁 Project Structure

```
JavaApp/
│
├── Client/                          # JavaFX Client (RPC-based)
│   ├── src/main/java/app/client/gui/
│   │   ├── LoginController.java
│   │   ├── MainController.java
│   │   ├── SceneManager.java
│   │   ├── Util.java
│   ├── src/main/java/app/client/
│   │   └── StartRpcClient.java
│   ├── src/main/resources/
│   │   ├── LoginWindow.fxml
│   │   ├── MainWindow.fxml
│
├── GrpcClient/                      # JavaFX Client (gRPC-based)
│   ├── src/main/java/app/grpcclient/gui/
│   │   ├── GrpcLoginController.java
│   │   ├── GrpcMainController.java
│   │   ├── SceneManager.java
│   │   └── Util.java
│   ├── src/main/java/app/grpcclient/
│   │   └── GrpcStartClient.java
│   ├── src/main/proto/
│   │   └── ticket.proto
│   ├── src/main/resources/
│   │   ├── GrpcLoginWindow.fxml
│   │   ├── GrpcMainWindow.fxml
│
├── Server/                          # Server module
│   ├── src/main/java/app/server/
│   │   ├── BasketballServicesImpl.java
│   │   └── StartRpcServer.java
│   ├── src/main/resources/
│   │   └── appserver.properties
│
├── Model/                           # Shared domain models
│   └── src/main/java/app/model/
│       ├── Match.java
│       ├── Ticket.java
│       └── User.java
│
├── Networking/                      # RPC Protocol and Networking
│   ├── src/main/java/app/network/dto/
│   │   ├── DTOUtils.java
│   │   ├── MatchDTO.java
│   │   ├── TicketDTO.java
│   │   └── UserDTO.java
│   ├── src/main/java/app/network/rpcprotocol/
│   │   ├── BasketballClientRpcReflectionWorker.java
│   │   ├── BasketballServicesRpcProxy.java
│   │   ├── Request.java
│   │   ├── RequestType.java
│   │   ├── Response.java
│   │   └── ResponseType.java
│   ├── src/main/java/app/network/utils/
│       ├── AbsConcurrentServer.java
│       ├── AbstractServer.java
│       ├── BasketballRpcConcurrentServer.java
│       └── ServerException.java
│
├── Persistence/                     # Database repositories (SQLite)
│   └── src/main/java/app/repository/jdbc/
│       ├── JdbcUtils.java
│       ├── MatchRepositoryJdbc.java
│       ├── TicketRepositoryJdbc.java
│       └── UserRepositoryJdbc.java
│
├── Services/                        # Service interfaces
│   └── src/main/java/app/services/
│       ├── BasketballException.java
│       ├── IBasketballObserver.java
│       └── IBasketballServices.java
│
├── logs/                             # Logs generated
│   └── app.log
├── build.gradle                      # Gradle multi-project build configuration
├── settings.gradle                   # Gradle project settings
└── README.md                         # This file
```

## ✨ Features

* 🔒 **Login / Logout** with server authentication.
* 📋 **Real-Time Match Updates**:

    * Live ticket availability across all clients.
    * Supports both socket and gRPC updates.
* 🎟️ **Ticket Selling** with customer and seat count input.
* 🚀 **Dual Communication Protocols**:

    * Sockets (custom RPC protocol)
    * gRPC (with server-streaming using `WatchMatches`)
* 🖥️ **JavaFX GUI** with separate views for socket and gRPC clients.
* 🔄 **Automatic logout** and cleanup on window close.
* 🧪 **gRPC Module (`GrpcClient`)** fully compatible with the C# gRPC server.
* 📚 **Modular Gradle Project** with clear domain separation.
* 🛢️ **SQLite JDBC persistence** with repositories.
* 🧾 **Log4j2 Logging** integrated in all repositories and services.

## 🚀 How to Run

### 1. Start the Server

```bash
cd Server
./gradlew run
```

### 2. Start the RPC Client (Socket)

```bash
cd Client
./gradlew run
```

### 3. Start the gRPC Client (JavaFX + gRPC)

```bash
cd GrpcClient
./gradlew run
```

> You can run multiple clients (RPC or gRPC) at the same time.

## 📌 Homework Requirements Implemented

* ✔️ Modular Gradle multi-project setup.
* ✔️ JavaFX GUI (for both RPC and gRPC clients).
* ✔️ Real-time updates with both socket-based and gRPC communication.
* ✔️ Streaming gRPC with `WatchMatches()`.
* ✔️ Cross-platform compatibility (gRPC works with C# server).
* ✔️ SQLite database with JDBC.
* ✔️ Logging via Log4j2.
* ✔️ Observer pattern and concurrent server handling.

## ⚙️ Technologies Used

* Java 21
* JavaFX 21
* Gradle (multi-module)
* SQLite via `sqlite-jdbc`
* gRPC + Protobuf
* TCP Sockets (custom protocol)
* Log4j2
* Java Concurrency APIs