# API de Loja

Uma API RESTful para sistema de gestão de e-commerce construída com Spring Boot.

## Funcionalidades

- Operações CRUD completas para produtos e pedidos
- Autenticação de usuários com JWT
- Autorização baseada em papéis (ADMIN, USER)
- Processamento de pagamentos com múltiplos métodos
- Gerenciamento de status de pedidos
- Controle de estoque de produtos
- Documentação abrangente da API com Swagger/OpenAPI

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

A aplicação pode ser configurada através do arquivo application.yml localizado em src/main/resources.

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

### Documentação da API
A Swagger UI está disponível em: http://localhost:8080/swagger-ui/index.html

A documentação da API está disponível em: http://localhost:8080/v3/api-docs
