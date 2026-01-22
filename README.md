# Budget Bees API

Backend service for the Budget Bees application, built with Spring Boot.

## Features

- **Spring Boot 4**: Leveraging the latest Spring framework features.
- **MVC Architecture**: Clean separation of concerns (Controller, Service, Repository, Model).
- **Security**: JWT-based Authentication and Role-based Access Control (RBAC).
- **Database**: PostgreSQL support (with Testcontainers for integration testing).
- **Migrations**: Integration with [budget-bees-db](../budget-bees-db) for Liquibase changesets.

## Prerequisites

- **Java 21** or higher
- **Docker** (Required for running tests via Testcontainers)
- **PostgreSQL** (For local development if not using containers)

## Getting Started

### 1. Build the Project

```bash
./gradlew clean build
```

### 2. Run Tests

Ensure Docker is running, then execute:

```bash
./gradlew test
```

### 3. Run the Application

```bash
./gradlew bootRun
```

## Authentication

The application uses JWT for security.

### Login

**Endpoint**: `POST /auth/login`

**Request Body**:
```json
{
  "username": "your_username",
  "password": "your_password"
}
```

**Response**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

### Accessing Secured Endpoints

Include the token in the `Authorization` header:

```
Authorization: Bearer <your_token>
```

**Example Secured Endpoint**: `GET /ping`

## Development

- **Formatting**: The project uses Spotless for code formatting. Run `./gradlew spotlessApply` to format code.
- **Database Migrations**: The test suite automatically runs Liquibase migrations from the sibling `budget-bees-db` project against a temporary container.
