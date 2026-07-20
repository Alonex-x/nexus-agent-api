# Nexus Agent Management API

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue)

API REST que actua como cerebro de un ecosistema de automatizacion: los agentes de software (scrapers, analizadores de red, monitores) se registran, reciben misiones y reportan resultados y eventos a traves de estos endpoints. Un panel de control (frontend) consulta el estado de agentes y misiones en tiempo real.

Proyecto backend del "Centro de Mando" / Nexus Terminal.

## Requisitos

- Java 17
- Maven 3.9+
- PostgreSQL 15 (o Docker, ver mas abajo)

## Instalacion

git clone <url-del-repo>
cd nexus

Levanta PostgreSQL con Docker (opcional pero recomendado):

docker compose up -d

O configura tu propia instancia editando `src/main/resources/application.yml` (`spring.datasource.*`).

Ejecuta la aplicacion:

mvn spring-boot:run

La API queda disponible en `http://localhost:8080`.

## Autenticacion

Todos los endpoints, salvo el registro de agentes y la documentacion, requieren el header:

X-Agent-Key: <api-key-del-agente>

El API Key se entrega una unica vez al registrar el agente y no se puede recuperar despues (solo se persiste su hash SHA-256).

## Uso — ejemplos con curl

### Registrar un agente

curl -X POST http://localhost:8080/api/v1/agents/register \
  -H "Content-Type: application/json" \
  -d '{
        "name": "scraper-v1",
        "version": "1.0.0",
        "capabilities": ["scrape", "parse"]
      }'

### Enviar heartbeat

curl -X POST http://localhost:8080/api/v1/agents/heartbeat \
  -H "X-Agent-Key: <api-key>"

### Consultar estado de agentes

curl http://localhost:8080/api/v1/agents/status \
  -H "X-Agent-Key: <api-key>"

### Crear una mision

curl -X POST http://localhost:8080/api/v1/missions \
  -H "X-Agent-Key: <api-key>" \
  -H "Content-Type: application/json" \
  -d '{
        "agentName": "scraper-v1",
        "action": "scrape_url",
        "params": {"url": "https://example.com"}
      }'

### Consultar (y tomar) misiones pendientes

curl "http://localhost:8080/api/v1/missions/pending?agent=scraper-v1" \
  -H "X-Agent-Key: <api-key>"

### Reportar el resultado de una mision

curl -X POST http://localhost:8080/api/v1/missions/<mission-id>/report \
  -H "X-Agent-Key: <api-key>" \
  -H "Content-Type: application/json" \
  -d '{
        "status": "COMPLETED",
        "result": {"itemsScraped": 42}
      }'

### Enviar un evento

curl -X POST http://localhost:8080/api/v1/events \
  -H "X-Agent-Key: <api-key>" \
  -H "Content-Type: application/json" \
  -d '{
        "agent": "scraper-v1",
        "type": "log",
        "data": {"message": "scraping iniciado"}
      }'

### Consultar eventos recientes

curl "http://localhost:8080/api/v1/events/recent?limit=20"

## Documentacion interactiva

Con la aplicacion corriendo, la documentacion Swagger UI esta disponible en:

http://localhost:8080/swagger-ui.html

## Tests

mvn test

Incluye tests unitarios (JUnit 5 + Mockito) para la capa de servicios y tests de integracion (`@SpringBootTest` + H2) que ejercitan los flujos completos de cada recurso.

## Notas

Proyecto educativo desarrollado como parte de un portafolio profesional.
