# 📘 Week 2 — Day 3 (Complete Notes)

## 🎯 Goal
Build a Spring Boot backend connected to PostgreSQL running in Docker and implement full CRUD with real persistence.

---

## 🧱 What We Built

```text
Client (Browser / Postman)
        ↓
Controller (REST API layer)
        ↓
Repository (Spring Data JPA)
        ↓
Hibernate (ORM implementation)
        ↓
PostgreSQL (Docker container)
```

This was the first day in Week 2 where the API stopped being stateless and started persisting real data.

---

## ⚙️ Project Structure

We created the following package structure under the base package:

```text
com.nextgen.cloudbackendlab
├── controller
├── entity
├── repository
```

### Why this matters
- `controller` handles HTTP requests and responses
- `entity` maps Java classes to database tables
- `repository` provides DB access through Spring Data JPA

Because all of these packages are inside `com.nextgen.cloudbackendlab`, Spring Boot can scan and register them automatically.

---

## 🧩 Step 1 — Added JPA + PostgreSQL dependencies

In `pom.xml`, the important DB-related dependencies were:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

### Concepts learned
#### `spring-boot-starter-data-jpa`
This brings in:
- Spring Data JPA
- Hibernate
- JPA-related support

#### PostgreSQL dependency
This is the JDBC driver that lets Java talk to PostgreSQL.

#### Why `scope=runtime`
We do not directly instantiate PostgreSQL classes in our code.
Spring/Hibernate uses the driver when the app runs, so compile-time usage is not needed.

---

## 🧩 Step 2 — Created the Entity

We created `User.java` inside the `entity` package.

### Final structure
```java
package com.nextgen.cloudbackendlab.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;

    public User() {
    }

    public User(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
```

### Concepts learned
#### `@Entity`
Marks this class as a JPA entity. Hibernate maps it to a table.

#### `@Table(name = "users")`
Explicitly sets the DB table name to `users`.

#### `@Id`
Marks the primary key.

#### `@GeneratedValue(strategy = GenerationType.IDENTITY)`
Tells the database to generate the ID automatically using its own identity/auto-increment mechanism.

#### Why getters and setters were necessary
Without getters/setters:
- JSON request mapping becomes unreliable
- Hibernate persistence can break
- update operations become harder

---

## 🧩 Step 3 — Created the Repository

We created `UserRepository.java` inside the `repository` package.

```java
package com.nextgen.cloudbackendlab.repository;

import com.nextgen.cloudbackendlab.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
```

### Concepts learned
#### `JpaRepository<User, Long>`
- `User` = entity class
- `Long` = type of the primary key

Because `id` in the entity is `Long`, repository also uses `Long`.

#### Built-in methods we got automatically
- `save()`
- `findAll()`
- `findById()`
- `deleteById()`

This means basic CRUD does not require writing SQL manually.

---

## 🧩 Step 4 — Created the Controller

We created the controller and gradually added endpoints.

### Final endpoints built
- `POST /users`
- `GET /users`
- `GET /users/{id}`
- `PUT /users/{id}`
- `DELETE /users/{id}`

### Example controller flow
At this stage, controller was talking directly to repository.

That is fine for learning, but later we noted the better structure should be:

```text
Controller → Service → Repository
```

### Why controller-first worked for Day 3
It let us focus on:
- request handling
- DB persistence
- repository usage
- CRUD verification

without adding architecture complexity too early.

---

## 🧩 Step 5 — Configured Spring Boot for PostgreSQL

In `src/main/resources/application.properties`, we had:

```properties
spring.application.name=cloud-backend-lab
server.port=${PORT:8080}

spring.datasource.url=jdbc:postgresql://localhost:5432/app_db
spring.datasource.username=postgres
spring.datasource.password=postgres

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

### Concepts learned

#### `spring.application.name`
Sets app name.

#### `server.port=${PORT:8080}`
Use environment variable `PORT` if it exists, otherwise use `8080`.

This is useful because:
- locally: app runs on `8080`
- later in Cloud Run: app can use injected `PORT`

#### `spring.datasource.url`
Tells Spring Boot where the DB is.

Breakdown:
- `jdbc:postgresql://`
- host = `localhost`
- port = `5432`
- DB name = `app_db`

#### `spring.datasource.username/password`
Credentials for DB connection.

#### `spring.jpa.hibernate.ddl-auto=update`
Hibernate checks entity structure and updates schema automatically.

This helped in local development because:
- tables got created automatically
- columns could be added automatically

#### Why `ddl-auto=update` is bad for production
It can silently change schema without explicit control.

Example:
- entity field changes from `name` to `fullName`
- Hibernate may create a new column instead of renaming old one
- old data stays in old column
- schema becomes messy

In production, teams use migration tools instead.

#### What is used in production instead
Usually:
- Flyway
- Liquibase

Those tools apply explicit, versioned SQL migrations.

#### `spring.jpa.show-sql=true`
Shows generated SQL in logs.

#### `spring.jpa.properties.hibernate.format_sql=true`
Makes SQL easier to read.

---

## 🐳 Step 6 — Started PostgreSQL in Docker

We ran this command:

```bash
docker run --name postgres-db -e POSTGRES_DB=app_db -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -p 5432:5432 -d postgres:15
```

### Full breakdown of command

#### `docker run`
Creates and starts a container from an image.

Mental model:
- image = template
- container = running instance

#### `--name postgres-db`
This is the name of the running container.

- image = `postgres:15`
- container name = `postgres-db`

#### `-e`
Sets environment variables inside container.

Used values:
- `POSTGRES_DB=app_db`
- `POSTGRES_USER=postgres`
- `POSTGRES_PASSWORD=postgres`

These are recognized by the PostgreSQL Docker image and used during initialization.

#### `-p 5432:5432`
Port mapping in format:

```text
HOST_PORT:CONTAINER_PORT
```

So:
- host machine port = `5432`
- container port = `5432`

That means Spring Boot can connect to:

```text
localhost:5432
```

and Docker forwards that to PostgreSQL running inside the container.

#### Important port concept learned
If we used:

```bash
-p 6000:5432
```

then Spring Boot would have to use:

```properties
jdbc:postgresql://localhost:6000/app_db
```

because host port is what the app connects to.

#### `-d`
Detached mode = run in background.

#### `postgres:15`
Image name + version tag.

This image already contains:
- PostgreSQL installed
- startup scripts
- default entrypoint that launches database server

That is why the container “knows” it is a DB container.

---

## 🧠 Docker concepts learned

### Image vs Container
- Image = blueprint/template
- Container = running instance

### Why `docker run` matters
`docker run` effectively does:
1. pull image if needed
2. create container
3. start container

### Why no directory mattered
The command did not depend on local files or Dockerfile, so it could be run from any directory.

### What happens if container stops
If container stops:
- DB process stops
- app cannot connect

If container is removed and no volume is used:
- data is lost

### Proper next-level concept
To persist DB data even after container removal, a Docker volume should be used.

---

## ✅ Step 7 — Verified PostgreSQL container

We ran:

```bash
docker ps
```

And confirmed container was up.

Important output looked like:

```text
0.0.0.0:5432->5432/tcp
```

That proved host-to-container port mapping was working.

---

## ▶️ Step 8 — Ran Spring Boot app

Instead of overcomplicating Maven, we ran the app directly from IntelliJ using:

- `CloudBackendLabApplication`
- Run button

### Important build concept learned
IntelliJ automatically compiles before running.

So we did **not** need to manually run:
- `mvn package`
- `mvn clean install`

for local dev.

### Difference learned
- IntelliJ Run = compile + run quickly
- Maven package = build JAR
- clean install = full Maven lifecycle + install to local repo

### App port learned
The app ran on:

```text
8080
```

because:

```properties
server.port=${PORT:8080}
```

and no `PORT` env variable was set locally.

---

## 🌐 Step 9 — Tested API

### First test
Opened browser:

```text
http://localhost:8080/users
```

Result:

```json
[]
```

This proved:
- app was running
- endpoint was mapped
- DB connection was working
- table existed
- no data yet

---

## 🧪 Step 10 — Used Postman for CRUD

After trying and rejecting several tool paths, we settled on Postman.

### POST test
Request:
- Method: POST
- URL: `http://localhost:8080/users`

Body:
```json
{
  "name": "Mansoor",
  "email": "test@mail.com"
}
```

Response returned created user with generated ID.

### GET all
Opened:

```text
http://localhost:8080/users
```

and saw the saved user.

### GET by ID
Opened:

```text
http://localhost:8080/users/1
```

and confirmed fetch-by-id worked.

### PUT
Updated user using Postman and verified changed values were returned.

### DELETE
Deleted user via endpoint and confirmed list became empty again.

---

## 🔁 CRUD operations completed

### 1. Create
- `POST /users`

### 2. Read all
- `GET /users`

### 3. Read by id
- `GET /users/{id}`

### 4. Update
- `PUT /users/{id}`

### 5. Delete
- `DELETE /users/{id}`

This completed the full local CRUD cycle.

---

## 🧠 Core backend concepts learned/refreshed

### JPA Flow
```text
Controller → Repository → Hibernate → JDBC Driver → PostgreSQL
```

### Hibernate
Hibernate is the ORM implementation used under the hood by Spring Data JPA.

### JDBC Driver
PostgreSQL JDBC driver is the bridge between Java and PostgreSQL.

### Port Mapping
App does not connect directly to the container’s internal network.
It connects to host port, which Docker forwards into the container.

### Entity vs Table
- entity = Java representation
- table = DB representation

### Repository
Repository abstracts DB operations so basic CRUD does not require manual SQL.

---

## ❌ Mistakes / Confusions / Fixes

### 1. IntelliJ dependency sync confusion
Dependencies were not appearing properly until project was reloaded from disk / Maven reloaded.

### 2. Confusion between SQL language and Oracle SQL / PostgreSQL
A lot of time got lost because “SQL” was being used in two meanings:
- SQL language
- Oracle DB / Oracle SQL ecosystem

### 3. Confusion around RDBMS vs ORDBMS
Important clarified takeaway:
- PostgreSQL can be used like a normal relational DB
- OR features are extra power, not mandatory

### 4. Wrong app port assumption
Tried `5050`, but app was actually on `8080`.

### 5. Trailing slash issue
`/users/` did not behave the same as `/users` in one test.

### 6. Tool confusion for API testing
Tried browser, IntelliJ HTTP, curl, extensions, then settled on Postman.

### 7. Build confusion
Thought manual build was required after adding classes.
Clarified that IntelliJ Run compiles automatically.

---

## ✅ Final outcome of Day 3

By the end of the day, we had:

- Spring Boot app running locally
- PostgreSQL running in Docker
- Spring Boot successfully connected to PostgreSQL
- `users` table auto-created via Hibernate
- Full CRUD API working
- API tested successfully through browser/Postman

---

## 📌 What was completed from Week 2 through Day 3

Completed:
- design simple API
- add REST endpoints
- setup DB schema through JPA/Hibernate
- local Postgres in Docker
- connect Spring Boot to DB
- store and fetch data

Still remaining after Day 3:
- service layer
- env-based DB config
- Cloud SQL
- Cloud Run redeploy with DB
- live end-to-end public CRUD

---

## 🔥 One-line summary of Day 3

Week 2 Day 3 was the day the project became a real backend system: Spring Boot + JPA + PostgreSQL + full CRUD running locally with Docker.
