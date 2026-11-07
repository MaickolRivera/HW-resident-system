# HW Resident System

Sistema de ingreso y permisos para conjuntos residenciales, construido con Java 21 y Clean Architecture.

## Estructura

```
com.hwresident
├── domain           # Entidades, reglas de negocio y puertos (interfaces de repositorio)
├── application      # Casos de uso
├── infrastructure   # Implementaciones técnicas (persistencia, etc.)
├── presentation     # Entrada/salida (CLI por ahora)
└── Main             # Composition root: ensambla las dependencias
```

Regla de dependencias: `presentation` e `infrastructure` -> `application` -> `domain`.
El dominio no conoce a ninguna otra capa.

## Requisitos

- JDK 21 o superior
- Maven 3.9+

## Comandos

```bash
mvn clean test
mvn clean package
java -jar target/hw-resident-system-0.1.0-SNAPSHOT.jar
```
