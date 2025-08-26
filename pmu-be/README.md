# pmu-be

This project was created using the [Ktor Project Generator](https://start.ktor.io).

Here are some useful links to get you started:

- [Ktor Documentation](https://ktor.io/docs/home.html)
- [Ktor GitHub page](https://github.com/ktorio/ktor)
- The [Ktor Slack chat](https://app.slack.com/client/T09229ZC6/C0A974TJ9). You'll need
  to [request an invite](https://surveys.jetbrains.com/s3/kotlin-slack-sign-up) to join.

## Features

Here's a list of features included in this project:

| Name                                                                        | Description                                                                                             |
| -----------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------- |
| [AsyncAPI](https://start.ktor.io/p/asyncapi)                                | Generates and serves AsyncAPI documentation                                                             |
| [Caching Headers](https://start.ktor.io/p/caching-headers)                  | Provides options for responding with standard cache-control headers                                     |
| [Compression](https://start.ktor.io/p/compression)                          | Compresses responses using encoding algorithms like GZIP                                                |
| [CORS](https://start.ktor.io/p/cors)                                        | Enables Cross-Origin Resource Sharing (CORS)                                                            |
| [HSTS](https://start.ktor.io/p/hsts)                                        | Enables HTTP Strict Transport Security (HSTS)                                                           |
| [Routing](https://start.ktor.io/p/routing)                                  | Provides a structured routing DSL                                                                       |
| [OpenAPI](https://start.ktor.io/p/openapi)                                  | Serves OpenAPI documentation                                                                            |
| [Simple Cache](https://start.ktor.io/p/simple-cache)                        | Provides API for cache management                                                                       |
| [Simple Redis Cache](https://start.ktor.io/p/simple-redis-cache)            | Provides Redis cache for Simple Cache plugin                                                            |
| [Swagger](https://start.ktor.io/p/swagger)                                  | Serves Swagger UI for your project                                                                      |
| [Authentication](https://start.ktor.io/p/auth)                              | Provides extension point for handling the Authorization header                                          |
| [Authentication OAuth](https://start.ktor.io/p/auth-oauth)                  | Handles OAuth Bearer authentication scheme                                                              |
| [CSRF](https://start.ktor.io/p/csrf)                                        | Cross-site request forgery mitigation                                                                   |
| [Status Pages](https://start.ktor.io/p/status-pages)                        | Provides exception handling for routes                                                                  |
| [Call Logging](https://start.ktor.io/p/call-logging)                        | Logs client requests                                                                                    |
| [Call ID](https://start.ktor.io/p/callid)                                   | Allows to identify a request/call.                                                                      |
| [OpenTelemetry](https://start.ktor.io/p/opentelemetry-java-instrumentation) | Instruments applications with distributed tracing, metrics, and logging for comprehensive observability |
| [Content Negotiation](https://start.ktor.io/p/content-negotiation)          | Provides automatic content conversion according to Content-Type and Accept headers                      |
| [Jackson](https://start.ktor.io/p/ktor-jackson)                             | Handles JSON serialization using Jackson library                                                        |
| [kotlinx.serialization](https://start.ktor.io/p/kotlinx-serialization)      | Handles JSON serialization using kotlinx.serialization library                                          |
| [MongoDB](https://start.ktor.io/p/mongodb)                                  | Adds MongoDB database to your application                                                               |
| [Koin](https://start.ktor.io/p/koin)                                        | Provides dependency injection                                                                           |

## Structure

This project includes the following modules:

| Path             | Description                                             |
| ------------------|--------------------------------------------------------- |
| [server](server) | A runnable Ktor server implementation                   |
| [core](core)     | Domain objects and interfaces                           |
| [client](client) | Extensions for making requests to the server using Ktor |

## Building

To build the project, use one of the following tasks:

| Task                                            | Description                                                          |
| -------------------------------------------------|---------------------------------------------------------------------- |
| `./gradlew build`                               | Build everything                                                     |
| `./gradlew :server:buildFatJar`                 | Build an executable JAR of the server with all dependencies included |
| `./gradlew :server:buildImage`                  | Build the docker image to use with the fat JAR                       |
| `./gradlew :server:publishImageToLocalRegistry` | Publish the docker image locally                                     |

## Running

To run the project, use one of the following tasks:

| Task                          | Description                      |
| -------------------------------|---------------------------------- |
| `./gradlew :server:run`       | Run the server                   |
| `./gradlew :server:runDocker` | Run using the local docker image |

If the server starts successfully, you'll see the following output:

```
2024-12-04 14:32:45.584 [main] INFO  Application - Application started in 0.303 seconds.
2024-12-04 14:32:45.682 [main] INFO  Application - Responding at http://0.0.0.0:8080
```

