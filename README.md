# forum-mse-2026_256307

A modern web forum developed as a university project using **Spring Boot** and **React**.

The application allows registered users to create discussion topics, participate in conversations through replies, and manage content based on their assigned role.

---

# Technologies

## Backend

- Java 21
- Spring Boot
- Spring Security
- JWT Authentication
- Spring Data JPA
- PostgreSQL
- Flyway
- MapStruct
- OpenAPI Generator
- Maven

## Frontend

- React
- React Router
- Vite
- Fetch API

---

# Features

## Authentication

- User registration
- User login
- JWT authentication

## Topics

- Create topic
- Edit own topic
- Moderators and administrators can edit every topic
- Unique topic title
- View counter
- Creation and modification timestamps

## Replies

- Create reply
- Edit own reply
- Moderators and administrators can edit every reply
- Creation and modification timestamps

## Roles

### Administrator

- Full system access
- Can promote users to moderators
- Can manage all topics and replies

### Moderator

- Can edit all topics
- Can edit all replies

### User

- Can create topics
- Can create replies
- Can edit only their own content

---

# Running the project

## Backend

Compile the project

```bash
mvn clean compile
```

Start Spring Boot

```bash
mvn spring-boot:run
```

Backend runs on

```
http://localhost:9000
```

---

## Frontend

Navigate to the frontend folder

```bash
cd frontend
```

Install dependencies

```bash
npm install
```

Start React

```bash
npm run dev
```

Frontend runs on

```
http://localhost:5173
```

---

# User Stories

- Create Topics
- Create Replies
- View Topics
- User Registration
- Role Based Permissions

---

# Security

- JWT Authentication
- Password hashing using BCrypt
- Role-based authorization
- Stateless authentication

---

**Kaloyan Kirilov**

Faculty Number: 256307

Software Engineering

Ruse University "Angel Kanchev"
