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

**Base URL:** `/api/auth`
| Método | Endpoint       | Descrição                          |
|--------|----------------|-----------------------------------|
| POST   | `/signup`      | Registrar novo usuário            |
| POST   | `/login`       | Fazer login e obter JWT token     |
| GET    | `/me`          | Obter informações do usuário logado |

### 📦 Pedidos
**Base URL:** `/api/orders`
| Método | Endpoint            | Descrição                          |
|--------|---------------------|-----------------------------------|
| GET    | `/`                 | Listar pedidos com filtros        |
| POST   | `/`                 | Criar novo pedido                 |
| PUT    | `/{id}`             | Atualizar pedido completo         |
| PUT    | `/{id}/status`      | Atualizar status do pedido        |
| DELETE | `/{id}`             | Excluir pedido                    |

### 🛍️ Produtos
**Base URL:** `/api/products`
| Método | Endpoint    | Descrição                          |
|--------|-------------|-----------------------------------|
| GET    | `/`         | Listar produtos com filtros       |
| POST   | `/`         | Criar novo produto                |
| PUT    | `/{id}`     | Atualizar produto completo        |
| DELETE | `/{id}`     | Excluir produto                   |

### 📊 Relatórios
**Base URL:** `/api/reports`
| Método | Endpoint          | Descrição                          |
|--------|-------------------|-----------------------------------|
| GET    | `/sales`          | Relatório de vendas               |
| GET    | `/orders/status`  | Distribuição de status de pedidos |
