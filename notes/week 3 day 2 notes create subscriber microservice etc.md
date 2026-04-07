📘 WEEK 3 — DAY 2 (COMPLETE NOTES)
🔥 OVERALL GOAL

Build a subscriber microservice that:

listens to Pub/Sub
receives events asynchronously
parses JSON → DTO
validates data
processes events
acknowledges messages
🧠 ARCHITECTURE (VERY IMPORTANT)
Producer (Spring Boot)
        ↓
Pub/Sub Topic (user-events)
        ↓
Subscription (user-events-sub)
        ↓
Subscriber Service
        ↓
DTO → Validation → Processing → ACK
🔹 BLOCK 5 — SUBSCRIBER MICROSERVICE
🎯 Goal

Create a new service that listens to Pub/Sub subscription

🧱 Setup
Created:
new Spring Boot project → subscriber-service
📦 Dependencies
spring-cloud-gcp-starter-pubsub
spring-boot-starter-web
⚙️ application.properties
spring.cloud.gcp.project-id=gcp-upskill-9891
subscriber.subscription=user-events-sub
server.port=8081
🧠 Core Concept
pubSubTemplate.subscribe(subscriptionName, this::handleMessage);
Meaning:

👉 “Register a callback method”

Pub/Sub will:

Receive message → call handleMessage(message)
🔥 Method Reference
this::handleMessage

Equivalent:

message -> handleMessage(message)
❗ WHY NOT THIS?
handleMessage   ❌

Because:

Java needs a function
not a method name
🧠 KEY CONCEPT — CALLBACK SYSTEM

You are NOT doing:

while(true) { ... }

Instead:

Pub/Sub calls YOU
🔁 MESSAGE FLOW
M1 → handleMessage(M1)
M2 → handleMessage(M2)
M3 → handleMessage(M3)

Each call is independent.

🧵 THREADING

Log:

[sub-subscriber1]

Means:

running in background thread
async processing
📥 MESSAGE OBJECT
BasicAcknowledgeablePubsubMessage message

Contains:

payload
messageId
ack/nack control
❗ WHY NOT String?

Because:

👉 You need:

message.ack()
message.nack()
🔑 ACK / NACK
message.ack();   // success
message.nack();  // failure
⚠️ IMPORTANT

Pub/Sub uses:

At-least-once delivery

So:

duplicates possible
system must be idempotent
🔹 BLOCK 6 — DTO PARSING
🎯 Goal

Convert JSON → Java object

❌ Before
String payload
✅ After
UserCreatedEvent event
📦 DTO
class UserCreatedEvent {
    Long userId;
    String name;
    String email;
}
🔁 Conversion
objectMapper.readValue(payload, UserCreatedEvent.class);
🧠 Why DTO?
type safety
clean code
easier validation
readable
🧠 Design Improvement
processUserCreatedEvent(event);

Separate method:

handleMessage → transport
process → business logic
🔹 BLOCK 7 — VALIDATION
🎯 Goal

Ensure incoming data is valid

❗ Why?

External systems can send:

null values
empty fields
invalid email
✅ DTO VALIDATION
@NotNull
@NotBlank
@Email
⚙️ Validation Flow
DTO → validate → if invalid → throw → catch → nack
🧠 Where rules live?

👉 DTO (correct design)

🧠 Where failure handled?

👉 Subscriber (try-catch)

🧠 Why throw?

Because:

Throw = stop processing immediately
🧠 Alternative (you suggested)
if (!isValid) {
    nack();
    return;
}

✔ valid approach
❗ but less scalable

🧠 Validator vs Manual
Manual	Validator
simple	scalable
messy for large objects	clean
repeated logic	reusable
🔹 SPRING CONCEPTS (VERY IMPORTANT)
@Service
@Service

Means:

👉 “Spring manages this class”

BEAN

Bean = object managed by Spring

DEPENDENCY INJECTION (DI)

Instead of:

new PubSubTemplate()

Spring gives:

PubSubTemplate
CONSTRUCTOR INJECTION (CI)
public PubSubSubscriber(PubSubTemplate pubSubTemplate)

Best practice because:

clean
testable
immutable
@PostConstruct
@PostConstruct

Runs:
👉 after bean creation

Used for:
👉 start subscriber

@PreDestroy
@PreDestroy

Runs:
👉 before shutdown

Used for:
👉 stop subscriber

❗ Without PreDestroy
threads may keep running
messy shutdown
🔹 JAVA CONCEPTS
Lambda
message -> handleMessage(message)
Method Reference
this::handleMessage

Cleaner version of lambda

Type Inference

Java infers:

message type from subscribe() method
Functional Interface

subscribe() expects:

Consumer<BasicAcknowledgeablePubsubMessage>
🔹 POM vs BOM (VERY IMPORTANT)
POM
pom.xml

Contains:

dependencies
plugins
config
BOM
<dependencyManagement>

Manages:
👉 versions

WHY BOM?

Without BOM:

version conflicts
manual control

With BOM:

auto alignment
compatibility
KEY IDEA
dependencies → WHAT
BOM → WHICH VERSION
🔹 SPRING BOOT STARTER

Example:

spring-boot-starter-web
Why use starter?
bundles dependencies
auto-config
easier
Why NOT webmvc directly?

Because:

manual setup
more work
🔹 GCP + PROJECT CONFIG
Project ID
spring.cloud.gcp.project-id=gcp-upskill-9891

Must match:

topic
subscription
gcloud config
gcloud config get-value project

Only needed for CLI, not app

🔹 PUBSUB CONSOLE
Where?

Console → Pub/Sub → Subscriptions

Important

ACKED messages:

❌ NOT visible
Why?

Pub/Sub is:

delivery system, not storage
🔹 COMMANDS USED
Run app
mvn spring-boot:run
Pull messages manually
gcloud pubsub subscriptions pull user-events-sub --limit=5 --auto-ack
Auth
gcloud auth application-default login
🔹 MISTAKES / CONFUSIONS (VERY IMPORTANT)
❌ mkdir instead of Spring project

👉 No src/pom created

❌ CI confusion

CI ≠ Continuous Integration
CI = Constructor Injection (context)

❌ method vs function
handleMessage ❌
this::handleMessage ✅
❌ message type confusion

Type comes from:
👉 subscribe() signature

❌ expecting Pub/Sub to store messages

It does NOT

❌ thinking you control loop

You don’t

Pub/Sub calls your method

🔥 FINAL FLOW (VERY IMPORTANT)
Receive message
   ↓
Extract payload
   ↓
Convert JSON → DTO
   ↓
Validate DTO
   ↓
Process event
   ↓
ACK
🚀 FINAL TAKEAWAY

You built:

async system
event-driven architecture
DTO processing
validation pipeline
proper Spring-based microservice

👉 This is real backend engineering level

✅ STATUS
Block 5 ✅
Block 6 ✅
Block 7 ✅

👉 Week 3 Day 2 COMPLETE