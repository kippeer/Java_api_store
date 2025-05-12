# Store API

A RESTful API for an online store management system built with Spring Boot.

## Features

- Complete CRUD operations for products and orders
- User authentication with JWT
- Role-based authorization (ADMIN, USER)
- Payment processing with multiple payment methods
- Order status management
- Product inventory management
- Comprehensive API documentation with Swagger/OpenAPI

## Technology Stack

- Java 17
- Spring Boot 3.x
- Spring Security with JWT
- Spring Data JPA
- PostgreSQL
- Swagger/OpenAPI for documentation
- JUnit 5 and Mockito for testing

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven
- PostgreSQL

### Configuration

The application can be configured through the `application.yml` file located in the `src/main/resources` directory.

Key configuration properties:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/storedb
    username: postgres
    password: postgres
```

### Running the Application

To build and run the application:

```bash
mvn clean install
mvn spring-boot:run
```

The application will be available at http://localhost:8080

### API Documentation

Swagger UI is available at: http://localhost:8080/swagger-ui/index.htm

API documentation is available at: http://localhost:8080/v3/api-docs
