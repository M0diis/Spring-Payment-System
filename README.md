# Payment Service

A Spring Boot application for managing payments, supporting creation, retrieval, cancellation, and filtering of
payments.

## Features

- REST API for payment operations (create, get, cancel, query)
- Database schema managed by Liquibase
- jOOQ code generation for type-safe SQL
- Validation and error handling
- OpenAPI (Swagger) documentation

## Tech Stack

- Java 21
- Spring Boot v3.1.4
- Gradle v8.10
- Liquibase v4.20.0
- jOOQ v3.19.8
- H2 Database (file-based, for development) v2.3.232
- Lombok

## Setup & Usage

### 1. Clone the Repository

```sh
git clone https://github.com/M0diis/Spring-Payment-System.git
cd Spring-Payment-System
```

### 2. Generate liquibase schema

Run Liquibase migrations to create the database schema

```sh
./gradlew update  # or use the Liquibase Gradle plugin task
```

This will apply all changesets and create the required tables in the H2 database.

### 3. Generate jOOQ classes

After the schema is created, generate jOOQ classes:

```sh
./gradlew generateJooq
```

This reads the database schema and generates Java classes for type-safe SQL access.

### 4. Configure the Application

Edit `src/main/resources/application.yml` as needed.
The default configuration uses an H2 in-memory database for development.

### 5. Run the Application

#### Local

```sh
./gradlew bootRun
```

#### Docker

Build the Docker image:

```sh
docker build -t payment-service .
```

Run the Docker container:

```sh
docker run -p 8000:8000 payment-service
```

The application will start on the configured port (default: 8000).

### 6. API Documentation

Visit http://localhost:8080/swagger-ui/index.html for API docs.

---

# Development Workflow

1. Change the database schema: Edit Liquibase changelogs in src/main/resources/db/changelog/.
2. Apply migrations: Run Liquibase to update the database.
3. Regenerate jOOQ classes: Run the jOOQ code generation task.
4. Develop and test: Implement features using the generated jOOQ classes and run the application.

### Project Structure

- `src/main/java/me/modkzl/aspect/` — Aspect-oriented programming (AOP) aspects
- `src/main/java/me/modkzl/config/` — Application configuration
- `src/main/java/me/modkzl/controllers/` — REST controllers
- `src/main/java/me/modkzl/enums/` — Enumerations used in the application
- `src/main/java/me/modkzl/exception/` — Custom exceptions and error handling
- `src/main/java/me/modkzl/service/` — Business logic
- `src/main/java/me/modkzl/repository/` — Database access (jOOQ)
- `src/main/java/me/modkzl/validation/` — Custom validators
- `src/main/java/me/modkzl/utils/` — Utility classes
- `src/main/java/me/modkzl/mapper/` — Request/Response/DTO/Entity mappers
- `src/main/test/groovy/me/modkzl/` — Unit and integration tests
- `src/main/resources/db/changelog/` — Liquibase changelogs
- `src/main/resources/application.yml` — Application configuration