# Console Banking Application

Консольное банковское приложение на Java с использованием Spring Framework для управления пользователями и счетами.

## 📋 Описание

Приложение предоставляет консольный интерфейс для выполнения банковских операций:
- Создание и управление пользователями
- Создание и управление банковскими счетами
- Пополнение и снятие средств
- Переводы между счетами
- Закрытие счетов с автоматическим переводом остатка

## 🛠️ Технологии

- **Java 17**
- **Spring Framework 6.1.10** (Spring Context)
- **Maven** для управления зависимостями
- **Jakarta Annotation API** для аннотаций

## 📁 Структура проекта
 ```text
📁 console-banking/
│
├── 📄 pom.xml                                    # 📦 Maven конфигурация
│
└── 📁 src/
    └── 📁 main/
        │
        ├── 📁 java/
        │   └── 📁 com/
        │       └── 📁 example/
        │           └── 📁 banking/
        │               │
        │               ├── 📄 Application.java                     # 🚀 Точка входа
        │               │
        │               ├── 📁 config/
        │               │   └── 📄 SpringConfig.java               # ⚙️ Конфигурация Spring
        │               │
        │               ├── 📁 console/
        │               │   ├── 📄 OperationCommand.java           # 🎮 Интерфейс команд
        │               │   ├── 📄 ConsoleOperation.java           # 📋 Enum операций
        │               │   └── 📁 command/
        │               │       ├── 📄 BaseCommand.java            # 🏛️ Базовый класс
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
        │               │       ├── 📄 User.java                  # 👤 Модель пользователя
        │               │       └── 📄 Account.java               # 💰 Модель счета
        │               │
        │               ├── 📁 service/
        │               │   ├── 📄 AccountService.java            # 📋 Интерфейс счетов
        │               │   ├── 📄 AccountServiceImpl.java        # 🔧 Реализация счетов
        │               │   ├── 📄 UserService.java               # 📋 Интерфейс пользователей
        │               │   ├── 📄 UserServiceImpl.java           # 🔧 Реализация пользователей
        │               │   ├── 📄 ConfigService.java             # ⚙️ Сервис конфигурации
        │               │   ├── 📄 ConsoleInputService.java       # ⌨️ Ввод с консоли
        │               │   ├── 📄 IdGeneratorService.java        # 🔑 Генерация ID
        │               │   ├── 📄 AccountLogger.java             # 📝 Логирование
        │               │   ├── 📄 AccountTransactionProcessor.java # 💳 Обработка транзакций
        │               │   └── 📄 OperationsConsoleListener.java  # 🎯 Обработка команд
        │               │
        │               └── 📁 exception/
        │                   ├── 📄 BankingException.java          # ❌ Основное исключение
        │                   └── 📄 ErrorType.java                 # 📋 Типы ошибок
        │
        └── 📁 resources/
            └── 📄 application.properties                          # ⚙️ Настройки приложения
```
## 🚀 Установка и запуск

### Требования
- Java 17 или выше
- Maven 3.6+


## 🛠️ Запуск базы данных и настройка окружения

Приложение использует СУБД **PostgreSQL**, развернутую в изолированных контейнерах Docker.

### 1. Запуск PostgreSQL и pgAdmin через Docker

Для запуска СУБД и веб-интерфейса управления базой данных выполните в терминале следующие команды:

```bash
# Создание внутренней сети Docker для контейнеров
sudo docker network create banking-network

# Запуск контейнера PostgreSQL (доступен локально на порту 5433)
sudo docker run --name banking-postgres \
  --network banking-network \
  -e POSTGRES_PASSWORD=stud_password \
  -e POSTGRES_DB=banking_db \
  -p 5433:5432 \
  -d postgres:latest

# Запуск контейнера pgAdmin 4 (веб-интерфейс)
sudo docker run --name banking-pgadmin \
  --network banking-network \
  -e PGADMIN_DEFAULT_EMAIL=student@test.com \
  -e PGADMIN_DEFAULT_PASSWORD=admin_password \
  -p 8080:80 \
  -d dpage/pgadmin4
```

### 2. Подключение через pgAdmin

1. Откройте браузер и перейдите по адресу: [http://localhost:8080](http://localhost:8080)
2. Войдите, используя данные:
    * **Email**: `student@test.com`
    * **Password**: `admin_password`
3. Нажмите **Add New Server** и на вкладке **Connection** укажите:
    * **Host name/address**: `banking-postgres` *(имя контейнера во внутренней сети)*
    * **Port**: `5432` *(внутренний порт СУБД)*
    * **Maintenance database**: `banking_db`
    * **Username**: `postgres`
    * **Password**: `stud_password`

### 3. Конфигурация приложения

Убедитесь, что в файле `src/main/resources/application.properties` прописаны актуальные настройки подключения к вашей локальной машине:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5433/banking_db
spring.datasource.username=postgres
spring.datasource.password=stud_password
spring.datasource.driver-class-name=org.postgresql.Driver

# Автоматическое управление схемами таблиц (ORM Hibernate)
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### 4. Компиляция и запуск приложения

Так как проект использует современную версию Java, сборку и запуск рекомендуется выполнять с явным указанием пути к установленной JDK через переменную окружения `JAVA_HOME`:

```bash
# Очистка и компиляция проекта под целевую Java 21
String format: JAVA_HOME=/path/to/your/jdk ./mvnw clean compile

# Пример для Axiom JDK 26:
JAVA_HOME=/home/student/.jdks/axiomjdk-26 ./mvnw clean compile

# Запуск Spring Boot приложения
JAVA_HOME=/home/student/.jdks/axiomjdk-26 ./mvnw spring-boot:run
```

### Сборка
```bash
mvn clean package
```

### Запуск

```bash
java -jar target/console-banking-1.0-SNAPSHOT-jar-with-dependencies.jar
```

Или через Maven:

```bash
mvn exec:java -Dexec.mainClass="com.example.banking.Application"
```

### 📝 Доступные операции

После запуска приложения вам будет доступно меню со следующими операциями:

| Операция       | Описание                                |
|----------------|-----------------------------------------|
| CREATE_USER    | Создание нового пользователя            |
| SHOW_USERS     | Просмотр всех пользователей             |
| DELETE_USER    | Удаление пользователя                   |
| CREATE_ACCOUNT | Создание нового счета для пользователя  |
| SHOW_ACCOUNTS  | Просмотр счетов пользователя            |
| DEPOSIT        | Пополнение счета                        |
| WITHDRAW       | Снятие средств со счета                 |
| TRANSFER       | Перевод между счетами                   |
| CLOSE_ACCOUNT  | Закрытие счета                          |
| EXIT           | Выход из приложения                     |

### ⚙️ Конфигурация

Настройки приложения хранятся в application.properties:
```properties
# Настройки счетов
account.default-amount=100.0           # Начальный баланс при создании счета
account.transfer-commission=50         # Комиссия при переводе между пользователями
account.minimum-balance=0.0            # Минимальный баланс
account.maximum-balance=1000000.0      # Максимальный баланс

# Настройки пользователей
user.max-accounts=5                    # Максимальное количество счетов на пользователя
user.login.min-length=3                # Минимальная длина логина
user.login.max-length=20               # Максимальная длина логина

# Настройки приложения
app.name=Banking Application
app.version=1.0.0
app.enable-logging=true
```

## 🏗️ Архитектура

### Сервисный слой

**AccountService**            Управление банковскими счетами       
**UserService**               Управление пользователями                  
**ConfigService**             Доступ к конфигурации                      
**ConsoleInputService**       Обработка ввода с консоли                  
**IdGeneratorService**        Генерация уникальных ID                    
**OperationsConsoleListener** Обработка команд                           

### Модели данных

**User**: Пользователь с логином и списком счетов

**Account**: Банковский счет с балансом

### Обработка ошибок

- **Единый механизм обработки исключений** через `BankingException`
- **Типизированные ошибки** через `ErrorType`
- **Дружественные сообщения** для пользователя

## 🔍 Особенности реализации

### Валидация

- Проверка логина на уникальность и длину

- Проверка суммы транзакции (положительное число)

- Проверка наличия средств на счете

- Проверка существования пользователя/счета

### Комиссия

- Перевод между счетами одного пользователя - без комиссии

- Перевод между разными пользователями - с комиссией (настраивается)

### Закрытие счета
- Автоматический перевод остатка на другой счет пользователя

- Проверка наличия альтернативного счета

- Запрет закрытия единственного счета
### Логирование

- Все операции логируются через AccountLogger

- Единые шаблоны сообщений