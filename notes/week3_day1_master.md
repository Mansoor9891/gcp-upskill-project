# 🚀 WEEK 3 — DAY 1 (MASTER NOTES)
## Event-Driven Architecture — Pub/Sub Integration

---

# 🎯 WHAT YOU DID TODAY (BIG PICTURE)

You transformed your system from:

CRUD API → EVENT-DRIVEN SYSTEM

Meaning:

Instead of just saving data,
your system now ALSO emits events when something happens.

---

# 🧠 CORE IDEA YOU LEARNED

👉 DATA ≠ EVENT

- Database = current state
- Event = something that happened

Example:

User exists in DB ❌ (no event)
User CREATED ✅ (event)

---

# 🧠 ARCHITECTURE YOU BUILT

Client
  ↓
Spring Boot API
  ↓
DB (Postgres)
  ↓
Pub/Sub Topic
  ↓
Subscription
  ↓
(you manually pulled)

---

# 🧠 KEY CONCEPTS

## Sync vs Async

SYNC:
Client → API → DB → Response

ASYNC:
Client → API → Event → Processing later

👉 Async = scalable systems

---

## Pub/Sub Components

Producer → Topic → Subscription → Subscriber

YOU BUILT:
- Producer = Spring Boot app
- Topic = user-events
- Subscription = user-events-sub

---

# 🛠️ BLOCK 2 — INFRA SETUP

## Commands & Meaning

### Set project
gcloud config set project gcp-upskill-9891

👉 selects GCP project

---

### Enable Pub/Sub
gcloud services enable pubsub.googleapis.com

👉 activates service

---

### Create topic
gcloud pubsub topics create user-events

👉 creates message channel

---

### Create subscription
gcloud pubsub subscriptions create user-events-sub --topic=user-events

👉 connects receiver to topic

---

# 🧪 BLOCK 3 — TESTING PUB/SUB

### Publish message
gcloud pubsub topics publish user-events --message="hello-week3"

👉 sends message to topic

---

### Pull message
gcloud pubsub subscriptions pull user-events-sub --limit=1 --auto-ack

👉 reads message from subscription

IMPORTANT:
- Topic does NOT store messages for you
- Subscription does

---

# ⚙️ BLOCK 4 — APP INTEGRATION

## What you added

1. Pub/Sub dependency
2. Event class
3. Publisher service
4. Service integration

---

## Flow implemented

POST /users
→ save user
→ create event
→ publish event

---

## Code logic (critical)

UserService:

1. save user
2. build UserCreatedEvent
3. call publisher
4. publisher sends to Pub/Sub

---

# 🔐 AUTH (CRITICAL LEARNING)

Problem:
App could not talk to Pub/Sub

Fix:
gcloud auth application-default login

👉 This creates ADC (Application Default Credentials)

---

# 🧠 ENV VARIABLES LEARNING

You learned:

CMD:
set VAR=value

PowerShell:
$env:VAR="value"

Mistake:
Used CMD syntax in PowerShell → failed

---

# 🧠 DATABASE LEARNING

Problem:
Cloud SQL dependency broke local run

Fix:
Remove:
postgres-socket-factory

👉 Local testing = use normal JDBC

---

# 🧠 KEY DEBUGGING INSIGHT

Error:
"url must start with jdbc"

Root cause:
Env variable not set properly

Fix:
Set env vars in SAME terminal

---

# 🧠 IMPORTANT REALIZATION

👉 Pub/Sub does NOT replay DB

Only events you publish exist

---

# 🧪 FINAL PROOF

You did:

POST /users

Then:

gcloud pubsub subscriptions pull user-events-sub

And saw:

{"userId":4,"name":"Ali","email":"ali@example.com"}

---

# 🎯 WHAT THIS MEANS

You successfully built:

👉 EVENT PRODUCER SERVICE

---

# 💣 FINAL STATE OF YOUR SYSTEM

Local App
→ saves to DB
→ publishes event
→ Pub/Sub receives
→ subscription stores
→ you can consume

---

# 🚀 WHAT YOU BECAME TODAY

Before:
CRUD developer

After:
Event-driven backend engineer

---

# 📅 NEXT (DAY 2)

You will build:

Subscriber service

That will:
- automatically listen
- process events
- act on them

---

# 🧠 FINAL TAKEAWAY

👉 Infrastructure + Code + Events = Real backend system

👉 This is how microservices communicate

