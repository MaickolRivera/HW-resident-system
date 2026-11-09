# HW Resident System

Sistema de ingreso y permisos para conjuntos residenciales, construido con Java 21 y Clean Architecture.

## Estructura

```
com.hwresident
├── domain           # Entidades, reglas de negocio y puertos (interfaces de repositorio)
├── application      # Casos de uso
├── infrastructure   # Implementaciones técnicas (persistencia, etc.)
├── presentation     # Entrada/salida
│   ├── cli          #   Interfaz de consola
│   └── gui          #   Interfaz gráfica (Swing)
│       ├── presenter  # Presentadores, contratos de vista y view-models (sin Swing)
│       └── swing      # Vistas pasivas: paneles, ventana y componentes
└── Main             # Composition root: ensambla las dependencias
```

Regla de dependencias: `presentation` e `infrastructure` -> `application` -> `domain`.
El dominio no conoce a ninguna otra capa.

### Interfaz gráfica (patrón MVP)

- Las **vistas** (`presentation.gui.swing`) son pasivas: leen y escriben widgets y delegan en su presentador. No importan dominio ni aplicación.
- Los **presentadores** (`presentation.gui.presenter`) invocan los casos de uso, capturan `DomainException` y le piden a la vista que muestre el resultado. No importan `javax.swing`, por eso se prueban con vistas falsas.

Pantallas: Residentes, Visitantes, Permisos (otorgar / revocar) y Control de acceso.

## Requisitos

- JDK 21 o superior
- Maven 3.9+

## Comandos

```bash
mvn clean test
mvn clean package
java -jar target/hw-resident-system-0.1.0-SNAPSHOT.jar          # abre la GUI
java -jar target/hw-resident-system-0.1.0-SNAPSHOT.jar --cli    # modo consola
```

Si no hay entorno gráfico disponible, la aplicación inicia automáticamente en modo consola.
