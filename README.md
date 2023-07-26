# University Management System

University Management System is a simple RESTful API project developed in Spring Boot for managing information related
to a university. It allows CRUD operations on university-related data like courses, students, and lecturers.

## Features

- CRUD operations for the following entities:
    - Students
    - Lecturers
    - Courses
- RESTful API endpoints
- Database integration using Spring Data JPA
- Unit and Integration Tests using JUnit
- In-memory database for testing using H2

## Technologies Used

- Java
- Spring Boot
- Spring Data JPA
- H2 Database (for testing)
- PostgreSQL Database (for production)
- JUnit
- Docker
- Gradle

## Getting Started

1. Clone the repository to your local machine.
2. Make sure you have Docker installed.
3. Run `docker-compose up` to start the PostgreSQL database in a Docker container.
4. Update the `application.yml` file in the `src/main/resources` directory with the correct database information.
5. Run the project using your IDE.

## API Documentation

The API documentation is available at `http://localhost:8080/swagger-ui/index.html#/`.