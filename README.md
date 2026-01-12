# Taskio Backend

A Spring Boot REST API backend for the Taskio task management application.

## Features

- RESTful API for task management (CRUD operations)
- H2 in-memory database
- Bean Validation for input validation
- CORS enabled for frontend communication
- Exception handling with meaningful error messages

## Technologies

- Java 17
- Spring Boot 3.2.1
- Spring Data JPA
- H2 Database
- Lombok
- Maven

## API Endpoints

- `GET /api/tasks` - Retrieve all tasks
- `GET /api/tasks/{id}` - Retrieve a task by ID
- `POST /api/tasks` - Create a new task
- `PUT /api/tasks/{id}` - Update a task
- `DELETE /api/tasks/{id}` - Delete a task

## Task Entity

```json
{
  "id": 1,
  "title": "Task title",
  "description": "Task description",
  "status": "TODO",
  "dueDate": "2026-01-15"
}
```

**Status values:** `TODO`, `IN_PROGRESS`, `DONE`

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+

### Installation & Running

1. Navigate to the backend directory:
   ```bash
   cd taskio-backend
   ```

2. Build and run the application:
   ```bash
   mvn spring-boot:run
   ```

   Or build the JAR and run it:
   ```bash
   mvn clean package
   java -jar target/taskio-backend-1.0.0.jar
   ```

3. The API will be available at `http://localhost:8080`

### H2 Console

Access the H2 database console at: `http://localhost:8080/h2-console`

- JDBC URL: `jdbc:h2:mem:taskiodb`
- Username: `sa`
- Password: (leave blank)

## Configuration

Configuration can be modified in `src/main/resources/application.properties`:

- Server port
- Database settings
- CORS configuration
- JPA settings

## Testing the API

### Using curl:

```bash
# Get all tasks
curl http://localhost:8080/api/tasks

# Create a task
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "My First Task",
    "description": "Task description",
    "status": "TODO",
    "dueDate": "2026-01-20"
  }'

# Update a task
curl -X PUT http://localhost:8080/api/tasks/1 \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Updated Task",
    "description": "Updated description",
    "status": "IN_PROGRESS",
    "dueDate": "2026-01-25"
  }'

# Delete a task
curl -X DELETE http://localhost:8080/api/tasks/1
```
