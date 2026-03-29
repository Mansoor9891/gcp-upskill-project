# Day 0 Setup Notes

## Goal
Prepare local machine, development tools, and Google Cloud so development can start without setup blockers.

---

## 1. Local Setup

Installed:
- IntelliJ IDEA
- Java (JDK 17)
- Maven

Configured environment:
- JAVA_HOME = JDK root
- MAVEN_HOME = Maven root

Updated PATH:
- %JAVA_HOME%\bin
- %MAVEN_HOME%\bin

Commands used:
java -version
javac -version
mvn -v

Concept:
PATH allows the terminal to recognise commands globally.

### Command explanations

`java -version`
- Checks if Java is installed
- Shows Java runtime version

`javac -version`
- Checks Java compiler
- Confirms code can be compiled

`mvn -v`
- Checks Maven installation
- Shows Maven version, Java version, and Java home used by Maven

---

## 2. Docker Setup

Installed:
- Docker Desktop
- Docker CLI

Commands used:
docker --version
docker run hello-world

Concept:
Docker CLI is the command tool.
Docker Desktop runs the engine.
hello-world verifies installation.

### Command explanations

`docker --version`
- Checks Docker CLI is installed

`docker run hello-world`
- Pulls the hello-world image
- Creates a container
- Runs it
- Prints success output

This confirms:
- Docker engine works
- Docker networking works
- Container execution works

---

## 3. Git and GitHub Setup

Installed:
- Git

Configured:
git config --global user.name "Your Name"
git config --global user.email "your@email.com"

Commands used:
git init
git add .
git commit -m "Initial commit"
git push
git pull
git clone <repo-url>
git config --global --list
ssh -V
where ssh

Project:
gcp-upskill-project

Structure:
app/
infra/
notes/
README.md

Important:
Git does NOT track empty folders.

### Command explanations

`git init`
- Creates a new Git repository in the current folder

`git add .`
- Stages all current files for commit

`git commit -m "Initial commit"`
- Saves a local snapshot of staged changes

`git push`
- Uploads local commits to GitHub

`git pull`
- Downloads latest changes from GitHub and merges them

`git clone <repo-url>`
- Downloads a remote repo from GitHub to the local machine

`git config --global user.name "Your Name"`
- Sets Git username globally

`git config --global user.email "your@email.com"`
- Sets Git email globally

`git config --global --list`
- Displays current global Git configuration

`ssh -V`
- Shows SSH version

`where ssh`
- Shows where SSH is installed on the system

---

## 4. IntelliJ Test

- Created Maven project
- Created Main class
- Ran successfully

Note:
- Spring Boot NOT configured
- Lombok NOT configured

---

## 5. Google Cloud CLI Setup

Installed:
- Google Cloud CLI

Commands used:
gcloud auth login
gcloud init
gcloud projects create gcp-upskill-9891
gcloud config set project gcp-upskill-9891
gcloud config list

Project:
gcp-upskill-9891

Concept:
Project IDs must be globally unique.

### Command explanations

`gcloud auth login`
- Opens browser login
- Authenticates Google account for CLI usage

`gcloud init`
- Starts initial setup wizard
- Lets you choose account, create/select project, and set defaults

`gcloud projects create gcp-upskill-9891`
- Creates a new Google Cloud project

`gcloud config set project gcp-upskill-9891`
- Sets the default active project for future gcloud commands

`gcloud config list`
- Shows current gcloud configuration
- Useful to verify active project

---

## 6. GCP Activation

Steps:
- Accepted Terms of Service in console
- Linked billing account

Concept:
- Project can exist without billing
- APIs require billing

---

## 7. Enable APIs

Commands used:
gcloud services enable run.googleapis.com
gcloud services enable artifactregistry.googleapis.com

Meaning:
Enables Cloud Run and Artifact Registry services.

### Command explanations

`gcloud services enable run.googleapis.com`
- Enables Cloud Run API for the active project

`gcloud services enable artifactregistry.googleapis.com`
- Enables Artifact Registry API for the active project

---

## 8. Artifact Registry

Command used:
gcloud artifacts repositories create my-repo --repository-format=docker --location=us-central1

Concept:
Artifact Registry stores Docker images.

### Command explanation

`gcloud artifacts repositories create my-repo --repository-format=docker --location=us-central1`
- Creates an Artifact Registry repository named `my-repo`
- `--repository-format=docker` means it will store Docker images
- `--location=us-central1` sets the region where the repository lives

---

## 9. Region

Used:
us-central1

Concept:
Keep all services in the same region.
EU projects should use europe-west3 (Frankfurt).

---

## 10. CLI vs GUI

- CLI = terminal commands (gcloud)
- SDK Shell = preconfigured terminal
- GUI = browser console

---

## 11. Final Status

Local:
- Java ✔
- Maven ✔
- Docker ✔
- Git ✔
- IntelliJ ✔

Cloud:
- GCP CLI ✔
- Project ✔
- Billing ✔
- APIs ✔
- Artifact Registry ✔

---

## 12. Big Picture Flow

Java code → Maven build → Docker image → Artifact Registry → Cloud Run

---

## 13. Commands summary

Java:
java -version
javac -version

Maven:
mvn -v

Docker:
docker --version
docker run hello-world

Git:
git init
git add .
git commit -m "Initial commit"
git push
git pull
git clone <repo-url>
git config --global user.name "Your Name"
git config --global user.email "your@email.com"
git config --global --list
ssh -V
where ssh

GCP:
gcloud auth login
gcloud init
gcloud projects create gcp-upskill-9891
gcloud config set project gcp-upskill-9891
gcloud config list
gcloud services enable run.googleapis.com
gcloud services enable artifactregistry.googleapis.com
gcloud artifacts repositories create my-repo --repository-format=docker --location=us-central1

---

## 14. Next (Day 1)

- Create Spring Boot app
- Add REST endpoint
- Run locally
- Dockerize application
- Deploy to Cloud Run
