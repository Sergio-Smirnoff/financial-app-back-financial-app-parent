# Commons & BOM Specification

Shared modules and parent BOM details. Shared value objects (`Money`, `Cbu`, `UserId`, `BankNumber`): parent `.ai/references/APP_STRUCTURE.md`.

## Modules

### `commons-core`
- `ApiResponse<T>`: Standard JSON response envelope (`status`, `title`, `code`, `message`, `data`).
- `ErrorCode`: Interface for service domain error codes (`getSlug()`, `getHttpStatus()`).
- `DomainError`: Contract implemented by error enums across microservices.
- Base exception types: `DomainException`, `ResourceNotFoundException`, `ResourceAlreadyExistsException`.

### `commons-web`
- `ApiExceptionHandler`: Shared `@RestControllerAdvice` base class mapping `DomainException` to `ApiResponse`.
- `@ApiErrorCodes`: OpenAPI documentation annotation declaring thrown domain error slugs for Swagger generation.

### `commons-messaging`
- CloudEvents 1.0 specification binding and deserializers.
- `OutboxRelay`: Polling relay for transactional outbox pattern (`outbox_event` table → Kafka).
- `ProcessedEventGateway`: Port for inbound message idempotency (`inbound_events` / `processed_events` table).
- `IdempotentEventProcessor`: Event handler wrapper executing `ProcessedEventGateway` deduplication checks before dispatching payload.

## BOM Version Policy

- All third-party library versions (Spring Boot 3.4.2, Spring Cloud 2024.0.1, Jackson, MapStruct, ArchUnit) are managed centrally in `financial-app-parent/pom.xml`.
- Child service POMs inherit from `financial-app-parent` and MUST NOT override versions unless explicitly approved.
