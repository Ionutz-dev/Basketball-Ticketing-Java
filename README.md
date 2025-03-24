# 🏀 Basketball Ticket Sales System (Java + JavaFX)

This project is a **JavaFX-based desktop application** for managing basketball ticket sales. It includes GUI interaction, ticket purchase logic, seat management, and persistent storage using SQLite.

## 📁 Project Structure
```
JavaApp/
│
├── build.gradle                     # Project build configuration
├── gradlew / gradlew.bat            # Gradle wrapper scripts
├── settings.gradle
├── identifier.sqlite                # SQLite database file
├── README.md                        # Project documentation
│
├── src/
│   └── main/
│       ├── java/
│       │   └── tickets/
│       │       ├── controller/
│       │       │   └── MainWindowController.java
│       │       ├── model/
│       │       │   ├── Match.java
│       │       │   ├── Ticket.java
│       │       │   └── User.java
│       │       ├── repository/
│       │       │   ├── IMatchRepository.java
│       │       │   ├── ITicketRepository.java
│       │       │   ├── IUserRepository.java
│       │       │   ├── MatchRepository.java
│       │       │   └── TicketRepository.java
│       │       ├── service/
│       │       │   └── TicketService.java
│       │       ├── utils/
│       │       │   └── DBUtils.java
│       │       ├── MainApp.java                   # JavaFX Application Entry Point
│       │       └── module-info.java               # Java Module Declaration
│       └── resources/
│           ├── db.properties
│           ├── log4j2.xml
│           └── MainWindow.fxml                    # JavaFX UI Layout File
│
└── target/
    └── app.log
```

## ✨ Features
- JavaFX **Graphical User Interface** for real-time interaction.
- **Ticket Purchase Workflow**: Select match → Enter customer → Buy tickets.
- **Seat Availability Tracking** and updates per match.
- **Persistent SQLite Storage** with pre-configured database and connection.
- **Log4j2 Logging** for repository/database actions.
- **Modular Java (module-info.java)** with Gradle JavaFX setup.

## 🚀 How to Run the App
1. **Clone the Repository**
   ```bash
   git clone <repo-url>
   cd JavaApp
   ```

2. **Open in IntelliJ IDEA**  
   Ensure Gradle is set to build the project (File > Settings > Build Tools > Gradle > Use Gradle).

3. **Build Project**
   ```bash
   ./gradlew clean build
   ```

4. **Run the JavaFX App**
   ```bash
   ./gradlew run
   ```

   This opens the **Basketball Ticket Sales** UI where you can buy tickets and view live seat updates.

## 📌 Homework Requirements Implemented
- ✔️ **Model Classes**: `User`, `Match`, and `Ticket` defined with proper fields and constructors.
- ✔️ **Repository Interfaces**: Structured contracts for user, match, and ticket database access.
- ✔️ **Repository Implementations**: Concrete SQL-based logic using SQLite and `DBUtils`.
- ✔️ **Log4j2 Logging**: Enabled in all repositories and service layers for debug and tracking.
- ✔️ **SQLite Configuration**: Externalized via `db.properties` for secure, flexible connection.
- ✔️ **JavaFX GUI**: Developed FXML-based GUI with controller interaction and service delegation.
- ✔️ **Service Layer**: Centralized business logic in `TicketService`, invoked by GUI controller.
- ✔️ **No Auto-Refresh**: Per requirements, no automatic data refresh is implemented.

## ⚙️ Dependencies & Technologies
- Java 21
- JavaFX 21 (`javafx.controls`, `javafx.fxml`)
- SQLite (`sqlite-jdbc`)
- Log4j2
- Gradle (with JavaFX and JLink plugin)
- Modular Java (`module-info.java`)
