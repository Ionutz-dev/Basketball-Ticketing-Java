# 🏀 Basketball Ticket Sales System (Java + JavaFX + Networking)

This project is a **JavaFX-based multi-client application** for managing basketball ticket sales with **real-time updates**, **server-client communication** over a **custom RPC protocol**, and **concurrent user support**.

## 📁 Project Structure
```
JavaApp/
│
├── Client/                          # Client module (JavaFX UI)
│   ├── src/main/java/app/client/gui/
│   │   ├── LoginController.java
│   │   ├── MainController.java
│   │   ├── SceneManager.java
│   │   ├── Util.java
│   ├── src/main/java/app/client/
│   │   └── StartRpcClient.java       # Client application entry point
│   ├── src/main/resources/
│   │   ├── LoginWindow.fxml
│   │   ├── MainWindow.fxml
│
├── Server/                          # Server module
│   ├── src/main/java/app/server/
│   │   ├── BasketballServicesImpl.java
│   │   └── StartRpcServer.java       # Server application entry point
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
- 🔒 **Login / Logout** functionality with server authentication.
- 📋 **Real-Time Match Updates**: All connected clients see instant seat updates when a ticket is sold.
- 🎟️ **Ticket Selling**: Select a match, input customer name and number of seats to buy.
- 🚀 **Socket-based RPC Communication** between clients and server.
- 🧹 **Automatic Logout** on window close.
- 🖥️ **JavaFX GUI** for login, match list, and selling tickets.
- 📚 **Multi-Module Gradle Project** (Client, Server, Model, Networking, Persistence, Services).
- 🛢️ **SQLite Database** persistence using custom JDBC repositories.
- 📜 **Log4j2 Logging** integrated for important server actions.

## 🚀 How to Run

### 1. Start the Server
```bash
cd Server
./gradlew run
```
This will launch the **Ticket Server** on the configured port (e.g., 55556).

### 2. Start the Client
In a new terminal:
```bash
cd Client
./gradlew run
```
The **Login Window** will open.

You can open multiple clients by launching the client multiple times!

## 📌 Homework Requirements Implemented
- ✔️ Modular structure using Gradle multiproject.
- ✔️ JavaFX GUI (Login + MainWindow).
- ✔️ Real-time client updates using Observer pattern via server push.
- ✔️ Proper logout handling (manual or window close).
- ✔️ Network communication via custom-designed RPC protocol.
- ✔️ Server concurrency with thread pool handling.
- ✔️ JDBC repositories for match/ticket/user database access.
- ✔️ Logging via Log4j2 in repositories and services.
- ✔️ Clean Java 21 code using JavaFX 21.

## ⚙️ Technologies Used
- Java 21
- JavaFX 21 (`javafx.controls`, `javafx.fxml`)
- Gradle (multi-project setup)
- SQLite (using `sqlite-jdbc`)
- Sockets (TCP/IP communication)
- Log4j2 (logging)
- Observer design pattern
- Modular Java (`module-info.java`)

Would you also like me to quickly generate a **graphical diagram** for your architecture (client-server flow)? 📈
It would make your README even cooler if you present this to someone! 🚀