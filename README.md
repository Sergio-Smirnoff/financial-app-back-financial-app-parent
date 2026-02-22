# financial-app-parent

Maven BOM parent for all Financial App microservices. Centralizes and standardizes all dependency versions.

## Usage

All microservices declare this as their parent in `pom.xml`. No microservice declares dependency versions independently.

```xml
<parent>
    <groupId>com.financialapp</groupId>
    <artifactId>financial-app-parent</artifactId>
    <version>1.0.0</version>
    <relativePath>../financial-app-parent/pom.xml</relativePath>
</parent>
```

## Managed Versions

| Dependency | Version |
|---|---|
| Spring Boot | 3.4.2 |
| Spring Cloud | 2024.0.1 |
| springdoc-openapi | 2.7.0 |
| jjwt | 0.12.6 |
| MinIO SDK | 8.5.11 |
| MapStruct | 1.6.3 |
| Java | 25 |

## Install locally (required before running any microservice)

```bash
mvn install -N
```
