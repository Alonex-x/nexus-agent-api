# Nexus Agent Management API

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue)

REST API that acts as the brain of an automation ecosystem: software agents (scrapers, network analyzers, monitors) register, receive missions, and report results and events through these endpoints. A control panel (frontend) queries agent and mission status in real time.

Backend project of the "Command Center" / Nexus Terminal.

## Requirements

- Java 17
- Maven 3.9+
- PostgreSQL 15 (or Docker, see below)

## Installation

git clone <repo-url>
cd nexus

Start PostgreSQL with Docker (optional but recommended):

docker compose up -d

Or configure your own instance by editing src/main/resources/application.yml (spring.datasource.*).

Run the application:

mvn spring-boot:run

The API is available at http://localhost:8080.

## Authentication

All endpoints except agent registration and documentation require the header:

X-Agent-Key: <agent-api-key>

The API Key is delivered once when the agent registers and cannot be recovered later (only its SHA-256 hash is persisted).

## Notes

Educational project developed as part of a professional portfolio.
