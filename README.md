# HW Resident System

Minuta digital de ingresos y salidas para conjuntos residenciales, construida con Java 21 y Clean Architecture.

## Estructura

```
com.hwresident
├── domain           # Entidades, reglas de negocio y puertos (interfaces de repositorio)
├── application      # Casos de uso
├── infrastructure   # Implementaciones técnicas
│   ├── persistence  #   Repositorios en memoria + almacenamiento local en archivo (FileDatabase)
│   └── seed         #   Datos de ejemplo para la demo (DemoData)
├── presentation     # Entrada/salida
│   ├── cli          #   Interfaz de consola (básica)
│   └── gui          #   Interfaz gráfica (Swing)
│       ├── presenter  # Presentadores, contratos de vista y view-models (sin Swing)
│       └── swing      # Vistas pasivas: paneles, ventana y componentes
└── Main             # Composition root: ensambla las dependencias
```

Regla de dependencias: `presentation` e `infrastructure` -> `application` -> `domain`.
El dominio no conoce a ninguna otra capa.

## Modelo de dominio

```
Person (abstracta: id, nombre, documento)
├── Resident        -> Apartment (vive en)
├── Guard           (registra cada movimiento)
└── ExternalPerson  (abstracta: necesita autorización)
    ├── Visitor
    └── ServiceProvider (kind: Domiciliario | Contratista | Personal de servicio, empresa, labor)

Apartment        torre + número
Vehicle          placa + tipo (objeto de valor)
AccessPermission residente -> persona externa -> apartamento, ventana de tiempo, revocable
AccessRecord     registro de la minuta: persona, apartamento destino, motivo, vehículo?, correspondencia?,
                 guarda de entrada, guarda de salida?, hora de solicitud / entrada / salida, estado
```

### Máquina de estados de `AccessRecord`

```
            admit()                 registerExit()
EN_ESPERA ───────────► DENTRO ─────────────────────► SALIDA_REGISTRADA
    │
    └── reject() ────► RECHAZADO
```

Las transiciones se validan en el dominio: no se puede registrar la entrada dos veces, dar salida a
quien no ha entrado, ni dar salida dos veces. Además, una persona no puede tener dos registros
abiertos (en espera o dentro) al mismo tiempo.

### Flujo de portería (`RegisterEntryUseCase`)

1. El guarda digita el documento.
2. **Residente** → se registra el ingreso directo a su apartamento.
3. **Externo** → se pide el apartamento destino:
   - con permiso vigente para ese apartamento → ingreso directo;
   - sin permiso → queda **EN ESPERA** y se pregunta *"¿El residente autoriza?"* → **DENTRO** o **RECHAZADO**.
4. **No registrado** → se piden nombre y tipo (y empresa/labor si es prestador); se registra en ese
   momento y sigue el flujo de externo.

## Pantallas

| Pestaña | Para qué |
|---|---|
| **Minuta** | Formulario de ingreso, movimientos del día, botones Autorizar / Rechazar / Dar salida |
| **Dentro del conjunto** | Externos con ingreso sin salida (opcionalmente residentes), tiempo dentro; se actualiza sola |
| **Historial** | Todos los registros, filtrables por documento, apartamento y "solo hoy" |
| Permisos | Autorizaciones anticipadas (otorgar / revocar) |
| Residentes, Inmuebles, Externos, Guardas | Registro de cada tipo de actor |
| Consultar permiso | Consulta rápida (no registra nada) |

El **guarda de turno** se elige arriba a la derecha y queda guardado en cada ingreso y salida.

### Interfaz gráfica (patrón MVP)

- Las **vistas** (`presentation.gui.swing`) son pasivas: leen y escriben widgets y delegan en su presentador. No importan dominio ni aplicación.
- Los **presentadores** (`presentation.gui.presenter`) invocan los casos de uso, capturan `DomainException` y le piden a la vista que muestre el resultado. No importan `javax.swing`, por eso se prueban con vistas falsas.

## Persistencia

Por defecto los datos se guardan en `datos/hw-resident.dat` (relativo a la carpeta desde donde se
ejecuta), así que sobreviven al cerrar la aplicación. `FileDatabase` serializa todo en un solo
archivo y los repositorios en memoria lo reescriben en cada cambio; el dominio no se enteró del cambio.

La primera vez (archivo vacío) se precargan **datos de ejemplo**. Para empezar de cero, borre la
carpeta `datos/`.

| Documento | Quién | Para probar |
|---|---|---|
| 5001, 5002 | Guardas Pedro Castillo, Sandra López | guarda de turno |
| 1001 | Ana Pérez, residente Torre 1 - 101 | ingreso directo de residente |
| 2001 | Carlos Ruiz, visitante con permiso de Ana (vigente 10 h desde que se cargan los datos) | ingreso con permiso previo (destino Torre 1 - 101) |
| 2002 | Laura Díaz, visitante sin permiso | pregunta "¿el residente autoriza?" |
| 3001 | Diana Torres, personal de servicio | ya está dentro: probar "Dar salida" |
| 3002, 3003 | Andrés Mora (domiciliario), Felipe Rojas (contratista) | prestadores de servicio |
| cualquier otro | — | persona no registrada: se registra al llegar |

## Requisitos

- JDK 21 o superior
- Maven 3.9+

## Comandos

```bash
mvn clean test
mvn clean package
java -jar target/hw-resident-system-0.1.0-SNAPSHOT.jar             # abre la GUI (datos en datos/)
java -jar target/hw-resident-system-0.1.0-SNAPSHOT.jar --memoria   # sin guardar en disco
java -jar target/hw-resident-system-0.1.0-SNAPSHOT.jar --sin-demo  # sin datos de ejemplo
java -jar target/hw-resident-system-0.1.0-SNAPSHOT.jar --cli       # modo consola (básico)
```

Si no hay entorno gráfico disponible, la aplicación inicia automáticamente en modo consola.
