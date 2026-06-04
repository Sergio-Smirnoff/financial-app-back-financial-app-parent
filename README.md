# financial-app-parent

Shared Maven parent POM / BOM for all Financial App backend microservices. Centralizes every dependency and plugin version so child services stay version-free.

## What it manages

Extends `spring-boot-starter-parent` 3.4.2. Packaging: `pom` (no runtime artifact).

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

Single `pom.xml`, packaging `pom`. No source code, no runtime service, no port.

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

Run once before building any microservice locally:

```bash
mvn install -N
```

> Architecture: `docs/specs/architecture.md` (parent workspace).
