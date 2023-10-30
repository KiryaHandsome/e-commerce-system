# E-commerce System Microservices Project

[![Build and Test](https://github.com/KiryaHandsome/e-commerce-system/actions/workflows/ci.yaml/badge.svg)](https://github.com/KiryaHandsome/e-commerce-system/actions/workflows/ci.yaml)
[![CodeFactor](https://www.codefactor.io/repository/github/kiryahandsome/e-commerce-system/badge)](https://www.codefactor.io/repository/github/kiryahandsome/e-commerce-system)

## Introduction

This project is an E-commerce System implemented as a set of microservices using Spring Boot and Kafka messaging. The system comprises three microservices, each serving a specific function: Inventory, Order, and Payment.

## Project Overview

The project aims to demonstrate the concept of microservices communication and transactional consistency using the SAGA pattern. Microservice A acts as the orchestrator and communicates with two independent microservices, B and C, through Kafka messages. The system is also secured using Spring Security.

## Services

1. **Inventory Service (Service B):** Manages product inventory and stock.
2. **Order Service (Service A):** Orchestrates the order creation process.
3. **Payment Service (Service C):** Handles payment transactions.

## Authentication

Access to the API is secured using Spring Security. Users need to authenticate to access the endpoints.

## Endpoints

### Order Service

- **POST /api/v1/auth/login** - Authenticates a user.

- **POST /api/v1/orders** - Creates an order. Request body format:
  ```json
  {
      "productCount" : 4,
      "customerId" : 3,
      "productId" : 2,
      "totalPrice" : 18.90
  }
  ```

- **GET /api/v1/orders/{id}** - Retrieves the status of an order. For instance:
    ```json
  {
      "id": 1,
      "productCount": 4,
      "customerId": 3,
      "productId": 2,
      "totalPrice": 18.9,
      "orderStatus": "in process",
      "paymentStatus": "in process",
      "inventoryStatus": "in process"
    }
  ```

## Testing

Unit and integration tests have been implemented to ensure the reliability and correctness of the services. Kafka integration tests are carried out using Testcontainers.

## Deployment

The services, Kafka, and the database are deployed in a local Kubernetes cluster using Minikube. This deployment demonstrates a local containerized environment.

## Tech Stack

- Spring Boot: For building microservices.
- Kafka: For asynchronous messaging.
- Spring Security: For securing the API.
- PostgreSQL: As the database.
- Data JPA: For data access.
- Testcontainers: For integration testing.
- Minikube: For local Kubernetes deployment.
