# HW Resident System

Sistema de escritorio para la **gestión de ingresos, salidas y autorizaciones de acceso en conjuntos residenciales**. Centraliza la información de residentes, visitantes, prestadores de servicios, guardas, inmuebles y movimientos de portería mediante una minuta digital.

Desarrollado con **Java 21**, **Maven** y **Java Swing**, aplicando principios de **Clean Architecture** para separar las reglas del negocio de la interfaz y de los mecanismos de persistencia.

---

## Tabla de contenido

- [1. Contexto y propósito](#1-contexto-y-propósito)
- [2. Funcionalidades](#2-funcionalidades)
- [3. Flujo de control de acceso](#3-flujo-de-control-de-acceso)
- [4. Reglas de negocio](#4-reglas-de-negocio)
- [5. Arquitectura](#5-arquitectura)
- [6. Tecnologías](#6-tecnologías)
- [7. Estructura del proyecto](#7-estructura-del-proyecto)
- [8. Requisitos previos](#8-requisitos-previos)
- [9. Instalación y ejecución](#9-instalación-y-ejecución)
- [10. Persistencia y datos de demostración](#10-persistencia-y-datos-de-demostración)
- [11. Pruebas](#11-pruebas)
- [12. Alcance y consideraciones](#12-alcance-y-consideraciones)
- [13. Trabajo futuro](#13-trabajo-futuro)

---

## 1. Contexto y propósito

En la operación diaria de una portería residencial es necesario identificar a las personas que ingresan, conocer el inmueble que visitan, validar las autorizaciones correspondientes y conservar un registro de sus movimientos. Cuando esta información se administra de forma dispersa o manual, puede ser difícil consultar los ingresos pendientes, saber quién permanece dentro del conjunto o reconstruir el historial de una visita.

**HW Resident System** propone una herramienta de apoyo para organizar ese proceso mediante una minuta digital. El sistema permite registrar solicitudes de ingreso, gestionar autorizaciones, controlar las salidas y consultar los movimientos almacenados.

### Objetivo general

Desarrollar una aplicación que facilite el registro y seguimiento de los movimientos de acceso en un conjunto residencial, aplicando reglas de negocio explícitas y una arquitectura modular que permita mantener y ampliar el software.

### Objetivos específicos

- Administrar la información básica de residentes, visitantes, prestadores de servicios, guardas e inmuebles.
- Registrar solicitudes de ingreso y validar las condiciones para permitir o rechazar el acceso.
- Gestionar permisos anticipados asociados a un residente, una persona externa, un apartamento y una vigencia.
- Registrar la entrada y salida de las personas, identificando al guarda responsable de cada movimiento.
- Consultar quién se encuentra dentro del conjunto y revisar el historial de la minuta.
- Separar el dominio, los casos de uso, la infraestructura y la presentación para facilitar las pruebas y el mantenimiento.

---

## 2. Funcionalidades

La aplicación cuenta con una interfaz gráfica de escritorio organizada en pestañas.

| Módulo | Descripción |
|---|---|
| **Minuta** | Registra solicitudes de ingreso, muestra los movimientos del día y permite autorizar, rechazar o registrar la salida de una persona. |
| **Dentro del conjunto** | Consulta las personas que tienen un ingreso abierto y todavía no registran su salida. También permite gestionar la salida desde esta vista. |
| **Historial** | Consulta los registros de acceso y permite filtrar por documento, apartamento de destino y fecha. |
| **Permisos** | Permite otorgar y revocar autorizaciones anticipadas para personas externas. |
| **Residentes** | Registra residentes y los relaciona con un apartamento. |
| **Inmuebles** | Administra los apartamentos identificados por torre y número. |
| **Externos** | Registra visitantes y prestadores de servicios, incluyendo el tipo de servicio y los datos de empresa o labor cuando corresponda. |
| **Guardas** | Registra y consulta los guardas que pueden quedar asociados a los movimientos. |
| **Consultar permiso** | Verifica si una persona registrada puede ingresar en un momento determinado, sin crear un registro en la minuta. |

### Información que puede conservar un movimiento

Un registro de acceso contempla, entre otros datos:

- Persona que solicita el ingreso.
- Apartamento de destino.
- Motivo de la visita.
- Vehículo, cuando aplica.
- Información de correspondencia, cuando aplica.
- Guarda responsable del ingreso.
- Fecha y hora de solicitud, entrada y salida.
- Estado actual del movimiento.
- Guarda responsable de la salida, cuando esta se registra.

---

## 3. Flujo de control de acceso

El proceso de ingreso se adapta al tipo de persona y a las autorizaciones disponibles.

1. El guarda de turno se selecciona en la aplicación.
2. Se ingresa el número de documento de la persona.
3. El sistema identifica el tipo de persona y determina el siguiente paso:
   - **Residente:** se registra el ingreso a su propio apartamento.
   - **Persona externa con permiso vigente para el apartamento indicado:** se permite el ingreso directamente.
   - **Persona externa sin permiso válido:** la solicitud queda pendiente mientras se consulta la autorización del residente.
   - **Persona no registrada:** se solicita su nombre y se registra como visitante o prestador de servicios, según la información proporcionada.
4. Cuando una solicitud queda pendiente, el residente puede autorizar o rechazar el ingreso.
5. Cuando la persona ingresa, el movimiento queda en estado **Dentro**.
6. Al salir, el guarda registra la salida y el movimiento pasa a **Salida registrada**.

### Estados de un registro

La entidad `AccessRecord` controla las transiciones permitidas:

```text
                         autorizar / admitir
    EN ESPERA  -------------------------------->  DENTRO
        |                                           |
        | rechazar                                  | registrar salida
        v                                           v
    RECHAZADO                              SALIDA REGISTRADA
```

- **En espera:** la solicitud fue creada, pero requiere una decisión de autorización.
- **Dentro:** el ingreso fue aprobado y todavía no se registra la salida.
- **Rechazado:** el residente no autorizó el ingreso.
- **Salida registrada:** la persona ya ingresó y posteriormente se registró su salida.

---

## 4. Reglas de negocio

Las reglas principales se implementan en el dominio y en los casos de uso, no dependen únicamente de la interfaz gráfica.

- No se permite registrar una persona sin documento ni duplicar un documento que ya pertenezca a otra persona registrada.
- Un residente debe estar asociado a un apartamento existente.
- Un permiso debe relacionar un residente, una persona externa y el apartamento correspondiente.
- La fecha final de un permiso debe ser posterior a la fecha inicial.
- Un permiso revocado o fuera de su ventana de vigencia no habilita el ingreso.
- Un permiso solo es válido para el apartamento al que fue asociado.
- Una persona no puede tener dos movimientos abiertos simultáneamente: uno pendiente o dentro.
- No se puede registrar dos veces la entrada de un mismo movimiento.
- No se puede registrar la salida de una persona que no haya ingresado.
- No se puede registrar dos veces la salida de un movimiento.
- Las fechas de entrada y salida deben respetar el orden cronológico del proceso.
- Cada ingreso y cada salida requieren un guarda de turno válido.

Estas validaciones se apoyan en excepciones de dominio (`DomainException`) para comunicar operaciones no permitidas.

---

## 5. Arquitectura

El proyecto sigue una organización basada en **Clean Architecture**, con separación de responsabilidades entre cuatro áreas:

| Capa | Responsabilidad |
|---|---|
| **Domain** | Contiene las entidades, objetos de dominio, excepciones y contratos de repositorio. Aquí viven las reglas esenciales del negocio. |
| **Application** | Contiene los casos de uso que coordinan las operaciones del sistema, por ejemplo registrar ingresos, gestionar permisos y consultar el historial. |
| **Infrastructure** | Implementa los contratos definidos por el dominio. Actualmente incluye repositorios en memoria y persistencia local mediante archivo. |
| **Presentation** | Contiene las interfaces con las que interactúa el usuario: interfaz gráfica Swing y una alternativa básica de consola. |

### Regla de dependencias

```text
        PRESENTATION       INFRASTRUCTURE
               \             /
                \           /
                  APPLICATION
                       |
                    DOMAIN
```

La capa de dominio no depende de las demás capas. La clase `Main` funciona como **composition root**: instancia las implementaciones necesarias y conecta los casos de uso con las interfaces.

### Interfaz gráfica: patrón MVP

La presentación gráfica utiliza una separación inspirada en **Model–View–Presenter (MVP)**:

- **Views:** paneles y componentes Swing responsables de mostrar información y capturar las acciones del usuario.
- **Presenters:** coordinan las acciones de la vista, invocan los casos de uso y presentan sus resultados.
- **GuiUseCases:** agrupa las dependencias de aplicación que se entregan a la interfaz.

Los presentadores no dependen de componentes Swing. Esto permite probar su comportamiento con vistas de prueba sin abrir ventanas gráficas.

---

## 6. Tecnologías

| Tecnología | Uso |
|---|---|
| **Java 21** | Lenguaje y plataforma de desarrollo. |
| **Maven** | Gestión del proyecto, dependencias, compilación y empaquetado. |
| **Java Swing** | Construcción de la interfaz gráfica de escritorio. |
| **JUnit 5 (Jupiter)** | Pruebas automatizadas. |
| **Java Object Serialization** | Persistencia local de los datos en un archivo. |
| **Git y GitHub** | Control de versiones y colaboración. |

No se utiliza actualmente un motor de base de datos externo. La persistencia de esta versión se realiza en un archivo local.

---

## 7. Estructura del proyecto

La organización principal de paquetes es la siguiente:

```text
HW-resident-system/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   └── java/com/hwresident/
    │       ├── Main.java
    │       ├── domain/
    │       │   ├── exception/
    │       │   ├── model/
    │       │   └── repository/
    │       ├── application/
    │       │   └── usecase/
    │       ├── infrastructure/
    │       │   ├── persistence/
    │       │   └── seed/
    │       └── presentation/
    │           ├── cli/
    │           └── gui/
    │               ├── presenter/
    │               └── swing/
    └── test/
        └── java/com/hwresident/
            ├── application/
            ├── domain/
            ├── infrastructure/
            └── presentation/
```

### Componentes relevantes

- `Main.java`: configura la aplicación, selecciona el modo de ejecución y conecta las dependencias.
- `domain/model`: define entidades como `Person`, `Resident`, `Visitor`, `ServiceProvider`, `Guard`, `Apartment`, `AccessPermission` y `AccessRecord`.
- `application/usecase`: contiene las operaciones del sistema y coordina las reglas del dominio.
- `infrastructure/persistence`: implementa los repositorios en memoria y la escritura/lectura del archivo local.
- `infrastructure/seed`: proporciona datos de ejemplo para facilitar las demostraciones.
- `presentation/gui/presenter`: contiene presentadores, contratos de vista y modelos de presentación.
- `presentation/gui/swing`: contiene las ventanas, pestañas, paneles y componentes gráficos.
- `src/test`: reúne las pruebas automatizadas de dominio, aplicación, persistencia y presentación.

---

## 8. Requisitos previos

Para compilar y ejecutar el proyecto se requiere:

- **JDK 21 o superior.**
- **Apache Maven 3.9 o superior.**
- Un sistema operativo con entorno gráfico para utilizar la interfaz Swing. Si no hay entorno gráfico disponible, la aplicación puede iniciar en modo consola.

Puedes comprobar las instalaciones con:

```bash
java -version
mvn -version
```

---

## 9. Instalación y ejecución

### 9.1. Clonar el repositorio

```bash
git clone https://github.com/MaickolRivera/HW-resident-system.git
cd HW-resident-system
```

### 9.2. Ejecutar las pruebas

```bash
mvn clean test
```

### 9.3. Construir el proyecto

```bash
mvn clean package
```

El archivo ejecutable se genera dentro de la carpeta `target/`.

### 9.4. Iniciar la aplicación

**Interfaz gráfica (modo predeterminado):**

```bash
java -jar target/hw-resident-system-0.1.0-SNAPSHOT.jar
```

**Modo consola:**

```bash
java -jar target/hw-resident-system-0.1.0-SNAPSHOT.jar --cli
```

**Ejecutar sin guardar datos en disco:**

```bash
java -jar target/hw-resident-system-0.1.0-SNAPSHOT.jar --memoria
```

**Iniciar sin cargar datos de demostración:**

```bash
java -jar target/hw-resident-system-0.1.0-SNAPSHOT.jar --sin-demo
```

Los argumentos se pueden combinar. Por ejemplo, para iniciar en memoria y sin datos de demostración:

```bash
java -jar target/hw-resident-system-0.1.0-SNAPSHOT.jar --memoria --sin-demo
```

> **Nota:** el modo de consola es una alternativa básica centrada en el registro de residentes y visitantes. La funcionalidad completa del sistema se encuentra en la interfaz gráfica.

---

## 10. Persistencia y datos de demostración

En el modo predeterminado, los datos se almacenan localmente en:

```text
datos/hw-resident.dat
```

La ruta es relativa al directorio desde el que se ejecuta la aplicación. El archivo contiene la información de personas, apartamentos, permisos y registros de acceso.

La implementación `FileDatabase` utiliza repositorios en memoria conectados a una operación de guardado. Después de cada cambio, serializa los datos en un archivo temporal y reemplaza el archivo principal. Cuando el sistema vuelve a iniciarse, carga la información guardada.

Si el archivo no existe o está vacío, se pueden cargar datos de demostración automáticamente. Para comenzar con datos limpios:

1. Cierra la aplicación.
2. Elimina la carpeta `datos/`.
3. Inicia nuevamente el programa.

También puedes utilizar `--sin-demo` para evitar la precarga de datos de ejemplo.

### Documentos de prueba incluidos

En una instalación nueva con datos de demostración se encuentran, entre otros, estos registros:

| Documento | Persona de ejemplo | Uso en la demostración |
|---|---|---|
| `5001`, `5002` | Guardas de ejemplo | Seleccionar el guarda de turno. |
| `1001` | Residente de Torre 1 - 101 | Probar el ingreso directo de un residente. |
| `2001` | Visitante con permiso anticipado | Probar el ingreso con autorización vigente. |
| `2002` | Visitante sin permiso | Probar una solicitud pendiente de autorización. |
| `3001` | Persona externa de ejemplo | Consultar un movimiento que ya se encuentra dentro. |
| `3002`, `3003` | Prestadores de servicios | Probar el registro de personal externo. |

Los permisos de demostración tienen vigencias relativas al momento en que se cargan los datos; por ello, conviene reiniciar los datos de ejemplo antes de una presentación.

---

## 11. Pruebas

El proyecto incorpora pruebas automatizadas con **JUnit 5**. La suite cubre diferentes niveles de la aplicación:

- **Dominio:** validación de entidades, estados de los movimientos, permisos y restricciones de fechas.
- **Casos de uso:** registro de residentes y movimientos, autorizaciones, rechazos, salidas y consultas del historial.
- **Persistencia:** recuperación de datos después de cerrar y volver a abrir el almacenamiento local, guardado de cambios y manejo de archivos corruptos.
- **Presentación:** comportamiento de los presentadores mediante contratos de vista, sin requerir ventanas Swing reales.

Para ejecutar todas las pruebas:

```bash
mvn clean test
```

Para compilar y empaquetar el proyecto:

```bash
mvn clean package
```

---

## 12. Alcance y consideraciones

La versión actual es una aplicación de escritorio orientada a demostrar el flujo funcional de portería y la separación arquitectónica del sistema.

Aspectos importantes para interpretar correctamente su alcance:

- La persistencia es local y basada en serialización de Java; no existe todavía una base de datos centralizada.
- Los datos se almacenan en el equipo donde se ejecuta la aplicación. Esta versión no implementa sincronización entre varias porterías o equipos.
- La interfaz gráfica Swing es la presentación principal. La consola ofrece un conjunto reducido de operaciones.
- Los datos de demostración son ficticios y están pensados para probar los flujos principales.
- La aplicación no debe considerarse todavía un sistema de producción para gestionar información personal real sin incorporar controles adicionales de seguridad, respaldo, acceso y despliegue.

---

## 13. Trabajo futuro

A partir de la arquitectura actual, algunas líneas de evolución posibles son:

- Incorporar una base de datos persistente y centralizada.
- Implementar autenticación, roles y permisos de usuario para diferenciar las operaciones de administración y portería.
- Habilitar operación multiusuario y sincronización de movimientos.
- Fortalecer la auditoría de acciones y los mecanismos de respaldo y recuperación.
- Ampliar las pruebas automatizadas y añadir pruebas de integración.
- Mejorar los reportes y las opciones de exportación del historial.
- Evaluar una interfaz web o una API para facilitar futuras integraciones.

Estas mejoras son propuestas de evolución y no funcionalidades que se deban asumir como disponibles en la versión actual.

---

**Repositorio:** https://github.com/MaickolRivera/HW-resident-system
