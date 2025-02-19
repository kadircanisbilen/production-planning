# Production Planning System

## 📌 Project Overview

This project is a **Production Planning System** that allows companies to manage their production schedules efficiently. It enables defining projects, models, and parts while handling monthly or weekly production plans. The system also ensures proper tracking and logging of changes.

## 🚀 Technologies Used

- **Java 17**
- **Spring Boot 3.3.x**
- **PostgreSQL** (Database)
- **Spring Data JPA**
- **MapStruct** (Entity-DTO Mapping)
- **Spring AOP** (Logging)
- **Lombok**
- **Docker & Docker Compose**
- **Swagger (SpringDoc OpenAPI)** (API Documentation)

## 🛠️ Setup and Installation

### 🔹 Running with Docker

1. Clone the repository:
   ```sh
   git clone https://github.com/kadircanisbilen/production-planning.git
   cd production-planning
   ```
2. Start the application with Docker Compose:
   ```sh
   docker-compose up --build
   ```
3. The API will be available at:
   ```
   http://localhost:8080
   ```
4. Swagger UI can be accessed at:
   ```
   http://localhost:8080/swagger-ui/index.html
   ```

### 🔹 Running Locally (Without Docker)

1. Ensure PostgreSQL is running and update `application.yml` with your DB credentials.
2. Build and run the project using:
   ```sh
   mvn clean install
   mvn spring-boot:run
   ```

## 📊 Database Structure

The system uses a relational database structure with the following tables:

- `projects` - Stores project details.
- `models` - Stores models under each project.
- `parts` - Stores parts used in models.
- `model_parts` - Defines the relationship between models and parts.
- `production_plans` - Stores production plans.
- `production_plan_details` - Stores model-specific production distribution.
- `operation_logs` - Logs all operations performed in the system.

## 🌐 API Endpoints

### **Projects API**

| Method | Endpoint                      | Description             |
| ------ | ----------------------------- | ----------------------- |
| GET    | `/api/projects`               | List all projects       |
| POST   | `/api/projects`               | Create a new project    |
| PUT    | `/api/projects/{id}/settings` | Update project settings |
| DELETE | `/api/projects/{id}`          | Soft delete a project   |

### **Models API**

| Method | Endpoint           | Description          |
| ------ | ------------------ | -------------------- |
| GET    | `/api/models`      | List all models      |
| GET    | `/api/models/{id}` | Get model details    |
| POST   | `/api/models`      | Create a new model   |
| PUT    | `/api/models/{id}` | Update model details |
| DELETE | `/api/models/{id}` | Soft delete a model  |

### **Production Plan API**

| Method | Endpoint                     | Description                      |
| ------ | ---------------------------- | -------------------------------- |
| GET    | `/api/production-plans`      | List all production plans        |
| POST   | `/api/production-plans`      | Create a production plan         |
| GET    | `/api/production-plans/{id}` | Get details of a production plan |

### **Logging API**

| Method | Endpoint    | Description                    |
| ------ | ----------- | ------------------------------ |
| GET    | `/api/logs` | Retrieve all logged operations |

## 📝 Key Features

- **Soft Delete Implementation**: Projects, models, and parts are soft-deleted instead of being permanently removed.
- **Logging with Spring AOP**: Every CRUD operation is logged and stored in `operation_logs`.
- **Flexible Planning**: Production can be planned **monthly** or **weekly**.
- **DTO-Entity Separation**: DTOs are used in controllers, while entities handle persistence logic.
- **Automatic Timestamps**: `createdAt` and `updatedAt` are managed automatically in `BaseEntity`.

## 🛠️ Future Improvements

- Implement **unit & integration tests** (to be added soon!)
- Add authentication & authorization
- Improve error handling with custom exception responses

## 💡 Contribution

Feel free to fork and submit pull requests. Before submitting, make sure to:

1. Run `mvn clean install` to verify builds.
2. Ensure proper API documentation with Swagger.
3. Follow the existing coding style.

