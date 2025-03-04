# Store Inventory Management System

This project implements a **microservices-based store inventory system** that enables users to browse and purchase products.

## 🏗️ Tech Stack
![JAVA](https://img.shields.io/badge/java-%23EA2D2E?style=for-the-badge&logo=java&logoColor=white)
![SPRING BOOT](https://img.shields.io/badge/spring%20boot-%236DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![MYSQL](https://img.shields.io/badge/mysql-%234479A1?style=for-the-badge&logo=mysql&logoColor=white)
![JENKINS](https://img.shields.io/badge/jenkins-%23D24939?style=for-the-badge&logo=jenkins&logoColor=white)

## 📡 API Flow
This section provides an overview of the interaction between the **User**, **Frontend Service**, **Order Service**, and **Catalog Service** in the application.
```mermaid
sequenceDiagram
  participant User
  participant Frontend Service
  participant Order Service
  participant Catalog Service
  
  User->>Frontend Service: GET /products/{productName} (Request product details)
  Frontend Service->>Catalog Service: Request product information
  Catalog Service->>Frontend Service: Return product name, price, quantity
  Frontend Service->>User: Return product name, price, quantity
  
  User->>Frontend Service: POST /orders (Place order)
  Frontend Service->>Order Service: Forward order request
  Order Service->>Catalog Service: Forward order request
  Catalog Service->>Order Service: Return order confirmation number
  Order Service->>Frontend Service: Return order confirmation number
  Frontend Service->>User: Return order confirmation number

  User->>Frontend Service: GET /orders/{orderNumber} (Retrieve order details)
  Frontend Service->>Order Service: Forward order request
  Order Service->>Catalog Service: Forward order request
  Catalog Service->>Order Service: Return order details
  Order Service->>Frontend Service: Return order details
  Frontend Service->>User: Return order details
```
