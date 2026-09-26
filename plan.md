eCom Spring Boot Backend --- Development Plan

Project Goal

Build a practical e-commerce backend in Java/Spring Boot that starts as
a clean monolithic REST API and gradually evolves toward an
industry-style architecture.

The project is intentionally focused on backend engineering rather than
frontend, cloud deployment, Docker, or Kubernetes.

Technology Stack

Core Stack

Java 17

Spring Boot

Spring Web

Spring Data JPA

H2 Database

Jakarta Bean Validation

Spring Security + JWT --- Phase 3

Maven

Development Principles

No Lombok

Use explicit constructors/getters/setters where required

Use ResponseEntity for REST responses

Use DTOs instead of exposing JPA entities directly through APIs

Use @RestControllerAdvice for centralized exception handling

Use enums for business statuses

Store enum values as strings using @Enumerated(EnumType.STRING)

Prefer soft deletion/deactivation over physical deletion for
business data

Keep the initial application monolithic and modular

Phase 1 --- Project Setup & Domain Model

Objective

Create the Spring Boot project, configure H2/JPA, and build the complete
entity/domain model before implementing business APIs.

1. Basic Project Setup

Create the Spring Boot project with:

Java 17

Spring Web

Spring Data JPA

H2 Database

Validation

Maven

Suggested package structure:

src/main/java/com/example/ecommerce/

├── controller/
├── service/
├── repository/
├── entity/
├── dto/
├── exception/
├── config/
└── EcommerceApplication.java

Configuration should include:

H2 database configuration

JPA/Hibernate configuration through Spring Data JPA

SQL logging during development if useful

H2 console enabled for development

Spring Data JPA uses a JPA implementation such as Hibernate
underneath. Hibernate does not need to be treated as a separate
application-level dependency in the project design.

2. Entity Design

The initial domain contains 7 database entities/tables plus the
BaseEntity mapped superclass.

BaseEntity
    │
    ├── User
    ├── Category
    ├── Product
    ├── Cart
    ├── CartItem
    ├── Order
    └── OrderItem

BaseEntity is not a database table when implemented with
@MappedSuperclass.

2.1 BaseEntity

Purpose: common persistence fields shared by entities.

Fields:

id
createdAt
updatedAt

Suggested types:

id          → Long
createdAt   → LocalDateTime
updatedAt   → LocalDateTime

Use:

@MappedSuperclass

Do not put a generic status field here because different entities have
different lifecycle states.

2.2 User

Represents an e-commerce customer.

Fields:

id
createdAt
updatedAt

name
email
password
phone
status

Status:

ACTIVE
INACTIVE
BLOCKED
DELETED

Relationship:

User 1 ───── 1 Cart
User 1 ───── N Order

Notes:

Email should be unique.

Password will later contain a BCrypt hash, never a plain-text
password.

User authentication is implemented in Phase 3.

DELETED represents soft deletion.

2.3 Category

Represents a product category.

Fields:

id
createdAt
updatedAt

name
description
status

Status:

ACTIVE
INACTIVE
DELETED

Relationship:

Category 1 ───── N Product

Rules:

Category name should be unique.

Products belong to a category.

A deleted/inactive category should not normally be selectable for
new products.

2.4 Product

Represents an item available for purchase.

Fields:

id
createdAt
updatedAt

name
description
price
stockQuantity
status
category

Status:

ACTIVE
INACTIVE
OUT_OF_STOCK
DISCONTINUED
DELETED

Relationship:

Category 1 ───── N Product
Product 1 ───── N CartItem
Product 1 ───── N OrderItem

Rules:

Price must be positive.

Stock quantity cannot be negative.

Only active products should normally be available for purchase.

OUT_OF_STOCK can be derived from stock quantity, but keeping it as
a status is acceptable for this learning project if its behavior is
kept consistent.

2.5 Cart

Represents the current shopping cart of a user.

Fields:

id
createdAt
updatedAt

status
user

Status:

ACTIVE
CHECKED_OUT
ABANDONED

Relationship:

User 1 ───── 1 Cart
Cart 1 ───── N CartItem

Rules:

A user has one active cart.

A cart contains multiple cart items.

Cart should not be physically deleted during normal business
operations.

2.6 CartItem

Represents one product and quantity inside a cart.

Fields:

id
createdAt
updatedAt

quantity
cart
product

Relationships:

Cart 1 ───── N CartItem
Product 1 ── N CartItem

Rules:

Quantity must be greater than zero.

A cart should normally contain one CartItem per Product.

Adding an existing product to a cart should increase its quantity
rather than create duplicate CartItems.

2.7 Order

Represents a completed checkout/purchase request.

Fields:

id
createdAt
updatedAt

status
totalAmount
user

Status:

CREATED
CONFIRMED
PROCESSING
SHIPPED
DELIVERED
CANCELLED

Relationship:

User 1 ───── N Order
Order 1 ──── N OrderItem

Important:

totalAmount should be stored as part of the order.

Do not calculate historical order totals from the current Product
price.

Order status represents the order lifecycle.

2.8 OrderItem

Represents a product snapshot inside an order.

Fields:

id
createdAt
updatedAt

quantity
unitPrice
order
product

Relationships:

Order 1 ───── N OrderItem
Product 1 ─── N OrderItem

Important:

unitPrice must be stored on OrderItem.

Example:

Product price today = ₹1000
Customer buys = 2
OrderItem:
    quantity  = 2
    unitPrice = ₹1000

If the product later becomes ₹1200, the old order must still represent
the original ₹1000 purchase price.

Entity Relationship Summary

                    ┌──────────┐
                    │   User   │
                    └────┬─────┘
                         │
                 1       │       N
                         │
              ┌──────────┴─────────┐
              │                    │
              ▼                    ▼
           ┌──────┐             ┌───────┐
           │ Cart │             │ Order │
           └──┬───┘             └───┬───┘
              │ 1                   │ 1
              │                     │
              │ N                   │ N
              ▼                     ▼
         ┌──────────┐          ┌───────────┐
         │ CartItem │          │ OrderItem │
         └────┬─────┘          └─────┬─────┘
              │ N                    │ N
              │                      │
              │ 1                    │ 1
              └──────────┬───────────┘
                         ▼
                    ┌─────────┐
                    │ Product │
                    └────┬────┘
                         │ N
                         │
                         │ 1
                         ▼
                    ┌──────────┐
                    │ Category │
                    └──────────┘

Status Strategy

Use Java enums rather than numeric status codes.

Example:

public enum UserStatus {
    ACTIVE,
    INACTIVE,
    BLOCKED,
    DELETED
}

Persist as:

@Enumerated(EnumType.STRING)
private UserStatus status;

Avoid:

0 = ACTIVE
1 = INACTIVE
2 = DELETED

String-backed enums are much easier to understand in database records
and logs.

Do not force every entity to share the same status enum. Use
domain-specific enums where the lifecycle differs.

Phase 2 --- REST API & Business Modules

Objective

Turn the entity model into a clean REST API with DTOs, validation,
service-layer business logic, standardized responses, pagination,
sorting, filtering, and centralized exception handling.

Module 1 --- Auth/User Module

Initial endpoints:

POST /api/users
GET /api/users/{id}
PUT /api/users/{id}

Later, authentication endpoints will move into the security phase:

POST /api/auth/register
POST /api/auth/login

Features:

Create user

Get user

Update user

Validate email

Prevent duplicate email

Soft-delete/deactivate user

Use DTOs

Never expose password in response DTOs

Example response DTO:

{
  "id": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "9876543210",
  "status": "ACTIVE"
}

Module 2 --- Category Module

Endpoints:

POST   /api/categories
GET    /api/categories
GET    /api/categories/{id}
PUT    /api/categories/{id}
DELETE /api/categories/{id}

Features:

Create category

Update category

Get category

List categories

Soft-delete category

Validate category name

Filter active/inactive categories

Module 3 --- Product Module

Endpoints:

POST   /api/products
GET    /api/products
GET    /api/products/{id}
PUT    /api/products/{id}
DELETE /api/products/{id}

Features:

Create product

Update product

Get product

Soft-delete/deactivate product

Assign product to category

Validate price

Validate stock

Filter by category

Filter by status

Search by product name

Sort by price/name/createdAt

Pagination

Example:

GET /api/products?page=0&size=10&sort=price,asc

Example filtering:

GET /api/products?categoryId=2&status=ACTIVE

Module 4 --- Cart Module

Endpoints:

GET    /api/cart
POST   /api/cart/items
PUT    /api/cart/items/{id}
DELETE /api/cart/items/{id}
DELETE /api/cart

Features:

Get current user's cart

Add product to cart

Increase/decrease quantity

Remove cart item

Clear cart

Validate product availability

Validate stock

Prevent duplicate CartItems for the same product

Important business rule:

If product already exists in cart:
    increase quantity

Otherwise:
    create CartItem

Module 5 --- Order Module

Endpoints:

POST /api/orders
GET  /api/orders
GET  /api/orders/{id}
PUT  /api/orders/{id}/status

Checkout flow:

User
 ↓
Cart
 ↓
Validate Cart
 ↓
Validate Product Status
 ↓
Validate Stock
 ↓
Create Order
 ↓
Create OrderItems
 ↓
Calculate Total
 ↓
Reduce Stock
 ↓
Clear Cart

Use:

@Transactional

for the checkout operation.

The entire checkout operation should succeed or roll back as a unit.

REST API Standards

ResponseEntity

Use ResponseEntity to control HTTP status and response headers.

Examples:

201 CREATED
200 OK
204 NO CONTENT
400 BAD REQUEST
401 UNAUTHORIZED
403 FORBIDDEN
404 NOT FOUND
409 CONFLICT

Do not return HTTP 200 for every situation.

JSON Response Design

Use dedicated response DTOs.

Do not expose JPA entities directly.

Recommended structure:

Request DTO
     ↓
Controller
     ↓
Service
     ↓
Entity
     ↓
Repository
     ↓
Database

and:

Database
   ↓
Entity
   ↓
Service
   ↓
Response DTO
   ↓
Controller
   ↓
JSON

Validation

Use Jakarta Bean Validation.

Examples:

@NotBlank
private String name;

@Email
private String email;

@NotNull
@Positive
private BigDecimal price;

@Min(0)
private Integer stockQuantity;

Use:

@Valid

in controller request bodies.

Global Exception Handling

Create:

@RestControllerAdvice
public class GlobalExceptionHandler

Handle common cases:

ResourceNotFoundException
DuplicateResourceException
InvalidOperationException
MethodArgumentNotValidException

Standardize the error JSON.

Example:

{
  "timestamp": "2026-09-26T20:00:00",
  "status": 404,
  "error": "RESOURCE_NOT_FOUND",
  "message": "Product not found",
  "path": "/api/products/10"
}

Pagination, Sorting & Filtering

Implement these primarily in the Product and Category modules.

Pagination:

GET /api/products?page=0&size=10

Sorting:

GET /api/products?sort=price,asc

Filtering:

GET /api/products?categoryId=2&status=ACTIVE

Combined:

GET /api/products?categoryId=2&status=ACTIVE&page=0&size=10&sort=price,asc

Use Spring Data Pageable.

Phase 3 --- Spring Security + JWT

Objective

Secure the monolithic application using Spring Security and stateless
JWT authentication.

1. Authentication

Implement:

POST /api/auth/register
POST /api/auth/login

Login flow:

Login Request
     ↓
AuthenticationManager
     ↓
DaoAuthenticationProvider
     ↓
UserDetailsService
     ↓
Password verification
     ↓
JWT generated
     ↓
JWT returned to client

Passwords must be stored using:

BCryptPasswordEncoder

Never store plain-text passwords.

2. JWT Filter

Implement a JWT authentication filter.

Flow:

Request
   ↓
Extract Authorization header
   ↓
Bearer JWT
   ↓
Validate JWT signature/claims
   ↓
Extract username/user ID
   ↓
Load user
   ↓
Create Authentication
   ↓
SecurityContextHolder
   ↓
Controller

Use stateless sessions.

3. Authorization

Roles:

CUSTOMER
ADMIN

Example:

CUSTOMER
- View products
- Manage own cart
- Create orders
- View own orders

ADMIN
- Manage products
- Manage categories
- Manage users
- View/manage orders

Important:

Do not trust a userId supplied by the client when accessing "my cart"
or "my orders."

Derive the authenticated identity from the SecurityContext/JWT.

Phase 4 --- Microservices Evolution

Objective

Take the working monolith and split it into independently deployable
services.

Do this only after the monolith is stable.

Service 1 --- User Service

Owns:

User
Authentication
Roles

Responsibilities:

Registration

Login

User profile

User status

Authentication

Service 2 --- Product Service

Owns:

Category
Product

Responsibilities:

Product CRUD

Category CRUD

Product search

Filtering

Pagination

Product status

Inventory/stock information

Service 3 --- Cart Service

Owns:

Cart
CartItem

Responsibilities:

Create/get cart

Add item

Update quantity

Remove item

Clear cart

Service 4 --- Order Service

Owns:

Order
OrderItem

Responsibilities:

Checkout

Order creation

Order history

Order status

Order cancellation

Order total

Microservice Database Principle

Do not keep one shared database for all services.

Conceptually:

User Service
     ↓
User DB

Product Service
     ↓
Product DB

Cart Service
     ↓
Cart DB

Order Service
     ↓
Order DB

For this learning project, H2 can still be used while learning the
architecture.

The important concept is:

Each service owns its data.

API Gateway

Introduce an API Gateway in front of the services.

Client
   ↓
API Gateway
   ↓
 ┌───────────────┬───────────────┬───────────────┐
 ↓               ↓               ↓               ↓
User           Product          Cart            Order
Service        Service          Service         Service

Learn:

Routing

Authentication at gateway/service level

Service-to-service communication

Centralized request handling

Spring Cloud technologies can be introduced here as appropriate.

Phase 5 --- Later Industrial Improvements

Keep this phase short for now.

Implement these after the main project is complete:

Flyway --- database schema/version migrations

Swagger/OpenAPI --- API documentation

Spring Boot Actuator --- health and metrics

Structured logging --- useful application/service logs

JUnit + Mockito --- unit testing

Spring Boot integration tests

Testcontainers --- later, when moving beyond H2

PostgreSQL --- replace H2 for a more realistic database
environment

Docker --- containerization

CI/CD --- automated build/test pipeline

Cloud deployment --- later

Redis/Kafka --- only after understanding why the system needs
caching/event-driven communication

Do not attempt all of these before the core project works.

Definition of Done

Phase 1

Spring Boot project created

H2 configured

BaseEntity implemented

User entity

Category entity

Product entity

Cart entity

CartItem entity

Order entity

OrderItem entity

Relationships mapped

Status enums created

Database tables verified in H2

Phase 2

DTOs

Repository layer

Service layer

Controller layer

User/Auth module

Category module

Product module

Cart module

Order module

ResponseEntity

Proper HTTP status codes

Validation

Global exception handling

Pagination

Sorting

Filtering

Transactional checkout

Phase 3

Spring Security

BCrypt

JWT generation

JWT validation

JWT filter

SecurityContext

Role-based authorization

Customer/Admin access control

Phase 4

User Service

Product Service

Cart Service

Order Service

Separate service-owned databases

API Gateway

Service-to-service communication

Phase 5 --- Later

Flyway

Swagger/OpenAPI

Actuator

Better logging

Tests

PostgreSQL

Docker

CI/CD

Cloud deployment

Redis/Kafka when justified

Recommended Build Order

PHASE 1
Setup
  ↓
Entities
  ↓
Relationships
  ↓
Repositories

PHASE 2
User
  ↓
Category
  ↓
Product
  ↓
Cart
  ↓
Order
  ↓
DTO + Validation + Exceptions
  ↓
Pagination/Sorting/Filtering

PHASE 3
Spring Security
  ↓
JWT
  ↓
Roles
  ↓
Authorization

PHASE 4
User Service
  ↓
Product Service
  ↓
Cart Service
  ↓
Order Service
  ↓
API Gateway
  ↓
Service Communication

PHASE 5
Observability
  ↓
Testing
  ↓
Migration
  ↓
Docker
  ↓
Cloud

Guiding Principle

Build the simplest working version first.

Then improve the same application layer by layer.

The goal is not to use every Spring technology. The goal is to
understand why each technology is introduced and what problem it
solves.