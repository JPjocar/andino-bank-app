# Andino Bank

Aplicacion bancaria minima con Angular y Spring Boot.

## PostgreSQL local

Crea la base de datos `andino` y configura estas variables antes de iniciar la API:

```text
DB_URL=jdbc:postgresql://localhost:5432/andino
DB_USERNAME=postgres
DB_PASSWORD=tu-password
FRONTEND_URL=http://localhost:4200
```

La API valida el esquema existente y Flyway gestiona las migraciones.

Para cargar las cuentas de demostracion, inicia la API con el perfil `dev`:

```text
$env:SPRING_PROFILES_ACTIVE = 'dev'
.\mvnw.cmd spring-boot:run
```

Las tablas son creadas por Flyway desde `src/main/resources/db/migration`.

## Iniciar

Terminal 1, API:

```text
cd bank-api
.\mvnw.cmd spring-boot:run
```

Terminal 2, frontend:

```text
cd andino-app
npm.cmd start
```

Abre `http://localhost:4200`.

## Pruebas

```text
cd bank-api
.\mvnw.cmd test

cd ..\andino-app
npm.cmd test -- --watch=false
npm.cmd run build
```

Las pruebas unitarias puras no requieren una base de datos. Los tests de contexto,
seed e integración usan PostgreSQL mediante Testcontainers y requieren Docker Desktop.

La prueba `BankApiIntegrationIT` usa PostgreSQL real mediante Testcontainers,
crea sus datos durante cada caso y requiere Docker Desktop:

```text
cd bank-api
.\mvnw.cmd -Dit.test=BankApiIntegrationIT verify
```
