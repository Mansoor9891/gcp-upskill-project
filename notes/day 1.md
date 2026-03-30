# DAY 1 — SPRING BOOT + DOCKER (FULL NOTES)

## Goal of Day 1

The goal was to build a simple Spring Boot API, package it as a JAR, run it inside Docker, understand how Docker networking works, and prepare the app for cloud deployment.

By the end of Day 1, the app should:

* run locally
* build successfully with Maven
* build successfully as a Docker image
* run successfully as a Docker container
* be reachable from the browser
* support runtime port configuration through environment variables

---

## Big Picture

Day 1 was about turning this:

Java code inside IntelliJ

into this:

a runnable, portable backend service inside a Docker container

The important transition was:

source code
→ compiled application
→ packaged JAR
→ Docker image
→ running container

---

## Project Setup

We created a Spring Boot project using Spring Initializr, not a plain Java Maven project.

Why:

* Spring Initializr gives a proper Spring Boot base project
* a plain Maven Java project would not automatically be a web application
* for the 50-day plan, starting with the correct foundation matters

We used:

* Java 17
* Maven
* Packaging: Jar
* Dependency: Spring Web

Project identity:

* Group: `com.nextgen`
* Artifact: `cloud-backend-lab`
* Package: `com.nextgen.cloudbackendlab`

Important meaning:

* `groupId` is the organization / namespace
* `artifactId` is the project name
* together they uniquely identify the project

Example:
`com.nextgen:cloud-backend-lab`

---

## Why Spring Boot and Why Spring Web

This created a lot of confusion, so here is the final correct explanation.

### Spring Boot Generator

When IntelliJ showed:
Generator: Spring Boot

that meant:

* create a Spring Boot project template
* add Spring Boot auto-configuration support
* generate the main class with `@SpringBootApplication`
* generate the Maven structure

It did **not** mean:

* the app is already a web application
* the app already has an embedded server
* the app can already handle HTTP requests

The generator gives the base Spring Boot project.

### Spring Web

In IntelliJ UI, selecting **Spring Web** adds this dependency:

`spring-boot-starter-web`

This is part of the Spring Boot ecosystem.

That dependency gives the app:

* web capabilities
* Spring MVC
* REST controller support
* embedded Tomcat
* HTTP request handling

### Final correct understanding

Spring Boot core does **not** itself come with a web server by default.

The correct statement is:

Spring Boot auto-configures and starts an embedded server when a web starter such as `spring-boot-starter-web` is present on the classpath.

This means:

* Spring Boot = auto-configuration engine
* Spring Web starter = brings the web stack, including Tomcat
* both together = runnable web app

### Why this mattered

Without Spring Web:

* the app could still start
* but it would be a non-web application
* there would be no Tomcat
* there would be no port 8080
* `/hello` would not work
* browser would not connect

With Spring Web:

* Tomcat starts
* port 8080 is used by default
* controllers work
* browser requests work

---

## Project Structure

For a real 50-day project, we did not want a throwaway structure.

We used the base package:

`com.nextgen.cloudbackendlab`

and created a proper controller package:

`com.nextgen.cloudbackendlab.controller`

Why:

* this project is not just a one-hour demo
* later we will add service, repository, model, dto, config, exception, etc.
* starting clean now avoids messy refactoring later

A good future structure looks like this:

`com.nextgen.cloudbackendlab`

* `controller`
* `service`
* `repository`
* `model`
* `dto`
* `config`
* `exception`

---

## The Main Spring Boot Class

Spring Initializr generated a class like:

`CloudBackendLabApplication.java`

This contains:

* `@SpringBootApplication`
* the `main` method
* the app bootstrap logic

This is the entry point of the Spring Boot application.

---

## The Controller We Created

We created a controller under the `controller` package.

Code:

```java
package com.nextgen.cloudbackendlab.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "Day 1 working";
    }
}
```

### What this means

`@RestController`

* tells Spring that this class handles web requests
* return values go directly into the HTTP response body

`@GetMapping("/hello")`

* maps HTTP GET requests for `/hello` to this method

`return "Day 1 working";`

* sends that text back to the browser

### Why this works automatically

Because:

* Spring Boot scans packages under the main application package
* `CloudBackendLabApplication` is in `com.nextgen.cloudbackendlab`
* the controller package is a subpackage
* so Spring finds it automatically

---

## Running the App Locally

We ran the app using:

```bash
mvn spring-boot:run
```

This starts the Spring Boot application directly from the project.

Then we tested in the browser:

`http://localhost:8080/hello`

Expected response:

`Day 1 working`

### What happened under the hood

Spring Boot:

* started the application context
* auto-configured the web layer
* started embedded Tomcat
* opened port 8080
* routed `/hello` to the controller method

### Important concept

`localhost`
means:
your own computer

So when the browser hits `http://localhost:8080/hello`, it is talking to your machine.

---

## Maven Basics We Learned

### What Maven is

Maven is a build tool.
It manages:

* dependencies
* compilation
* packaging
* testing
* build lifecycle

### What `pom.xml` is

The `pom.xml` file defines:

* project identity
* dependencies
* plugins
* build configuration

---

## Meaning of JAR and WAR

### JAR

A JAR is a packaged Java application.

In our case, with Spring Boot, the JAR is executable and contains:

* our code
* dependencies
* embedded Tomcat
* Spring Boot loader classes

This is often called a fat JAR.

You can run it with:

```bash
java -jar app.jar
```

### WAR

A WAR is a web application archive used in older / classic Java web deployment.

A WAR is typically:

* deployed into an external server such as Tomcat
* not run directly the same way a Spring Boot fat JAR is

### Key difference

Classic WAR model:

* server runs the app

Spring Boot fat JAR model:

* app contains the server

### Clean mental model

WAR:
app needs server

JAR:
app includes server support and runs directly

---

## What `mvn clean package` Does

We used:

```bash
mvn clean package
```

This combines two lifecycle phases.

### `clean`

Deletes previous build output, especially:
`target/`

Purpose:

* remove old compiled files
* ensure fresh build

### `package`

Builds the project and packages it into an artifact.

For our Spring Boot project, that means:

* compile code
* run tests
* package into JAR

Output:
`target/cloud-backend-lab-0.0.1-SNAPSHOT.jar`

### Why this mattered

Docker needs the JAR.
Without packaging the app first, Docker would have nothing to run.

---

## What `mvn clean install` Does

We also discussed:

```bash
mvn clean install
```

This does everything `package` does, plus one more step:

install the artifact into the local Maven repository

Location:
`~/.m2/repository`

Why this exists:

* useful when other local Maven projects depend on this project

For our current use case:

* `install` was not necessary
* `package` was enough

### Final distinction

`package`

* builds the app

`install`

* builds the app and publishes it to the local Maven repository

---

## Maven Lifecycle Steps We Discussed

For `mvn clean install`, the important phases were:

* `clean` — delete old build output
* `validate` — validate project structure and configuration
* `compile` — compile Java code
* `test` — run tests
* `package` — create the artifact (JAR)
* `verify` — run additional checks
* `install` — copy artifact to local Maven repository

For Day 1, the most important were:

* clean
* compile
* package

---

## What the `target` Folder Is

The `target/` folder is the Maven build output directory.

It contains generated build artifacts such as:

* the packaged JAR
* compiled `.class` files
* test classes
* temporary build files

### Important rule

Do not manually edit `target/`.

It is generated output and can be deleted and recreated at any time.

---

## Old Maven / Classic Java Architecture Discussion

We also discussed how classic enterprise Java projects work.

### Important distinction: package vs module vs project

A Java package such as:

* `controller`
* `service`
* `domain`

is just code organization inside one project.

A Maven module or separate project is a separate build unit with its own:

* `pom.xml`
* `src/main/java`
* `target/`
* artifact output

### If everything is inside one Maven project

Then:

* all packages compile together
* one artifact is produced
* no separate JAR per package

### If they are separate Maven projects/modules

Then:

* each project/module gets its own `target/`
* each project/module produces its own JAR or WAR
* a final web project can depend on those JARs and package them into its own WAR

This is what happens in classic multi-module enterprise apps.

### Final clean understanding

Packages do not build separately.
Projects/modules do.

---

## Docker Basics

### What Docker is

Docker is a tool to package and run applications inside containers.

A container is an isolated runtime environment.

### Why Docker exists

It solves problems like:

* works on my machine
* different local setups
* dependency differences
* environment inconsistency

### What Docker gave us

With Docker, our app can run in a portable environment containing:

* an OS layer
* Java
* our app JAR
* a startup command

---

## Image vs Container

This was a major Day 1 concept.

### Docker Image

An image is a blueprint / template.

It contains:

* base environment
* runtime
* our app
* startup instructions

### Docker Container

A container is a running instance of an image.

### Simple analogy

* image = class blueprint
* container = object instance

### Final understanding

JAR is not the image.

The image contains:

* the base OS
* Java
* the JAR
* the startup command

The container is the running instance of that image.

---

## The Dockerfile

Final Dockerfile:

```dockerfile
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
```

Now the exact meaning of every line:

### `FROM eclipse-temurin:17-jdk`

This defines the base image.

Meaning:

* start building this image from an existing image
* use a Linux-based image with Java 17 installed

Important clarification:

* `FROM` is not the JVM
* `FROM` defines the base environment
* the JVM is included in that base image

Why `FROM` is called `FROM`:
because Docker builds your image on top of another existing image

So:

* `FROM` = starting point / base layer
* not “define environment from scratch”

### Why `openjdk:17-jdk-slim` did not work

We originally used:
`openjdk:17-jdk-slim`

It failed because that old image/tag path is deprecated / unreliable now.

We switched to:
`eclipse-temurin:17-jdk`

This is a maintained and safe Java 17 base image.

### `WORKDIR /app`

This creates or sets the working directory **inside the container**.

Important:

* it does not refer to your local machine
* it does not mean “find local folder app”
* it means “inside the image/container, work from `/app`”

After this line, Docker commands work relative to `/app`.

### `COPY target/*.jar app.jar`

This copies the built JAR from the Docker build context into the image.

Important meaning:

* source = `target/*.jar` from the build context
* destination = `app.jar` inside the working directory

Because `WORKDIR /app` is set, the destination becomes:
`/app/app.jar`

We also renamed the file to `app.jar` for simplicity.

### How Docker knew where `target/*.jar` was

Because we built the image with:

```bash
docker build -t cloud-backend-lab .
```

That final dot means:
use the current folder as the build context

The build context is the set of files Docker can access during image build.

So Docker could see:

* Dockerfile
* target/
* source files
* project files

If a file is outside the build context, Docker cannot copy it.

### `EXPOSE 8080`

This line caused confusion.

Final correct explanation:

* `EXPOSE` is just documentation / metadata
* it does **not** actually map or open the port
* it simply says “this container expects to use port 8080”

Actual access comes from `docker run -p ...`

### `ENTRYPOINT ["java","-jar","app.jar"]`

This defines the command Docker runs when the container starts.

Equivalent command:

```bash
java -jar app.jar
```

Meaning:

* start Java
* run the executable Spring Boot JAR
* Spring Boot starts
* embedded Tomcat starts
* app begins listening on its configured port

This is the command that launches the application inside the container.

---

## What `java -jar app.jar` Means

Breakdown:

* `java` = Java runtime executable
* `-jar` = run the given JAR as an executable application
* `app.jar` = our packaged Spring Boot app

When this runs:

* Java reads the JAR
* Spring Boot launcher starts
* embedded Tomcat starts
* the app begins listening on the configured port

If this had been a WAR in the classic setup, the model would be different:

* Tomcat would start
* Tomcat would load the WAR
* app would run inside Tomcat

So with JAR:
the app runs itself

With WAR:
the server runs the app

---

## Building the Docker Image

We ran:

```bash
docker build -t cloud-backend-lab .
```

Breakdown:

* `docker build` = build an image
* `-t cloud-backend-lab` = tag/name the image
* `.` = current folder as build context

Why `-t` mattered:

* without it, Docker still builds an image
* but the image only has an ID, not a friendly name
* tagging makes it easy to run the image later

Why the dot mattered:

* Docker always needs a build context
* the context is the directory Docker can access
* this is how Docker finds `target/*.jar`

### Final understanding of build context

Build context = everything Docker can see and use while building the image.

---

## Running the Container

We ran commands like:

```bash
docker run -p 8080:8080 cloud-backend-lab
```

and later:

```bash
docker run -d -p 9090:8080 cloud-backend-lab
```

and later:

```bash
docker run -d -e PORT=9090 -p 9090:9090 cloud-backend-lab
```

### `docker run`

Creates and starts a new container from the image.

### `-d`

Detached mode.
Run in the background so the terminal is free.

Without `-d`:

* the terminal attaches to the running container logs
* you cannot type new commands in that same terminal

### `-p hostPort:containerPort`

This is Docker port mapping.

Examples:

* `-p 8080:8080`
* `-p 9090:8080`
* `-p 9090:9090`

Meaning:

* left side = host machine port
* right side = container port

This is one of the most important concepts of Day 1.

---

## Exact Port Mapping Explanation

This was the core mental model we finally locked in.

### Example

```bash
docker run -p 9090:8080 cloud-backend-lab
```

Means:

listen on port 9090 on my machine, and forward traffic to port 8080 inside the container

### Final correct flow

Browser
→ `localhost:9090` on your machine
→ Docker Engine / Docker Desktop sees mapping
→ Docker forwards to container port 8080
→ Spring Boot handles request

### Important correction we established

The browser does **not** talk directly to the container.

The browser always talks to:
your host machine

Docker handles the forwarding.

### Final clean mental model

Left side:
where you access from outside

Right side:
where the app is actually listening inside the container

### Most important truth

The container itself does not “know” the host port mapping.

Docker knows it.
Docker Desktop / Docker Engine handles it.

Spring Boot only knows the internal application port.

---

## Docker Desktop’s Role

This was another important missing piece.

On Windows, Docker Desktop:

* runs Docker Engine
* manages Linux containers
* handles networking and port mapping
* provides the UI for images, containers, logs, ports, start/stop

### Why Docker Desktop mattered on Windows

We were running Linux containers on a Windows machine.

Docker Desktop handles that whole layer for us.

### Final understanding

Browser
→ your PC
→ Docker Desktop / Docker Engine
→ container
→ Spring Boot app

Docker Desktop is the runtime and bridge on your machine.

---

## Why the Terminal Was “Blocked”

When we ran:

```bash
docker run -p 8080:8080 cloud-backend-lab
```

without `-d`:

* the terminal attached to container output
* the app was running in the foreground
* the terminal became occupied

That is why we could not type `docker ps` in the same terminal.

Solutions:

* open a new terminal
* run with `-d`
* or stop the current run with `Ctrl + C`

---

## Docker Commands We Used and Why

### Build image

```bash
docker build -t cloud-backend-lab .
```

Build a Docker image from the current folder.

### Run container in foreground

```bash
docker run -p 8080:8080 cloud-backend-lab
```

Run the image and attach the terminal.

### Run container in background

```bash
docker run -d -p 9090:8080 cloud-backend-lab
```

Run the container in detached mode.

### Pass environment variable

```bash
docker run -d -e PORT=9090 -p 9090:9090 cloud-backend-lab
```

Set `PORT=9090` inside the container and map host 9090 to container 9090.

### List running containers

```bash
docker ps
```

Show currently running containers.

### Show logs

```bash
docker logs <container_id>
```

Inspect container logs.

### Stop a container

```bash
docker stop <container_id>
```

Stop a running container.

---

## Environment Variables and `-e`

We learned:

`-e` means:
set an environment variable inside the container

Example:

```bash
docker run -e PORT=9090 -p 9090:9090 cloud-backend-lab
```

This means:

* inside container, set `PORT=9090`
* then start the app

### Syntax mistake we hit

Wrong:

```bash
docker run -e PORT 9090 -p 9090:9090 cloud-backend-lab
```

Correct:

```bash
docker run -e PORT=9090 -p 9090:9090 cloud-backend-lab
```

Why the wrong one failed:
Docker interpreted `9090` as an image name.

---

## `application.properties` and Runtime Configuration

We added:

```properties
server.port=${PORT:8080}
```

This means:

* if environment variable `PORT` exists, use it
* otherwise default to 8080

### Important final understanding

`application.properties` is runtime configuration.

It is read when the app starts.

But there was an important subtle distinction:

* changing the **environment variable** is a runtime change
* changing the **application.properties file itself** changes the contents of the JAR

So:

### If you change only `-e PORT=...`

No rebuild needed.

### If you change `application.properties`

Rebuild is required.

This was the build-vs-runtime confusion we resolved.

---

## The Build vs Runtime Rule

This was one of the most important lessons.

### Change inside the JAR

Examples:

* Java code
* controller code
* `application.properties`

Then you must:

* rebuild the JAR
* rebuild the Docker image
* restart the container

### Change outside the JAR

Examples:

* `-e PORT=...`
* Docker runtime flags

Then no rebuild is needed.
You just rerun the container.

### Final rule

Inside JAR → rebuild
Outside JAR → no rebuild

---

## Why the App Still Used Port 8080 After We Added `${PORT:8080}`

We hit this exact issue.

What happened:

* the old JAR did not yet contain the updated property
* the Docker image still contained the old JAR
* so even though we passed `-e PORT=9090`, the app still started on 8080

How we confirmed this:

* `docker logs ...`
* saw “Tomcat initialized with port 8080”

How we fixed it:

* rebuild JAR
* rebuild image
* rerun container

This was a real and useful mistake because it clarified the difference between build-time content and runtime variables.

---

## EXPOSE vs `-p`

This caused important confusion too.

### `EXPOSE 8080`

This does not actually publish the port.
It is only:

* documentation
* metadata
* a hint to tooling

### `-p 9090:8080`

This is what actually maps traffic.

### Important result

Even if `EXPOSE` is wrong, the app may still work if `-p` is correct.

But humans reading the Dockerfile may get confused.

### Final rule

* `EXPOSE` helps humans and tools
* `-p` is what actually connects host and container

---

## Why `docker run -p 9090:9090` Failed When App Was on 8080

Because:

* Docker forwarded host 9090 to container 9090
* but Spring Boot was listening on container 8080
* nothing was listening on container 9090

Correct fix:

* either map `9090:8080`
* or make the app run on 9090 with `-e PORT=9090`

---

## Local Spring Boot Run vs Docker Run

### Local run

When running directly with Spring Boot locally:

* app listens on localhost:8080 on your machine
* browser sends request directly to your machine
* Spring Boot responds directly

### Docker run

When running inside Docker:

* app listens inside the container
* browser still talks to your machine
* Docker forwards traffic to the container
* app responds from inside the container

### Final 3-line explanation

Browser sends request to `localhost:PORT` on your machine.
Docker maps that host port to the container port.
Spring Boot inside the container handles the request and returns the response.

---

## Common Mistakes We Hit Today

### Mistake 1 — confusing Spring Boot generator with a ready-made web app

Final fix:
Generator creates the base project.
Spring Web starter makes it a web app.

### Mistake 2 — thinking Spring Boot core alone “comes with embedded server”

Final fix:
Spring Boot auto-configures the server when `spring-boot-starter-web` is present.

### Mistake 3 — creating the controller in the root package for a 50-day project

Final fix:
put controllers in a dedicated `controller` package.

### Mistake 4 — using old/deprecated `openjdk` image tag

Final fix:
use `eclipse-temurin:17-jdk`

### Mistake 5 — misunderstanding `WORKDIR`

Final fix:
it refers to a directory inside the container, not your local machine.

### Mistake 6 — misunderstanding how `COPY` finds the JAR

Final fix:
Docker uses the build context from `docker build ... .`

### Mistake 7 — wrong `-e` syntax

Wrong:
`-e PORT 9090`

Correct:
`-e PORT=9090`

### Mistake 8 — expecting runtime env var changes to work without rebuilding after changing the properties file

Final fix:
changing `application.properties` changes the JAR, so rebuild is required.

### Mistake 9 — mixing up host port and container port

Final fix:
left side = host
right side = container

### Mistake 10 — thinking browser talks directly to the container

Final fix:
browser talks to host; Docker forwards to container.

---

## Final Commands From Day 1

### Run locally

```bash
mvn spring-boot:run
```

### Build JAR

```bash
mvn clean package
```

### Build image

```bash
docker build -t cloud-backend-lab .
```

### Run container with host 9090 and container 8080

```bash
docker run -d -p 9090:8080 cloud-backend-lab
```

### Run container with environment-based port 9090

```bash
docker run -d -e PORT=9090 -p 9090:9090 cloud-backend-lab
```

### Show running containers

```bash
docker ps
```

### Show logs

```bash
docker logs <container_id>
```

### Stop container

```bash
docker stop <container_id>
```

---

## Final Workflow for Code Changes

If you change code or `application.properties`:

```bash
mvn clean package
docker build -t cloud-backend-lab .
docker stop <container_id>
docker run -d -p 9090:8080 cloud-backend-lab
```

If you only change the runtime environment variable value:

```bash
docker stop <container_id>
docker run -d -e PORT=9090 -p 9090:9090 cloud-backend-lab
```

No rebuild needed in that second case.

---

## Core Concepts We Learned Today

* Spring Boot generator creates the base project
* Spring Web adds the Boot web starter
* Spring Boot auto-configures web behavior when the web starter is present
* JAR is the packaged app
* WAR is the old external-server deployment model
* `target/` is Maven build output
* Docker image is not the JAR; it contains the JAR plus runtime environment
* container is the running instance of the image
* `FROM` defines the base image / environment
* `WORKDIR` sets a folder inside the container
* `COPY` copies files from the Docker build context into the image
* `ENTRYPOINT` defines the startup command
* Docker Desktop runs Docker Engine and handles container networking on Windows
* `-p` maps host port to container port
* `-e` sets environment variables inside the container
* `application.properties` is read at runtime
* changing config files inside the project still requires rebuild because the JAR contents changed
* build-time and runtime are different concerns

---

## Final Summary

Day 1 started with a Java/Spring project in IntelliJ and ended with a working containerized backend.

The real chain we built was:

source code
→ Spring Boot app
→ Maven package
→ executable JAR
→ Docker image
→ running container
→ browser request through Docker port mapping

The final understanding is:

Spring Boot runs the app.
Docker runs the environment.
Docker Desktop runs and connects containers on the machine.
Port mapping connects host traffic to the container.
Environment variables control runtime behavior.
Code/config inside the JAR requires rebuild when changed.

---

## Day 1 Result

Day 1 is complete when all of these are true:

* app runs locally
* JAR builds
* Docker image builds
* container runs
* browser reaches the API
* port mapping is understood
* runtime environment variables are understood
* build vs runtime distinction is understood

That happened.

## Day 1 Complete
