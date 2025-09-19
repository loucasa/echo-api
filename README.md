# Spring Boot API Playground

This project serves as a playground for experimenting with Spring Boot API development. It provides a basic API structure that can be used as a foundation for testing and learning Spring Boot concepts.

## Overview

The project includes:
- Basic REST endpoints (`/`, `/actuator/health`, `/echos`)
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
- `GET /echos/{id}` - Get a specific echos by ID
- `GET /echoss` - List all echos
- `POST /echos` - Create a new echos

## Containerization

### Build Docker Image
```bash
docker build -t echo-api:latest .
```

### Run with Docker
```bash
docker run -p 8080:8080 echo-api:latest
```

### Kubernetes Deployment
Apply the Kubernetes manifests in your cluster:
```bash
kubectl apply -f k8s-deployment.yaml
```
This will create a Deployment and a Service (ClusterIP) for the API. You can port-forward to access it:
```bash
kubectl port-forward svc/echo-api 8080:80
```

#### If using Minikube and you built the image locally, load it into your Minikube cluster before applying the manifest:
```bash
minikube start
minikube image load echo-api:latest
```
Then apply the manifest and port-forward as above. You can then manage the cluster using `minikube dashboard` if preferred to command line.