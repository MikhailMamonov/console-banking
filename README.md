# Console Banking Application

A console-based banking application built in Java using the Spring Framework for user and account management.

## 📋 Description

The application provides a console interface for performing banking operations:
- User creation and management
- Bank account creation and management
- Deposits and withdrawals
- Transfers between accounts
- Account closure with automatic balance transfer

## 🛠️ Technologies

- **Java 17**
- **Spring Framework 6.1.10** (Spring Context)
- **Maven** for dependency management
- **Jakarta Annotation API** for annotations
- **PostgreSQL** as the database system

## 📁 Project Structure
text
 ```text
📁 console-banking/
│
├── 📄 pom.xml                                    # 📦 Maven configuration
│
└── 📁 src/
    └── 📁 main/
        │
        ├── 📁 java/
        │   └── 📁 com/
        │       └── 📁 example/
        │           └── 📁 banking/
        │               │
        │               ├── 📄 Application.java                     # 🚀 Entry point
        │               │
        │               ├── 📁 config/
        │               │   └── 📄 SpringConfig.java               # ⚙️ Spring configuration
        │               │
        │               ├── 📁 console/
        │               │   ├── 📄 OperationCommand.java           # 🎮 Command interface
        │               │   ├── 📄 ConsoleOperation.java           # 📋 Operation enum
        │               │   └── 📁 command/
        │               │       ├── 📄 BaseCommand.java            # 🏛️ Base class
        │               │       ├── 📄 CreateUserCommand.java
        │               │       ├── 📄 ShowAllUsersCommand.java
        │               │       ├── 📄 ShowUserAccountsCommand.java
        │               │       ├── 📄 CreateAccountCommand.java
        │               │       ├── 📄 CloseAccountCommand.java
        │               │       ├── 📄 DepositCommand.java
        │               │       ├── 📄 WithdrawCommand.java
        │               │       ├── 📄 TransferCommand.java
        │               │       └── 📄 ExitCommand.java
        │               │
        │               ├── 📁 model/
        │               │   └── 📁 entity/
        │               │       ├── 📄 User.java                  # 👤 User model
        │               │       └── 📄 Account.java               # 💰 Account model
        │               │
        │               ├── 📁 service/
        │               │   ├── 📄 AccountService.java            # 📋 Account interface
        │               │   ├── 📄 AccountServiceImpl.java        # 🔧 Account implementation
        │               │   ├── 📄 UserService.java               # 📋 User interface
        │               │   ├── 📄 UserServiceImpl.java           # 🔧 User implementation
        │               │   ├── 📄 ConfigService.java             # ⚙️ Configuration service
        │               │   ├── 📄 ConsoleInputService.java       # ⌨️ Console input
        │               │   ├── 📄 IdGeneratorService.java        # 🔑 ID generation
        │               │   ├── 📄 AccountLogger.java             # 📝 Logging
        │               │   ├── 📄 AccountTransactionProcessor.java # 💳 Transaction processing
        │               │   └── 📄 OperationsConsoleListener.java  # 🎯 Command handling
        │               │
        │               └── 📁 exception/
        │                   ├── 📄 BankingException.java          # ❌ Main exception
        │                   └── 📄 ErrorType.java                 # 📋 Error types
        │
        └── 📁 resources/
            └── 📄 application.properties                          # ⚙️ Application settings
```
## 🚀 Installation and Setup

### Requirements
- Java 17 or higher
- Maven 3.6+
- Docker (for database)


## 🛠️ Database Setup and Environment Configuration

The application uses PostgreSQL as its database, deployed in isolated Docker containers.

### 1. Starting PostgreSQL and pgAdmin via Docker

To start the database and web management interface, run the following commands in your terminal:

```bash
# Create Docker internal network for containers
sudo docker network create banking-network

# Start PostgreSQL container (accessible locally on port 5433)
sudo docker run --name banking-postgres \
  --network banking-network \
  -e POSTGRES_PASSWORD=stud_password \
  -e POSTGRES_DB=banking_db \
  -p 5433:5432 \
  -d postgres:latest

# Start pgAdmin 4 container (web interface)
sudo docker run --name banking-pgadmin \
  --network banking-network \
  -e PGADMIN_DEFAULT_EMAIL=student@test.com \
  -e PGADMIN_DEFAULT_PASSWORD=admin_password \
  -p 8080:80 \
  -d dpage/pgadmin4
```

### 2. Connecting via pgAdmin

1. Open your browser and go to: http://localhost:8080
2. Log in using the credentials:
    * **Email**: `student@test.com`
    * **Password**: `admin_password`
3. Click **Add New Server** and on the **Connection** tab specify:
    * **Host name/address**: `banking-postgres` *(container name in the internal network)*
    * **Port**: `5432` *(internal database port)*
    * **Maintenance database**: `banking_db`
    * **Username**: `postgres`
    * **Password**: `stud_password`

### 3. Application Configuration

Ensure the `src/main/resources/application.properties` file contains the correct connection settings for your local machine:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5433/banking_db
spring.datasource.username=postgres
spring.datasource.password=stud_password
spring.datasource.driver-class-name=org.postgresql.Driver

# Automatic schema management (ORM Hibernate)
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### 4. Compilation and Application Launch

Since the project uses a modern Java version, building and running should be done with explicit JDK path specification via the JAVA_HOME environment variable:

```bash
# Clean and compile the project for Java 21
JAVA_HOME=/path/to/your/jdk ./mvnw clean compile

# Example with Axiom JDK 26:
JAVA_HOME=/home/student/.jdks/axiomjdk-26 ./mvnw clean compile

# Run the Spring Boot application
JAVA_HOME=/home/student/.jdks/axiomjdk-26 ./mvnw spring-boot:run
```

### Build
```bash
mvn clean package
```

### Run

```bash
java -jar target/console-banking-1.0-SNAPSHOT-jar-with-dependencies.jar
```

Or via Maven:

```bash
mvn exec:java -Dexec.mainClass="com.example.banking.Application"
```

### 📝 Available Operations

After launching the application, you'll have access to a menu with the following operations:

| Operation      | Description                           |
|----------------|---------------------------------------|
| CREATE_USER    | Create a new user                    |
| SHOW_USERS     | View all users                       |
| DELETE_USER    | Delete a user                        |
| CREATE_ACCOUNT | Create a new account for a user      |
| SHOW_ACCOUNTS  | View a user's accounts               |
| DEPOSIT        | Deposit funds into an account        |
| WITHDRAW       | Withdraw funds from an account       |
| TRANSFER       | Transfer between accounts            |
| CLOSE_ACCOUNT  | Close an account                     |
| EXIT           | Exit the application                 |

### ⚙️ Configuration

Application settings are stored in `application.properties`:
```properties
# Account settings
account.default-amount=100.0           # Initial balance when creating an account
account.transfer-commission=50         # Commission for transfers between users
account.minimum-balance=0.0            # Minimum balance
account.maximum-balance=1000000.0      # Maximum balance

# User settings
user.max-accounts=5                    # Maximum number of accounts per user
user.login.min-length=3                # Minimum login length
user.login.max-length=20               # Maximum login length

# Application settings
app.name=Banking Application
app.version=1.0.0
app.enable-logging=true
```

## 🏗️ Architecture

### Service Layer

| Service | Responsibility |
|---------|---------------|
| AccountService | Bank account management |
| UserService | User management |
| ConfigService | Access to configuration |
| ConsoleInputService | Console input handling |
| IdGeneratorService | Unique ID generation |
| OperationsConsoleListener | Command handling |

### Data Models

**User**: User with login and list of accounts

**Account**: Bank account with balance

### Error Handling

- **Unified exception handling** via `BankingException`
- **Typed errors** via `ErrorType`
- **User-friendly messages** for the end user

## 🔍 Implementation Features

### Validation

- Login uniqueness and length validation

- Transaction amount validation (positive number)

- Sufficient funds check on account

- User/account existence verification

### Commission

- Transfers between accounts of the same user - no commission

- Transfers between different users - with commission (configurable)

### Account Closure
- Automatic transfer of remaining balance to another user account

- Verification of alternative account existence

- Prevention of closing the only account
### Logging
- All operations are logged through AccountLogger

- Unified message templates
