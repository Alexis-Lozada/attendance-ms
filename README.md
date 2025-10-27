# Attendance Microservices (Roster Backend)

Conjunto de microservicios desarrollados en **Spring Boot 3.5.6 (Java 21)** que forman parte del sistema **Attendance-App / Roster**, orientado a la gestión académica, usuarios, almacenamiento y comunicación.

---

## Arquitectura general

Estructura modular basada en microservicios:

```
attendance-ms/
├── academic-ms/     → Gestión académica
├── users-ms/        → Gestión de usuarios y autenticación
├── chat-ms/         → Mensajería entre usuarios
├── storage-ms/      → Almacenamiento de archivos
└── README.md        → Documentación general
```

Cada módulo es una aplicación Spring Boot independiente con su propia configuración y base de datos (algunas compartidas). Todos utilizan el mismo esquema de autenticación JWT.

---

## Tecnologías principales

| Componente        | Versión | Descripción                |
| ----------------- | ------- | -------------------------- |
| Java              | 21      | Lenguaje principal         |
| Spring Boot       | 3.5.6   | Framework backend          |
| PostgreSQL (Neon) | 17      | Base de datos en la nube   |
| Hibernate ORM     | 6.6     | ORM para JPA               |
| HikariCP          | -       | Pool de conexiones         |
| Maven             | 4.0     | Gestión de dependencias    |
| JWT (JJWT)        | 0.12+   | Autenticación centralizada |

---

## Bases de datos (Neon PostgreSQL)

El sistema usa dos instancias Neon:

| Tipo          | Microservicios              | Host abreviado         |
| ------------- | --------------------------- | ---------------------- |
| Académico     | `academic-ms`, `storage-ms` | `ep-polished-surf...`  |
| Usuarios/Chat | `users-ms`, `chat-ms`       | `ep-fragrant-brook...` |

Cada microservicio usa variables de entorno prefijadas (`ACADEMIC_`, `STORAGE_`, `USERS_`, `CHAT_`).

---

## Variables de entorno globales

Definir en PowerShell (Administrador) con `setx`.

### Academic / Storage

```bash
setx ACADEMIC_DB_URL "jdbc:postgresql://ep-polished-surf.../neondb?sslmode=require"
setx ACADEMIC_DB_USER "neondb_owner"
setx ACADEMIC_DB_PASSWORD "********"
setx ACADEMIC_SERVER_PORT "8082"

setx STORAGE_DB_URL "jdbc:postgresql://ep-polished-surf.../neondb?sslmode=require"
setx STORAGE_DB_USER "neondb_owner"
setx STORAGE_DB_PASSWORD "********"
setx STORAGE_SERVER_PORT "8084"
```

### Users / Chat

```bash
setx USERS_DB_URL "jdbc:postgresql://ep-fragrant-brook.../neondb?sslmode=require"
setx USERS_DB_USER "neondb_owner"
setx USERS_DB_PASSWORD "********"
setx USERS_SERVER_PORT "8081"

setx CHAT_DB_URL "jdbc:postgresql://ep-fragrant-brook.../neondb?sslmode=require"
setx CHAT_DB_USER "neondb_owner"
setx CHAT_DB_PASSWORD "********"
setx CHAT_SERVER_PORT "8085"
```

### JWT (compartido entre todos los microservicios)

```bash
setx JWT_SECRET_KEY "<clave_jwt_segura>"
setx JWT_EXPIRATION_MS "3600000"
```

Verificar:

```bash
echo $env:ACADEMIC_DB_URL
echo $env:JWT_SECRET_KEY
```

---

## Configuración de microservicios

Cada microservicio define su `application.properties` así:

```properties
spring.application.name=<nombre-ms>

spring.datasource.url=${<PREFIX>_DB_URL}
spring.datasource.username=${<PREFIX>_DB_USER}
spring.datasource.password=${<PREFIX>_DB_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver

server.port=${<PREFIX>_SERVER_PORT:<default_port>}

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# JWT Configuración global
JWT_SECRET_KEY=${JWT_SECRET_KEY}
JWT_EXPIRATION_MS=${JWT_EXPIRATION_MS:3600000}
```

---

## Ejecución local

1. Verificar variables de entorno.
2. Entrar a la carpeta del microservicio:

   ```bash
   cd academic-ms
   ```
3. Ejecutar:

   ```bash
   mvn spring-boot:run
   ```
4. Abrir en navegador: `http://localhost:<puerto>`

---

## Despliegue en Cloud Run

Ejemplo de despliegue:

```bash
gcloud run deploy <nombre-ms> \
  --image gcr.io/<proyecto>/<nombre-ms> \
  --region us-central1 \
  --allow-unauthenticated \
  --set-env-vars "<PREFIX>_DB_URL=...,<PREFIX>_DB_USER=...,<PREFIX>_DB_PASSWORD=...,<PREFIX>_SERVER_PORT=...,JWT_SECRET_KEY=...,JWT_EXPIRATION_MS=..."
```

---

## Puertos asignados

| Microservicio | Puerto | Prefijo   | Base Neon            |
| ------------- | ------ | --------- | -------------------- |
| academic-ms   | 8082   | ACADEMIC_ | ep-polished-surf...  |
| storage-ms    | 8084   | STORAGE_  | ep-polished-surf...  |
| users-ms      | 8081   | USERS_    | ep-fragrant-brook... |
| chat-ms       | 8085   | CHAT_     | ep-fragrant-brook... |

---

## Dockerfile universal (ejemplo)

```dockerfile
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE ${SERVER_PORT}
ENTRYPOINT ["java","-jar","/app/app.jar"]
```

---

## Autor

**Alexis Lozada Salinas**
Universidad Tecnológica de Querétaro (UTEQ)
Proyecto académico: *Attendance-App / Roster Backend*
