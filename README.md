# hotel-management-system
Java microservices architecture for hotel management with role-based access, event-driven design, and AI integration.

# Hotel Management Microservices System

[![Build Status](https://img.shields.io/github/workflow/status/your-username/hotel-management/CI)](https://github.com/your-username/hotel-management/actions)  
[![License](https://img.shields.io/badge/license-MIT-green)](LICENSE)  
[![Docker](https://img.shields.io/badge/docker-ready-blue)](https://www.docker.com/)

---

## Overview

This is a **full-featured hotel management system** built with **Java Spring Boot microservices**, following a **modern cloud-native architecture**.  
It supports:

- In-house dining & room management
- Delivery orders
- Billing & notifications
- Staff management with **role-based access**
- Kafka-based **event-driven notifications**
- Observability with **Prometheus & Grafana**
- Config-driven dynamic role permissions

---

## Architecture

```
hotel-management/
├── api-gateway/           # Gateway with authentication & authorization
├── config-server/         # Centralized configuration
├── staff-service/         # Authentication & staff management
├── room-service/          # Room management
├── order-service/         # Orders (dining & delivery)
├── billing-service/       # Bill generation & Kafka producer
├── notification-service/  # Kafka consumer for notifications
├── ai-service/            # Analytics / AI suggestions
├── database/              # DB scripts
├── prometheus/            # Monitoring
├── grafana/               # Visualization dashboards
├── frontend/              # Web UI
├── docker-compose.yml     # Container orchestration
└── README.md
```

---

## Tech Stack

- **Backend**: Java, Spring Boot, Spring Cloud (Gateway, Eureka, Config Server)  
- **Messaging**: Apache Kafka  
- **Database**: PostgreSQL/MySQL (service-specific)  
- **Security**: JWT, Spring Security  
- **Monitoring**: Prometheus, Grafana  
- **Frontend**: React / Angular (optional)  
- **Containerization**: Docker, Docker Compose  

---

## Modules & Responsibilities

| Module                  | Responsibility |
|-------------------------|----------------|
| `api-gateway`           | Central routing, JWT validation, role-based authorization |
| `config-server`         | Centralized configuration for roles, secrets, service URLs |
| `staff-service`         | Authentication, staff CRUD, JWT token generation |
| `room-service`          | Room CRUD, availability, booking management |
| `order-service`         | Orders (dining & delivery) |
| `billing-service`       | Billing, Kafka notifications |
| `notification-service`  | Kafka consumer, email/SMS notifications, retry & dead-letter handling |
| `ai-service`            | AI/analytics (future) |
| `prometheus` & `grafana`| Monitoring, visualization |

---

## Setup & Running

### Prerequisites

- Java 17+  
- Docker & Docker Compose  
- Maven  
- Config Server repo containing `.properties` files  

### Steps

1. Clone the repository:

```bash
git clone <repo-url>
cd hotel-management
```

2. Start all services using Docker:

```bash
docker-compose up -d
```

3. Access services:

- Eureka: `http://localhost:8761`  
- API Gateway: `http://localhost:8080`  
- Prometheus: `http://localhost:9090`  
- Grafana: `http://localhost:3000` (user/pass: `admin/admin`)  
- Frontend: `http://localhost:4200` (if React/Angular)

---

## Security & Role-Based Access

- **JWT authentication** handled centrally at API Gateway.  
- **Roles are configurable** via Config Server:

```properties
roles.access.staff-service=ADMIN
roles.access.room-service=ADMIN,STAFF
roles.access.order-service=ADMIN,STAFF,CUSTOMER
roles.access.billing-service=ADMIN,STAFF
```

- Supported roles: `ADMIN`, `STAFF`, `CUSTOMER`.  
- Adding new roles or services only requires updating Config Server properties — **no code changes needed**.  

---

## Messaging & Kafka

- **Billing Service** produces events (`BILL_CREATED`).  
- **Notification Service** consumes events asynchronously.  
- Retry mechanism and dead-letter topic implemented with `DefaultErrorHandler`.  
- Kafka metrics monitored via Prometheus.  

---

## Example API Calls

### Login (staff-service)

```bash
POST /auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "password123"
}
```

Response:

```json
{
  "token": "<JWT_TOKEN>"
}
```

### Access Room Service

```bash
GET /rooms
Authorization: Bearer <JWT_TOKEN>
```

Response:

```json
[
  { "roomId": 101, "type": "Deluxe", "status": "AVAILABLE" }
]
```

### Create Billing

```bash
POST /billing
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "orderId": 1,
  "amount": 250.0
}
```

Response:

```json
{
  "billId": 1,
  "orderId": 1,
  "amount": 250.0,
  "status": "PAID"
}
```

---

## Monitoring & Observability

- All services expose `/actuator/prometheus`.  
- Prometheus scrapes metrics for:

  - HTTP requests & response times  
  - Kafka consumer lag & message count  
  - Service health & CPU/memory usage  

- Grafana dashboards visualize metrics across all services.  

---

## Future Enhancements

- AI module (`ai-service`) for analytics  
- Dynamic token refresh & JWT blacklisting  
- Multi-tenant support  
- Granular RBAC rules per service  
- Cloud deployment with Kubernetes / Helm charts  

---


