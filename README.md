# financial-app-parent

Shared Maven parent POM / BOM **and commons modules** for all Financial App backend
microservices. Centralizes every dependency and plugin version so child services stay
version-free, and hosts the shared response envelope + error infrastructure.

## What it manages

Extends `spring-boot-starter-parent` 3.4.2. Packaging: `pom` aggregator with two jar modules (no runtime service).

| Dependency | Version |
|---|---|
| Spring Boot (via spring-boot-starter-parent) | 3.4.2 |
| Spring Cloud BOM | 2024.0.1 |
| Java | 21 |
| springdoc-openapi (WebMVC + WebFlux) | 2.7.0 |
| jjwt (api / impl / jackson) | 0.12.6 |
| MinIO SDK | 8.5.11 |
| MapStruct + mapstruct-processor | 1.6.3 |
| lombok-mapstruct-binding | 0.2.0 |

Plugin management pre-configures `spring-boot-maven-plugin` (excludes Lombok from the fat jar) and `maven-compiler-plugin` (Java 21, annotation processor order: Lombok → MapStruct → lombok-mapstruct-binding).

## Rule

Child service POMs **must not** declare version numbers. All versions come from here. To bump a version, change it in this POM only.

## File distribution

```
financial-app-parent/
├── pom.xml          # BOM parent (packaging pom) + <modules> aggregator
├── commons-core/    # shared envelope + error model — framework-free except HttpStatus/Jackson
│   └── com.financialapp.commons.core
│       ├── domain/model/ shared zero-behavior domain vocabulary (IvaTreatment, PageResult)
│       ├── error/      ErrorCategory, ErrorCode (interface), DomainException (base)
│       └── response/   ApiResponse  { status, title, code, message, data }
├── commons-web/     # servlet-MVC shared web infrastructure
│   └── com.financialapp.commons.web
│       ├── error/      ApiExceptionHandler (advice base), ErrorCategoryHttpMapper (static), CommonErrorCode
│       └── openapi/    @ApiErrorCodes, ErrorCodeOperationCustomizer, OpenApiAutoConfiguration
└── commons-messaging/  # CloudEvents Kafka plumbing — DDD-layered (domain/ + infrastructure/)
    └── com.financialapp.commons.messaging
        ├── domain/
        │   ├── gateway/   OutboxGateway, ProcessedEventGateway, DomainEventMapper (ports)
        │   └── model/     OutboxRecord, EventType, CeAttributes (value objects)
        └── infrastructure/
            ├── messaging/ relay/ (OutboxRelay, OutboxEventPublisher), consume/ (IdempotentEventProcessor,
            │              CeTypeRegistry), serde/ (CloudEventSerde), header/ (CeHeaders),
            │              config/ (KafkaCloudEventConfig), error/ (StandardDlqErrorHandler)
            └── persistence/ entity/ (OutboxRecordEntity, ProcessedEventEntity — @MappedSuperclass)
```

**Structural convention.** Shared modules follow the **same DDD layering as the services** — `domain/` (ports under `domain/gateway/`, VOs under `domain/model/`) and `infrastructure/` (framework/SDK adapters) — carrying *only the layers that genuinely apply* to a library (no `web/`/`application/`: a shared module has no controllers or business workflows). `commons-messaging` confines the `io.cloudevents` SDK to `infrastructure/` and exposes only `domain/gateway` ports (DIP); gateway implementations that own a DB table live in each service's `infrastructure/gateway/`. `commons-core`/`commons-web` remain framework-light building-block modules at their current granularity. A shared library is a cross-cutting technical concern, not a bounded context — layered like a service, but never forced to carry empty layers.

| Module | Consumed by | Purpose |
|---|---|---|
| `commons-core` | all 7 services | the single `ApiResponse` envelope; `ErrorCode` abstraction implemented by every service's `DomainError` catalog; `DomainException` base caught once by the shared handler; `com.financialapp.commons.core.domain.model` shared zero-behavior domain vocabulary (`IvaTreatment`, cursor-shaped `PageResult<T>`) |
| `commons-web` | the 6 servlet services (NOT ms-gateway — WebFlux) | `@RestControllerAdvice` base with domain/validation/malformed/data-integrity/fallback handlers + `constraintMessages()` hook; OpenAPI auto-config that documents every endpoint's declared error codes (`@ApiErrorCodes`) with generated example bodies |
| `commons-messaging` | the event-driven services (producers + consumers) | CloudEvents 1.0 Kafka binding (binary mode): `domain/gateway` ports (`OutboxGateway`, `ProcessedEventGateway`, `DomainEventMapper`), VOs, and the `infrastructure/` plumbing (`OutboxRelay`, `IdempotentEventProcessor`, `CloudEventSerde`, `CeHeaders`, `StandardDlqErrorHandler`) — see `docs/specs/architecture.md` §1 and the CloudEvents Kafka design spec |

Services add the modules as versionless dependencies (managed in this BOM):

```xml
<dependency>
    <groupId>com.financialapp</groupId>
    <artifactId>commons-core</artifactId>
</dependency>
<dependency>
    <groupId>com.financialapp</groupId>
    <artifactId>commons-web</artifactId>
</dependency>
```

## How services inherit

Each microservice `pom.xml` declares:

```xml
<parent>
    <groupId>com.financialapp</groupId>
    <artifactId>financial-app-parent</artifactId>
    <version>1.0.0</version>
    <relativePath>../financial-app-parent/pom.xml</relativePath>
</parent>
```

## Install locally

Run once before building any microservice locally (builds BOM + both commons modules):

```bash
mvn install
```

`scripts/dev.sh` (parent workspace) and every service Dockerfile run this automatically.

> Architecture: `docs/specs/architecture.md` (parent workspace).

## CI/CD

| Workflow | Trigger | Does |
|---|---|---|
| `ci.yml` | PRs; push to develop/master | builds + tests BOM and commons modules via shared `parent-ci.yml` |

Reusable workflows live in the root repo `Sergio-Smirnoff/financial-app`.
