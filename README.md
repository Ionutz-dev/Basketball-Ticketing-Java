# Basketball Ticket Sales System (Java)
This project is a Java-based system for managing basketball ticket sales. 

## Project Structure
```
JavaApp/
│
├── build.gradle
├── gradlew / gradlew.bat
├── settings.gradle
├── identifier.sqlite                    # SQLite database file
├── README.md                            # Project documentation
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── model/
│   │   │   │   ├── Match.java
│   │   │   │   ├── Ticket.java
│   │   │   │   └── User.java
│   │   │   ├── repository/
│   │   │   │   ├── IMatchRepository.java
│   │   │   │   ├── ITicketRepository.java
│   │   │   │   ├── IUserRepository.java
│   │   │   │   └── MatchRepository.java
│   │   │   ├── utils/
│   │   │   │   └── DBUtils.java
│   │   │   └── Main.java
│   │   └── resources/
│   │       ├── db.properties
│   │       └── log4j2.xml
│   └── test/
│       ├── java/                        # Test classes (if any)
│       └── resources/                   # Test resources (if any)
│
└── target/                              # Build output
    └── app.log                          # Log file (generated)
```

## Features
- User authentication (Login/Logout)
- Ticket sales tracking
- Available seat search and management
- Logging of repository actions (Log4j2)
- Database connection via configuration file (SQLite)

## How to Run
1. Clone the repository.
2. Open in IntelliJ IDEA.
3. Build the project using Gradle:
   ```bash
   ./gradlew build
   ```
4. Run the application:
   ```bash
   ./gradlew run
   ```

## Homework Requirements Implemented
- Designed and implemented the **model classes**: `User`, `Match`, and `Ticket`, each representing core entities of the ticket sales system with appropriate attributes and constructors.
- Defined **repository interfaces** for user, match, and ticket data operations, enabling interaction with a **relational database (SQLite)** through well-structured contracts.
- Developed concrete **repository implementations**, such as `MatchRepository`, to execute SQL queries for retrieving matches, updating seat availability, and managing ticket sales.
- Integrated **Log4j2 logging** within repository methods to trace key actions like database reads, updates, and error handling, ensuring runtime visibility and debugging support.
- Configured **database connection management** via the `DBUtils` utility class, which loads connection parameters dynamically from the external `db.properties` file, supporting flexible and secure configuration without hardcoding values.

