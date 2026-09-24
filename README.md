# SimpleURL

A simple URL shortener REST API built with Java 25 and Spring Boot 4.

> **Status:** work in progress. Features are delivered in small pull requests; see [Roadmap](#roadmap).

## Goals

- Provide a small, well-structured backend that shortens URLs, redirects visitors and tracks clicks.
- Showcase a simple, modern and explicit Spring Boot architecture with high test coverage (≥ 80%).
- Keep the persistence layer database-ready: JPA entities map the real tables, while the runtime uses in-memory repositories (no database required to run the app).

## Features

- Create a short link for any `http`/`https` URL.
- Redirect `/{code}` to the original URL (`302 Found`).
- Get and delete links.
- Optional expiration date: expired links answer `410 Gone`.
- Click statistics: total clicks, last click and clicks per day.
- Errors follow [RFC 9457 Problem Details](https://www.rfc-editor.org/rfc/rfc9457).
- Interactive API documentation with Swagger UI.

## Tech stack

| Concern | Choice |
|---|---|
| Language | Java 25 |
| Framework | Spring Boot 4 (Spring MVC, Bean Validation) |
| Build | Maven (wrapper included) |
| Persistence model | Jakarta Persistence (JPA) annotations, in-memory repositories |
| API docs | springdoc-openapi (Swagger UI) |
| Tests | JUnit 5, AssertJ, Spring Boot Test, JaCoCo (≥ 80% coverage gate) |
| CI | GitHub Actions |

## Architecture

Package-by-feature, with a one-way dependency flow `redirect → click → link`:

```
io.github.brunogutierre.simpleurl
├── config/    application configuration (clock, properties, OpenAPI)
├── error/     global error handling (Problem Details)
├── link/      short link entity, repository, code generation, service and API
├── click/     click entity, repository, statistics service and API
└── redirect/  public redirect endpoint
```

Design decisions and their rationale are documented as each feature lands.

## Running locally

Requirements: JDK 25.

```sh
./mvnw spring-boot:run   # starts the API on http://localhost:8080
./mvnw verify            # runs the test suite
```

## Roadmap

- [x] Project bootstrap
- [ ] CI, coverage gate and API documentation
- [ ] Link domain (entity, in-memory repository, code generator, service)
- [ ] Links REST API and redirect endpoint
- [ ] Link expiration
- [ ] Click statistics
- [ ] Complete documentation

## License

[MIT](LICENSE)
