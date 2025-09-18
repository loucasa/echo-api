# Spring Boot API Playground

This project serves as a playground for experimenting with Spring Boot API development. It provides a basic API structure that can be used as a foundation for testing and learning Spring Boot concepts.

## Overview

The project includes:
- Basic REST endpoints (`/`, `/actuator/health`, `/answer`)
- Entity management with JPA
- Error handling
- Integration tests

## Purpose

This API was created as a base playground for experimenting with Spring Boot features and concepts. It's designed to be simple enough to understand quickly, yet structured in a way that allows for easy expansion and experimentation.

## Getting Started

### Prerequisites
- Java JDK
- Maven

### Running the Application
```bash
mvn spring-boot:run
```

### Running Tests
```bash
mvn test
```

## API Endpoints

- `GET /` - Index endpoint, returns "ok"
- `GET /answer/{id}` - Get a specific answer by ID
- `GET /answers` - List all answers
- `POST /answer` - Create a new answer

Feel free to use this as a starting point for your own Spring Boot experiments!