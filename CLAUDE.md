# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a distributed e-commerce system (online store) built with Java, implementing a microservices architecture. The project uses Serbian language for domain terminology. It consists of a REST API gateway (CentralniServer) that communicates with three backend subsystems via JMS messaging.

## Build Commands

### CentralniServer (Maven)
```bash
cd CentralniServer && mvn clean package
```

### Podsistem1, Podsistem2, Podsistem3, KlijentskaAplikacija (Ant)
```bash
cd <Subsystem> && ant clean build
```

### Database Setup
```bash
mysql -u root -p < podsistem1.sql
mysql -u root -p < podsistem2.sql
mysql -u root -p < podsistem3.sql
```

## Architecture

```
KlijentskaAplikacija (CLI/Swing Client)
         │
         │ HTTP (XML)
         ▼
CentralniServer (REST API Gateway - JAX-RS)
         │
         │ JMS (XML messages, request-reply pattern)
         ▼
┌────────────────┬────────────────┬────────────────┐
│  Podsistem1    │  Podsistem2    │  Podsistem3    │
│  (Users)       │  (Products)    │  (Orders)      │
│                │                │                │
│  MySQL DB      │  MySQL DB      │  MySQL DB      │
│  podsistem1    │  podsistem2    │  podsistem3    │
└────────────────┴────────────────┴────────────────┘
```

### Communication Flow
1. Client sends HTTP request with XML body to CentralniServer REST endpoint
2. CentralniServer creates JMS message with action type and XML payload
3. Target subsystem receives from its JMS queue, processes, and sends reply
4. CentralniServer returns response via HTTP (10-second JMS timeout)

### Key Patterns
- **Authorization**: Bearer token via `X-Korisnik-Id` HTTP header
- **JMS Correlation**: Uses `JMSCorrelationID` for request-reply matching
- **Error Response**: XML with `<greska>` root element
- **Persistence**: JPA 2.2 with EclipseLink, RESOURCE_LOCAL transactions

## Technology Stack

- Java 8
- GlassFish application server
- JAX-RS (Jakarta REST)
- JMS for inter-service messaging
- JPA/EclipseLink for persistence
- MySQL 5.7+
- DOM-based XML processing

## Subsystem Responsibilities

**Podsistem1** - User management: authentication, user CRUD, city management, balance operations

**Podsistem2** - Product catalog: categories, products (artikli), shopping cart (korpa), wishlist, pricing/discounts

**Podsistem3** - Order processing: order creation, payment transactions, cross-database validation (checks balances from Podsistem1, products from Podsistem2)

## Domain Terminology (Serbian)

- Korisnik = User
- Artikl = Product
- Korpa = Shopping Cart
- Narudzbina = Order
- Stavka = Order Line Item
- Transakcija = Transaction
- Kategorija = Category
- Grad = City
- Uloga = Role
- Popust = Discount
- Cena = Price
