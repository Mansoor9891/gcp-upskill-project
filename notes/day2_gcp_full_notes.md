# Day 2 — GCP Deployment Notes (Full Concepts, Commands, Mistakes, and Explanations)

## Scope of this document

This file covers **everything learned today** while taking the existing Dockerized Spring Boot app `cloud-backend-lab` and deploying it to **Google Cloud Run**.

It includes:

- every major concept learned
- every command used
- what each command does
- why each step exists
- mistakes and corrections
- the mental model behind Docker auth, Artifact Registry, tagging, pushing, and Cloud Run

---

# 1. Starting point at the beginning of Day 2

Day 1 was already complete before today.

That means the project already had:

- a Spring Boot app
- `Spring Web`
- a `HelloController`
- a working `/hello` endpoint
- Maven build working
- Dockerfile working
- Docker image built locally
- container running locally
- `server.port=${PORT:8080}` already added

So Day 2 was **not** about local Docker again.

Day 2 was about this flow:

```text
Local Docker image
    ↓
Google Artifact Registry
    ↓
Google Cloud Run
    ↓
Public HTTPS URL
```

---

# 2. Big-picture Day 2 architecture

## Final deployment flow

```text
Spring Boot app
    ↓
Maven builds JAR
    ↓
Docker builds image
    ↓
Docker image tagged with GCP registry path
    ↓
Docker pushes image to Artifact Registry
    ↓
Cloud Run pulls image from Artifact Registry
    ↓
Cloud Run starts container
    ↓
Public URL serves requests
```

## Main GCP services used today

### Cloud Run
Runs the container as a serverless service.

### Artifact Registry
Stores the Docker image in GCP.

### Cloud Build
Enabled because GCP deployment flow may rely on it and it is commonly required in Cloud Run workflows, even if the image was built locally today.

---

# 3. Core concepts learned today

## 3.1 `gcloud`

`gcloud` is the Google Cloud CLI.

It is the command-line tool used to interact with Google Cloud from your terminal.

Examples of what it can do:

- log in to GCP
- select project
- enable services
- create repositories
- deploy Cloud Run services

## 3.2 GCP project

A **project** is the logical container for your Google Cloud resources.

Important concept:

A GCP project is **global**.  
It is not permanently tied to one region.

That means:

- project = global container
- services inside it choose their own region

So it is valid to have:

- project: `gcp-upskill-9891`
- Artifact Registry in `europe-west3`
- Cloud Run in `europe-west3`

## 3.3 Region

A region is the physical location where a cloud resource runs.

Examples:

- `europe-west3`
- `us-central1`

For this project, we used:

```text
europe-west3
```

Important rule learned:

**Artifact Registry and Cloud Run should use the same region** for consistency and efficiency.

## 3.4 Enabling APIs / services in GCP

GCP is modular.  
Most services are not implicitly active. You explicitly enable them per project.

The command pattern:

```bash
gcloud services enable ...
```

means:

> turn on these services/APIs for the current GCP project

This is why enabling services was required before creating resources or deploying.

## 3.5 Artifact Registry

Artifact Registry is GCP’s package and artifact storage system.

It can store different formats such as:

- Docker images
- Maven packages
- npm packages
- Python packages

Today we used it as a **Docker image registry**.

Mental model:

```text
Artifact Registry = GCP’s Docker warehouse
Repository = a shelf/folder inside that warehouse
Image = actual container image stored there
Tag = version of that image
```

## 3.6 Repository format

When creating an Artifact Registry repository, you specify a format.

For today:

```text
docker
```

Other formats discussed conceptually:

- `maven`
- `npm`
- `python`

Important distinction learned:

- `.jar` is a file type
- `maven` is a repository format

## 3.7 Registry endpoint format

For Docker images in Artifact Registry, the full path format is:

```text
[region]-docker.pkg.dev/[project-id]/[repository]/[image]:[tag]
```

Our actual image path:

```text
europe-west3-docker.pkg.dev/gcp-upskill-9891/cloud-backend-lab-repo/cloud-backend-lab:v1
```

Breakdown:

- `europe-west3-docker.pkg.dev` = registry hostname
- `gcp-upskill-9891` = project
- `cloud-backend-lab-repo` = repository
- `cloud-backend-lab` = image name
- `v1` = tag

## 3.8 `docker.pkg.dev`

This is **Google’s registry domain** for Docker images in Artifact Registry.

Important clarification learned:

- `docker.pkg.dev` is not user-defined
- it is owned by Google
- all users use the same base domain
- uniqueness comes from region + project + repo + image + tag

Analogy:

```text
docker.pkg.dev = like gmail.com
full path = like your complete email address
```

## 3.9 Tagging Docker images

A Docker image can have multiple names/tags pointing to the same underlying image.

`docker tag` does **not** rebuild the image.

It simply adds another reference.

This was critical today because:

- local image name: `cloud-backend-lab`
- GCP push target required a full registry path

So we tagged the same local image with a GCP-compatible name.

## 3.10 Image name vs tag

Important distinction:

- image name = identity of the app
- tag = version of the app

Example:

```text
cloud-backend-lab:v1
cloud-backend-lab:v2
cloud-backend-lab:latest
```

We chose `v1` because it is better than `day2` for versioning.

## 3.11 Why version tags matter

Without an explicit tag, Docker uses `latest`.

Problems with only using `latest`:

- gets overwritten
- poor rollback visibility
- harder debugging
- less deployment control

Explicit tag like `v1` is better because:

- easier rollback
- clearer history
- more production-like practice

## 3.12 `docker build` vs `docker push`

Important correction learned:

### `docker build`
Builds the image **locally only**.

It does **not** upload anything anywhere.

### `docker push`
Uploads an image to a registry.

So the correct flow is:

```text
build → local
tag → still local
push → local + registry
```

## 3.13 Where Docker stores images locally

Docker images are not stored as simple files you manually browse like `.jar` files.

Docker stores them in Docker’s own internal storage (Docker Engine / Docker Desktop backend).

Practical rule learned:

Do **not** try to manage images through filesystem paths.  
Use Docker commands instead:

```bash
docker images
docker inspect
docker history
```

## 3.14 Docker global config vs Dockerfile

Very important distinction:

### Dockerfile
Defines **how to build** an image.

### Docker config (`config.json`)
Defines **how Docker behaves**, including authentication for registries.

This was critical for understanding:

```bash
gcloud auth configure-docker ...
```

That command does **not** touch your Dockerfile.

It modifies Docker’s config file.

## 3.15 Docker config file location

Default user-level Docker config location on Windows:

```text
C:\Users\manso\.docker\config.json
```

General default pattern:

```text
$HOME/.docker/config.json
```

Important nuance learned:

- default location is standard
- but it can change if environment variables or custom config paths are used

## 3.16 `credHelpers`

After running Docker auth configuration, Docker’s config got a section like:

```json
"credHelpers": {
  "europe-west3-docker.pkg.dev": "gcloud"
}
```

Meaning:

> when Docker talks to this registry, use `gcloud` as the credential helper

This does **not** store username/password.

It simply tells Docker who to ask for credentials.

## 3.17 How Docker auth actually works with GCP

This was one of the most important concepts learned.

Flow:

```text
docker push
    ↓
Docker sees registry hostname
    ↓
Docker checks config.json
    ↓
Finds credHelper = gcloud
    ↓
Docker calls gcloud
    ↓
gcloud gets OAuth access token
    ↓
Docker sends token to GCP
    ↓
GCP accepts and allows push
```

So the real relationship is:

```text
Docker → gcloud → OAuth token → GCP
```

## 3.18 OAuth token vs username/password

GCP auth here does not use username/password.

Instead:

- `gcloud auth login` opens browser
- you authenticate with Google
- gcloud stores OAuth credentials locally
- later Docker asks gcloud for a token

So `credHelpers` means:

> ask gcloud for a token when needed

## 3.19 How `docker push` knows where to push

`docker push` knows the destination from the image name itself.

Example:

```bash
docker push europe-west3-docker.pkg.dev/gcp-upskill-9891/cloud-backend-lab-repo/cloud-backend-lab:v1
```

Docker reads:

```text
registry = europe-west3-docker.pkg.dev
```

So it knows the remote destination is GCP Artifact Registry.

Key insight:

**The image name itself encodes the registry destination.**

## 3.20 Cloud Run

Cloud Run is a serverless service that runs containers.

General concept:

- you give it a container image
- it runs the image
- it provides a URL
- it can be public or private
- it auto-scales
- Google manages infrastructure

Important correction learned:

Cloud Run always gives a URL, but that does **not automatically mean public access**.  
Public/private depends on authentication settings.

## 3.21 Public vs private Cloud Run service

A Cloud Run service can be:

- public
- private

The flag:

```bash
--allow-unauthenticated
```

means:

> allow public access without requiring auth

Without that flag, the service URL still exists, but access would require authorization.

## 3.22 Serverless

Clean definition learned:

Serverless means:

- you do not manage VMs
- you do not manage scaling manually
- provider handles infrastructure
- you deploy code/container, not servers

## 3.23 Port handling in Cloud Run

Cloud Run expects the container to listen on a port provided by the platform.

This is why the app had:

```properties
server.port=${PORT:8080}
```

Meaning:

- if `PORT` is provided, use it
- otherwise default to `8080`

This makes the app work both:

- locally
- in Cloud Run

## 3.24 Who sets the port in Cloud Run

Important concept learned:

You did **not** manually set the port in the app at deployment time.

Cloud Run sets the `PORT` environment variable at runtime.

Typical default behavior:

```text
PORT=8080
```

Then Spring Boot reads it using:

```properties
server.port=${PORT:8080}
```

## 3.25 Can Cloud Run port be overridden?

Yes, Cloud Run can be told which container port to use via deploy flag.

Example:

```bash
--port 9090
```

Then Cloud Run sets:

```text
PORT=9090
```

Your app still needs to read `PORT`.

Important distinction vs Docker:

- Docker lets you do host-port ↔ container-port mapping
- Cloud Run controls networking and simply expects your app to listen on the declared/provided container port

## 3.26 Why Cloud Run controls port

Cloud Run owns the routing layer.

It handles:

- HTTPS endpoint
- load balancing
- request forwarding
- scaling

So it needs a predictable internal contract:

> your container must listen on the port Cloud Run provides or declares

---

# 4. Commands used today

## 4.1 Check gcloud installation

```bash
gcloud --version
```

### What it does
Checks whether Google Cloud CLI is installed and available in the terminal.

### Why it mattered
At first, `gcloud` was not recognized in IntelliJ terminal.  
The fix was effectively environment/terminal refresh.

---

## 4.2 Log in to Google Cloud

```bash
gcloud auth login
```

### What it does
Starts browser-based Google authentication for the CLI.

### What happens
- browser opens
- user logs into Google account
- gcloud stores auth credentials locally

### Why it mattered
Without login, no GCP commands requiring access would work.

---

## 4.3 Enable required services

```bash
gcloud services enable run.googleapis.com artifactregistry.googleapis.com cloudbuild.googleapis.com
```

### What it does
Enables the following APIs for the current GCP project:

- Cloud Run
- Artifact Registry
- Cloud Build

### Why each one matters
- Cloud Run → deployment/runtime
- Artifact Registry → image storage
- Cloud Build → commonly needed internal build/deploy support

### Key concept
This command means:
> enable these services in this project

---

## 4.4 Create Artifact Registry repository

```bash
gcloud artifacts repositories create cloud-backend-lab-repo --repository-format=docker --location=europe-west3 --description="Docker repository for cloud-backend-lab"
```

### What it does
Creates a Docker repository named `cloud-backend-lab-repo` in Artifact Registry.

### Flag breakdown
- `cloud-backend-lab-repo` = repository name
- `--repository-format=docker` = repository stores Docker images
- `--location=europe-west3` = region
- `--description=...` = human-readable description

---

## 4.5 Configure Docker authentication for GCP registry

```bash
gcloud auth configure-docker europe-west3-docker.pkg.dev
```

### What it does
Updates Docker config so Docker uses `gcloud` credentials for the given registry hostname.

### Important clarification
It does **not** use your Dockerfile.  
It modifies global Docker config.

### Internal effect
Adds `credHelpers` entry to Docker config.

---

## 4.6 Tag local image for GCP registry

```bash
docker tag cloud-backend-lab europe-west3-docker.pkg.dev/gcp-upskill-9891/cloud-backend-lab-repo/cloud-backend-lab:v1
```

### What it does
Adds a GCP registry-compatible name to the local image.

### Important clarification
- does not rebuild image
- does not duplicate image layers
- simply gives same image another reference

---

## 4.7 Push image to Artifact Registry

```bash
docker push europe-west3-docker.pkg.dev/gcp-upskill-9891/cloud-backend-lab-repo/cloud-backend-lab:v1
```

### What it does
Uploads the tagged image to GCP Artifact Registry.

### What was observed
Push output showed layers being uploaded and a final digest.

### Important concept
This is where the auth configured in Step 5 actually gets used.

---

## 4.8 Deploy to Cloud Run (normal/default port flow)

```bash
gcloud run deploy cloud-backend-lab --image europe-west3-docker.pkg.dev/gcp-upskill-9891/cloud-backend-lab-repo/cloud-backend-lab:v1 --region europe-west3 --platform managed --allow-unauthenticated
```

### What it does
Deploys the image as a Cloud Run service named `cloud-backend-lab`.

### Flag breakdown
- `cloud-backend-lab` = service name
- `--image ...` = container image to run
- `--region europe-west3` = deployment region
- `--platform managed` = serverless managed Cloud Run
- `--allow-unauthenticated` = public access

### Result
Cloud Run created service URL successfully.

---

## 4.9 Deploy to Cloud Run with explicit custom port

```bash
gcloud run deploy cloud-backend-lab --image europe-west3-docker.pkg.dev/gcp-upskill-9891/cloud-backend-lab-repo/cloud-backend-lab:v1 --region europe-west3 --platform managed --allow-unauthenticated --port 9090
```

### What it does
Same deployment as above, but explicitly tells Cloud Run that the container will listen on port 9090.

### Important concept
Cloud Run then injects:

```text
PORT=9090
```

Your app must read it using:

```properties
server.port=${PORT:8080}
```

---

# 5. Windows shell / command-line issues learned today

## 5.1 Multiline command issue in Windows CMD

Using backslash `\` as line continuation caused failures.

### Why
`\` works as shell continuation in Linux/macOS bash, but not in Windows CMD.

### Correct Windows CMD approach
Use one single line.

### PowerShell multiline alternative
Use backtick `` ` `` instead of `\`.

This was an important real-world terminal nuance.

---

# 6. Mistakes, confusion points, and corrections from today

## Mistake 1: Thinking Day 2 started with local Docker again
Correction:
Day 1 was already complete.  
Day 2 correctly began with GCP deployment.

## Mistake 2: Thinking `docker build` pushes image
Correction:
`docker build` is local only.  
`docker push` uploads.

## Mistake 3: Confusion between Dockerfile and Docker config
Correction:
Dockerfile = image build instructions  
Docker config = Docker client behavior and auth

## Mistake 4: Confusion about `docker.pkg.dev`
Correction:
It is Google’s shared Artifact Registry Docker domain, not user-defined.

## Mistake 5: Confusion about where Docker auth credentials come from
Correction:
From `gcloud` OAuth token, not username/password.

## Mistake 6: Confusion about Cloud Run URL meaning public
Correction:
Cloud Run always gives URL; public/private depends on IAM and `--allow-unauthenticated`.

## Mistake 7: Confusion about who defines port in Cloud Run
Correction:
Cloud Run injects `PORT`; app must read it.

## Mistake 8: Confusion about whether port can be overridden
Correction:
Yes, with `--port`, but Cloud Run still controls networking.

---

# 7. Complete mental model after Day 2

## Local side

```text
Spring Boot app
    ↓
Maven JAR
    ↓
Docker image
    ↓
Local Docker storage
```

## GCP side

```text
Tagged image with GCP path
    ↓
docker push
    ↓
Artifact Registry
    ↓
Cloud Run deploy
    ↓
Cloud Run pulls image
    ↓
Container starts
    ↓
Public URL available
```

## Authentication side

```text
gcloud auth login
    ↓
OAuth credentials stored by gcloud
    ↓
gcloud auth configure-docker
    ↓
Docker config gets credHelper = gcloud
    ↓
docker push asks gcloud for token
    ↓
token sent to GCP
    ↓
push succeeds
```

## Port side

```text
Cloud Run sets PORT
    ↓
Spring Boot reads PORT via server.port=${PORT:8080}
    ↓
App listens on expected port
    ↓
Cloud Run routes traffic correctly
```

---

# 8. Final outcome of Day 2

By the end of Day 2, the following was achieved:

- gcloud installed and working
- authenticated with Google Cloud
- project selected and active
- required APIs enabled
- Artifact Registry repository created
- Docker configured to authenticate with Artifact Registry
- local image tagged in correct GCP format
- image pushed successfully to Artifact Registry
- Cloud Run service deployed successfully
- public service URL generated

This means the app moved from:

```text
localhost only
```

to:

```text
real public cloud deployment
```

---

# 9. Most important takeaways from today

1. A Docker image must be named in registry format before it can be pushed to that registry.
2. `docker push` decides destination from the image name.
3. `gcloud auth configure-docker` does not modify Dockerfile; it modifies Docker config.
4. Docker auth with GCP uses OAuth token from `gcloud`, not username/password.
5. Artifact Registry is the image storage; Cloud Run is the image runtime.
6. Cloud Run deploys containers, not source code.
7. Cloud Run controls runtime networking.
8. The app must read `PORT` from environment in Cloud Run.
9. Public access is a deploy/access choice, not a property of Cloud Run itself.
10. Terminal output is the final ground truth for whether a command worked.

---

# 10. Short glossary

## Image
Packaged application and runtime layers.

## Tag
Version or alternate reference to an image.

## Registry
Remote place where images are stored.

## Repository
Collection/category inside a registry.

## Cloud Run service
Running serverless endpoint backed by a container image.

## Artifact Registry
GCP service that stores artifacts like Docker images.

## credHelper
Docker auth mechanism that delegates credential retrieval.

## OAuth token
Temporary auth token used instead of raw passwords.

## Region
Physical GCP location for a service/resource.

## Serverless
Provider manages infrastructure, scaling, and runtime platform.

---

# 11. Recommended command recap (clean list)

```bash
gcloud --version
gcloud auth login
gcloud services enable run.googleapis.com artifactregistry.googleapis.com cloudbuild.googleapis.com
gcloud artifacts repositories create cloud-backend-lab-repo --repository-format=docker --location=europe-west3 --description="Docker repository for cloud-backend-lab"
gcloud auth configure-docker europe-west3-docker.pkg.dev
docker tag cloud-backend-lab europe-west3-docker.pkg.dev/gcp-upskill-9891/cloud-backend-lab-repo/cloud-backend-lab:v1
docker push europe-west3-docker.pkg.dev/gcp-upskill-9891/cloud-backend-lab-repo/cloud-backend-lab:v1
gcloud run deploy cloud-backend-lab --image europe-west3-docker.pkg.dev/gcp-upskill-9891/cloud-backend-lab-repo/cloud-backend-lab:v1 --region europe-west3 --platform managed --allow-unauthenticated
```

---

# 12. One-line summary of Day 2

Today was the transition from:

```text
local container knowledge
```

to:

```text
real cloud deployment knowledge
```
