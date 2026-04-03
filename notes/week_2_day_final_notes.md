# 📘 Week 2 — Day (Cloud SQL + Cloud Run + CRUD) — FULL NOTES

---

# 🎯 Goal

Take a Spring Boot app from:

Local → Docker → GCP → Public API → Connected to Cloud DB

---

# 🧱 Architecture Built

Client (Postman / Browser)
↓
Cloud Run (Spring Boot container)
↓
Cloud SQL (PostgreSQL managed DB)

---

# 🧱 Step-by-Step What We Did (FULL FLOW)

## 1. Create Cloud SQL Instance

* Go to GCP → Cloud SQL
* Click **Create Instance**
* Choose **PostgreSQL**
* Instance ID: `cloud-backend-lab-db`
* Region: `europe-west3`
* Set password for `postgres`
* Click Create

---

## 2. Create Database inside Instance

* Open instance
* Go to **Databases**
* Click **Create database**
* Name: `app_db`

---

## 3. Run Postgres locally (for dev)

```bash
docker run --name postgres-db \
-e POSTGRES_DB=app_db \
-e POSTGRES_USER=postgres \
-e POSTGRES_PASSWORD=postgres \
-p 5432:5432 \
-d postgres:15
```

---

## 4. Configure Spring Boot (env-based)

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
```

---

## 5. Add Cloud SQL dependency (Socket Factory)

Required for Cloud SQL connection.

---

## 6. Build JAR

```bash
mvn clean package -DskipTests
```

---

## 7. Build Docker Image

```bash
docker build -t cloud-backend-lab .
```

---

## 8. Tag Image for Artifact Registry

```bash
docker tag cloud-backend-lab europe-west3-docker.pkg.dev/gcp-upskill-9891/cloud-backend-lab-repo/cloud-backend-lab:v1
```

---

## 9. Push Image

```bash
docker push europe-west3-docker.pkg.dev/gcp-upskill-9891/cloud-backend-lab-repo/cloud-backend-lab:v1
```

---

## 10. Deploy to Cloud Run

* Go to Cloud Run
* Click **Deploy container**
* Select image from Artifact Registry

---

## 11. Set Environment Variables

```
DB_URL=jdbc:postgresql:///app_db?cloudSqlInstance=gcp-upskill-9891:europe-west3:cloud-backend-lab-db&socketFactory=com.google.cloud.sql.postgres.SocketFactory
DB_USERNAME=postgres
DB_PASSWORD=your-password
```

---

## 12. Attach Cloud SQL

* Go to **Containers tab**
* Scroll → **Cloud SQL connections**
* Add:

```
cloud-backend-lab-db
```

---

## 13. Deploy Service

* Click Deploy
* Wait for revision ready

---

## 14. Test API

* GET /users
* POST /users
* PUT /users/{id}
* DELETE /users/{id}

---

# 🧠 Core Concepts

## 1. Instance vs Database

* **Instance (cloud-backend-lab-db)** → PostgreSQL server
* **Database (app_db)** → actual DB inside instance

👉 Same DB name ≠ same DB

Local and Cloud DB are completely different.

---

## 2. Environment Variables

Instead of hardcoding:

```properties
spring.datasource.url=...
```

We used:

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
```

👉 Why?

* separation of config
* secure
* environment-specific

---

## 3. Cloud SQL Connection (IMPORTANT)

Normal JDBC:

```text
jdbc:postgresql://localhost:5432/app_db
```

Cloud SQL JDBC:

```text
jdbc:postgresql:///app_db?cloudSqlInstance=PROJECT:REGION:INSTANCE&socketFactory=com.google.cloud.sql.postgres.SocketFactory
```

### Why weird?

* No host
* Google manages connection internally
* uses **Socket Factory** instead of TCP

---

## 4. Why Socket Factory Dependency?

Because:

👉 Without it, app cannot connect to Cloud SQL

It enables:

App → Google internal network → Cloud SQL

---

## 5. Docker Concepts

### Dockerfile

```dockerfile
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY target/cloud-backend-lab-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
```

### Meaning

* FROM → base image
* WORKDIR → working directory inside container
* COPY → copy + rename jar
* EXPOSE → documentation (not enforced)
* ENTRYPOINT → what runs when container starts

---

## 6. Docker Commands

### Build

```bash
mvn clean package -DskipTests
docker build -t cloud-backend-lab .
```

* `-t` = tag (name of image)
* `.` = build context (where Dockerfile is)

---

### Tag for Artifact Registry

```bash
docker tag cloud-backend-lab europe-west3-docker.pkg.dev/gcp-upskill-9891/cloud-backend-lab-repo/cloud-backend-lab:v1
```

👉 adds full remote path

---

### Push

```bash
docker push europe-west3-docker.pkg.dev/gcp-upskill-9891/cloud-backend-lab-repo/cloud-backend-lab:v1
```

👉 uploads image to GCP

---

## 7. Artifact Registry Concepts

* Stores Docker images
* Required for Cloud Run deployment
* Image name must include:

```text
region-docker.pkg.dev/project/repo/image:tag
```

---

## 8. Cloud Run Concepts

### What it does

* Runs containers
* serverless
* auto scaling

---

### Important rules

#### PORT handling

Cloud Run sets:

```text
PORT=8080
```

Your app must use:

```properties
server.port=${PORT:8080}
```

👉 NEVER hardcode port

---

### Deployment flow

1. Select image
2. Add env vars
3. Attach Cloud SQL
4. Deploy

---

## 9. CRUD Endpoints

| Operation | Method | Endpoint    |
| --------- | ------ | ----------- |
| Create    | POST   | /users      |
| Read      | GET    | /users      |
| Update    | PUT    | /users/{id} |
| Delete    | DELETE | /users/{id} |

---

## 10. Postman Testing

### POST

```json
{
  "name": "Mansoor",
  "email": "mansoor@example.com"
}
```

---

### GET

```text
/users
```

---

### PUT

```json
{
  "name": "Updated",
  "email": "updated@example.com"
}
```

---

### DELETE

```text
/users/1
```

---

# ⚠️ Mistakes & Confusions (Important Learnings)

## 1. Local DB vs Cloud DB

❌ Thought same DB
✅ Actually different systems

---

## 2. PORT confusion

❌ Tried to set manually
✅ Cloud Run sets automatically

---

## 3. EXPOSE misunderstanding

❌ Thought it controls runtime
✅ It is just documentation

---

## 4. Docker push confusion

❌ Tried pushing local tag
✅ Must push full registry path

---

## 5. Region confusion

Learned where to find region in:

* Cloud SQL
* Artifact Registry

---

## 6. Empty response confusion

❌ Thought error
✅ It meant DB is empty (good sign)

---

# 🧠 Key Learnings

* Cloud = different environment
* Always externalize config
* Docker image ≠ running container
* Tag ≠ push
* Instance ≠ database
* Cloud Run enforces PORT

---

# 🚀 Final Outcome

You built:

Public API → Cloud Run → Cloud SQL

Fully working CRUD system.

---

# 🎯 Status

Week 2: ✅ COMPLETE

---

# 🔜 Next (Week 3)

* Pub/Sub
* event-driven architecture
* async processing

---

# 🧠 One-Line Summary

You can now build, containerize, deploy, and expose a backend API connected to a managed database on GCP.

---
