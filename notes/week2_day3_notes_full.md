# 📘 Week 2 --- Day 3 (Full Notes)

## 🎯 Goal

Build a Spring Boot backend connected to PostgreSQL and perform full
CRUD operations.

------------------------------------------------------------------------

## 🧱 Architecture Built

Controller → Repository → JPA → PostgreSQL → Persistence

------------------------------------------------------------------------

## ⚙️ Project Structure

    com.nextgen.cloudbackendlab
    ├── controller
    ├── entity
    ├── repository

------------------------------------------------------------------------

## 🧩 Entity

``` java
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
}
```

------------------------------------------------------------------------

## 🧩 Repository

``` java
public interface UserRepository extends JpaRepository<User, Long> {}
```

------------------------------------------------------------------------

## 🧩 Controller Endpoints

-   POST /users
-   GET /users
-   GET /users/{id}
-   PUT /users/{id}
-   DELETE /users/{id}

------------------------------------------------------------------------

## 🐳 Docker PostgreSQL Setup

``` bash
docker run --name postgres-db -e POSTGRES_DB=app_db -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -p 5432:5432 -d postgres:15
```

### Concepts

-   `-e` → environment variables
-   `-p 5432:5432` → host:container port mapping
-   `-d` → detached mode
-   `postgres:15` → image + version

------------------------------------------------------------------------

## 🔌 Spring Boot Config

``` properties
spring.datasource.url=jdbc:postgresql://localhost:5432/app_db
spring.datasource.username=postgres
spring.datasource.password=postgres

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

------------------------------------------------------------------------

## 🧠 Key Concepts Learned

### JPA Flow

    Controller → Repository → Hibernate → DB

### ddl-auto=update

-   auto creates/updates tables
-   ❌ bad for production (no control)

### Port Mapping

    localhost:5432 → Docker → Postgres:5432

### Docker

-   Image = template
-   Container = running instance

------------------------------------------------------------------------

## ⚠️ Mistakes

-   Wrong port (5050 instead of 8080)
-   Trailing slash issue `/users/`
-   IntelliJ HTTP client confusion
-   Not understanding Docker ports initially

------------------------------------------------------------------------

## 🚀 Outcome

-   Built full CRUD API
-   Connected Spring Boot to PostgreSQL
-   Persisted real data
-   Understood Docker fundamentals
-   Executed real backend flow

------------------------------------------------------------------------

## 🔥 Status

Week 2 Day 3 COMPLETE
