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

### Persistence

- JPA entities (`ShortLink`) map the target tables; the schema is documented in
  [`db/migration`](src/main/resources/db/migration) as Flyway scripts (not executed).
- Only the Jakarta Persistence **API** is on the classpath (no JPA provider, no JDBC driver),
  so no `DataSource` is created and the annotations act as mapping metadata.
- Services depend on repository interfaces (`ShortLinkRepository`); the current implementations
  are thread-safe in-memory stores.

## Design decisions

| Decision | Rationale |
|---|---|
| Random 7-character Base62 codes (`SecureRandom`) | ~3.5 trillion combinations; codes are not guessable or enumerable, unlike sequential IDs. |
| Retry up to 5 times on code collision | Collisions are extremely rare; a bounded retry keeps creation simple and fails loudly if the code space is ever exhausted. |
| Uniqueness enforced by the repository (`saveIfCodeAbsent`) | Atomic `ConcurrentHashMap.putIfAbsent` mirrors the table's unique constraint and has no check-then-act race. |
| `CodeGenerator` interface | Tests use deterministic codes to cover collision handling. |
| Injected `Clock` | All timestamps come from one source, so time-based behavior is testable without sleeps. |
| Errors as `ErrorResponseException` subclasses | Spring renders them as RFC 9457 Problem Details with no extra handler code. |
| Same URL shortened twice gets two codes | Simpler than deduplication and keeps each link's statistics independent. |

## Running locally

Requirements: JDK 25.

```sh
./mvnw spring-boot:run   # starts the API on http://localhost:8080
./mvnw verify            # runs tests and the 80% coverage gate
```

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI document: http://localhost:8080/v3/api-docs
- Coverage report (after `verify`): `target/site/jacoco/index.html`
- Configuration: set `SIMPLEURL_BASE_URL` to the public address used in short URLs (default `http://localhost:8080`).

## Roadmap

- [x] Project bootstrap
- [x] CI, coverage gate and API documentation
- [x] Link domain (entity, in-memory repository, code generator, service)
- [ ] Links REST API and redirect endpoint
- [ ] Link expiration
- [ ] Click statistics
- [ ] Complete documentation

## License

[MIT](LICENSE)
