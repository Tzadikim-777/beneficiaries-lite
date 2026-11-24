
# Beneficiaries Lite

Microservicio REST para la administración de beneficiarios
bancarios, desarrollado con Java 17 y Quarkus 3.

Permite registrar, consultar y desactivar beneficiarios,
aplicando reglas de negocio, validaciones y baja lógica.

## Tecnologías

- Java 17
- Quarkus 3.x
- Maven
- Jakarta REST
- Hibernate ORM / JPA
- H2 Database
- Hibernate Validator
- Lombok
- SmallRye OpenAPI / Swagger UI
- SmallRye Health
- JUnit 5
- Mockito
- RestAssured

## Arquitectura

El proyecto implementa arquitectura hexagonal
(Puertos y Adaptadores).

### Domain

Contiene los modelos y reglas de negocio:

- Beneficiary
- BeneficiaryStatus
- ClabeValidator
- Excepciones de negocio

### Application

Contiene los casos de uso:

- CreateBeneficiaryUseCase
- GetBeneficiaryUseCase
- ListBeneficiariesUseCase
- DeactivateBeneficiaryUseCase

También incluye:

- BeneficiaryService
- BeneficiaryRepositoryPort
- DTOs de entrada y salida
- BeneficiaryResponseMapper

### Infrastructure

Contiene los adaptadores tecnológicos:

- BeneficiaryResource
- BeneficiaryPersistenceAdapter
- BeneficiaryRepository
- BeneficiaryPersistenceMapper
- BeneficiaryEntity
- GlobalExceptionMapper
- ValidationExceptionMapper

La capa de aplicación depende de interfaces,
no de implementaciones concretas de infraestructura.


## Prerrequisitos

Para compilar y ejecutar el proyecto se necesita:

- Java JDK 17
- Git
- Terminal de comandos
- Maven Wrapper (incluido en el proyecto)

No es necesario instalar Maven globalmente,
ya que el proyecto incluye Maven Wrapper.

Tampoco se requiere instalar H2, porque
utilizamos una base de datos en memoria.

## Compilación

Desde la raíz del proyecto:

### Linux / macOS / Git Bash

```bash
./mvnw clean package
```

### Windows (CMD / PowerShell)

```powershell
.\mvnw.cmd clean package
```

## Ejecución en modo desarrollo

### Linux / macOS / Git Bash

```bash
./mvnw quarkus:dev
```

### Windows (CMD / PowerShell)

```powershell
.\mvnw.cmd quarkus:dev
```

El microservicio estará disponible en:

http://localhost:8080

## Ejecución de pruebas

Para ejecutar la suite completa:

```bash
./mvnw clean verify
```

Las pruebas incluyen:

- Validación del algoritmo CLABE.
- Pruebas unitarias de BeneficiaryService.
- Validación del modelo de dominio.
- Pruebas de integración REST.
- Restricciones de unicidad y rollback.
- Límite de beneficiarios activos.
- Enmascaramiento de CLABE.

## Configuración de base de datos

El proyecto utiliza H2 en memoria.

Perfiles configurados:

- dev: base de datos beneficiaries.
- test: base de datos beneficiaries_test.
- prod: generación automática de esquema deshabilitada.

En desarrollo y pruebas, Hibernate crea
automáticamente las tablas.

Los registros se pierden al finalizar
el proceso de la aplicación.

La configuración se encuentra en:

src/main/resources/application.properties

## Documentación y monitoreo

Swagger UI:

http://localhost:8080/q/swagger-ui

Documento OpenAPI:

http://localhost:8080/q/openapi

Health Check:

http://localhost:8080/q/health



## Endpoints REST

URL base:

`http://localhost:8080/api/v1/beneficiaries`

### 1. Registrar beneficiario

**POST** `/api/v1/beneficiaries`

```bash
curl -i -X POST \
  http://localhost:8080/api/v1/beneficiaries \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "12345678",
    "alias": "Proveedor papelería",
    "holderName": "Papelería La Estrella SA de CV",
    "clabe": "032180000118359719",
    "maxAmount": 50000.00,
    "email": "pagos@laestrella.mx"
  }'
```

Respuesta exitosa: `201 Created`.

El sistema genera automáticamente:

- id
- bankCode
- status = ACTIVE
- createdAt

### 2. Consultar beneficiario por ID

**GET** `/api/v1/beneficiaries/{id}`

```bash
curl -i \
  http://localhost:8080/api/v1/beneficiaries/1
```

Respuesta exitosa: `200 OK`.

Si el beneficiario no existe: `404 Not Found`.

### 3. Listar beneficiarios activos

**GET** `/api/v1/beneficiaries?customerId={id}`

```bash
curl -i \
  "http://localhost:8080/api/v1/beneficiaries?customerId=12345678"
```

Respuesta exitosa: `200 OK`.

Solo devuelve beneficiarios con estado ACTIVE.

Si no existen beneficiarios activos, devuelve:

```json
[]
```

### 4. Desactivar beneficiario

**PATCH** `/api/v1/beneficiaries/{id}/deactivate`

```bash
curl -i -X PATCH \
  http://localhost:8080/api/v1/beneficiaries/1/deactivate
```

Respuesta exitosa: `204 No Content`.

La operación realiza una baja lógica:
cambia el estado de ACTIVE a INACTIVE.

Si no existe: `404 Not Found`.

Si ya está inactivo: `409 Conflict`.


## Reglas de negocio

El microservicio implementa las siguientes reglas:

1. El customerId debe contener exactamente 8 dígitos numéricos.
2. El alias debe tener entre 3 y 30 caracteres.
3. El nombre del titular es obligatorio y admite hasta 100 caracteres.
4. La CLABE debe contener exactamente 18 dígitos y superar la validación del dígito verificador.
5. El bankCode se obtiene de los primeros 3 dígitos de la CLABE.
6. El maxAmount debe ser mayor que cero y admitir como máximo 2 decimales.
7. El email es opcional, pero debe tener un formato válido cuando se proporciona.
8. Todo beneficiario nuevo inicia con estado ACTIVE.
9. No se permite duplicar una CLABE para el mismo cliente, independientemente del estado del beneficiario.
10. Cada cliente puede tener como máximo 10 beneficiarios activos.
11. La desactivación es lógica: cambia ACTIVE a INACTIVE sin eliminar el registro.
12. No se permite desactivar un beneficiario que ya está INACTIVE.

## Códigos de respuesta HTTP

| Código HTTP | Código interno | Descripción |
|-------------|----------------|-------------|
| 200 | — | Consulta exitosa |
| 201 | — | Beneficiario registrado |
| 204 | — | Beneficiario desactivado |
| 400 | BEN-400 | Datos inválidos o CLABE incorrecta |
| 404 | BEN-404 | Beneficiario inexistente |
| 409 | BEN-409 | CLABE duplicada o beneficiario ya inactivo |
| 422 | BEN-422 | Límite de 10 beneficiarios activos |
| 500 | BEN-500 | Error inesperado del servidor |

### Formato estándar de errores

Todos los errores controlados utilizan la siguiente estructura:

```json
{
  "code": "BEN-409",
  "message": "La CLABE ya está registrada para este cliente",
  "timestamp": "2026-11-20T10:30:00",
  "details": []
}
```

Cuando se incumplen restricciones de Bean Validation,
el arreglo details contiene los campos inválidos y sus mensajes.

## Decisiones técnicas

### Arquitectura hexagonal

Se implementó arquitectura de puertos y adaptadores
para separar las reglas de negocio de la infraestructura.

Los casos de uso dependen de interfaces, no de
implementaciones concretas de persistencia.

### Separación de modelos

Se utilizan tres representaciones diferentes:

- DTOs: entrada y salida del API.
- Beneficiary: modelo de dominio.
- BeneficiaryEntity: entidad de persistencia JPA.

Las transformaciones se realizan mediante mappers.

### Persistencia

Se utiliza Hibernate ORM con H2 en memoria.

La combinación customer_id + clabe tiene una restricción
UNIQUE en la base de datos.

Las operaciones de escritura utilizan @Transactional.

### Validación de CLABE

Se implementó el algoritmo del dígito verificador,
utilizando los pesos 3, 7 y 1 para las primeras
17 posiciones.

La validación matemática también se ejecuta
desde el constructor del modelo de dominio.

### Manejo de excepciones

Se utilizan excepciones específicas para representar
los errores de negocio.

Los ExceptionMapper convierten esas excepciones
en respuestas HTTP con una estructura uniforme.

### Seguridad de logs

Se utiliza JBoss Logging.

Las CLABEs se enmascaran mostrando solamente
sus últimos cuatro dígitos.

No se registran contraseñas ni tokens.

### Pruebas

Se implementaron pruebas unitarias con JUnit 5
y Mockito, además de pruebas de integración
con @QuarkusTest y RestAssured.

También se verifican restricciones de base
de datos y rollback transaccional.

## Supuestos y limitaciones

- Se utiliza H2 exclusivamente para desarrollo y pruebas.
- No se implementa autenticación ni autorización,
  porque no forman parte de los requisitos solicitados.
- La consulta por ID permite recuperar beneficiarios
  tanto activos como inactivos.
- La consulta por customerId devuelve una lista vacía
  cuando no existen beneficiarios activos.
- El monto máximo admite hasta 15 dígitos enteros
  y 2 decimales.
- La desactivación exitosa devuelve HTTP 204.
- La restricción UNIQUE protege frente a duplicados
  concurrentes de customerId + clabe.
- El límite de 10 beneficiarios se valida de forma
  transaccional, pero requeriría un mecanismo
  adicional de concurrencia para garantizarse
  estrictamente ante peticiones simultáneas.
- No se configura una base de datos productiva.


## Ejecución con Docker

El proyecto incluye un Dockerfile para ejecutar
el microservicio en un contenedor con Java 17.

### 1. Compilar el proyecto

```bash
./mvnw clean package
```

### 2. Construir la imagen

```bash
docker build -t beneficiaries-lite:1.0 .
```

### 3. Ejecutar el contenedor

```bash
docker run -d \
  --name beneficiaries-container \
  -p 8080:8080 \
  beneficiaries-lite:1.0
```

### 4. Verificar el funcionamiento

Health Check:

http://localhost:8080/q/health

Swagger UI:

http://localhost:8080/q/swagger-ui

### 5. Consultar los logs

```bash
docker logs beneficiaries-container
```

### 6. Detener y eliminar el contenedor

```bash
docker stop beneficiaries-container
docker rm beneficiaries-container
```

### Consideraciones

- Se utiliza Java 17 como runtime.
- La aplicación se expone en el puerto 8080.
- La imagen utiliza temporalmente el perfil dev.
- H2 se ejecuta en memoria.
- Los datos no persisten al eliminar o reiniciar
  el proceso de la aplicación.
- Esta configuración está orientada a demostración
  local, no a producción.

