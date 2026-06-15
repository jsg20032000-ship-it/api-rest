# Memoria de Despliegue - Todo List API REST

**Autor:** Jorge Suarez Gallegos  
**Proyecto:** Todo List - API REST con Spring Boot  
**Curso:** 2 DAW - CIFP La Laboral - 2025/2026  
**Fecha:** Mayo 2026

---

## Indice

1. [Descripcion del proyecto y sus componentes](#1-descripcion-del-proyecto-y-sus-componentes)
2. [Proceso de dockerizacion](#2-proceso-de-dockerizacion)
3. [Problemas encontrados y soluciones](#3-problemas-encontrados-y-soluciones)
4. [Instrucciones para reproducir el despliegue](#4-instrucciones-para-reproducir-el-despliegue)
5. [Proceso de despliegue en hosting](#5-proceso-de-despliegue-en-hosting)
6. [Enlaces](#6-enlaces)
7. [Documentacion en Docker Hub](#7-documentacion-en-docker-hub)

---

## 1. Descripcion del proyecto y sus componentes

Todo List es una aplicacion web Full Stack para la gestion de tareas personales. El proyecto esta compuesto por tres componentes principales:

| Componente | Tecnologia | Descripcion |
|---|---|---|
| **Backend** | Spring Boot 4.0.5 (Java) | API REST con autenticacion Basic Auth, sistema de roles y CRUD de tareas |
| **Frontend** | HTML5, CSS3, JavaScript | Cliente web que consume la API mediante Fetch API |
| **Base de datos** | MySQL 8.0 | Almacenamiento persistente de usuarios, tareas, categorias y etiquetas |

### Arquitectura

La aplicacion sigue el patron **MVC** (Modelo-Vista-Controlador):

- **Controladores:** reciben las peticiones HTTP del cliente (GET, POST, PUT, DELETE) y devuelven respuestas.
- **Servicios:** contienen la logica de negocio. Intermediarios entre el controller y el repository.
- **Repositorios:** comunicacion con la base de datos mediante JPA/Hibernate.
- **Modelos:** entidades JPA que representan las tablas de la base de datos.
- **DTOs:** objetos de transferencia para controlar la informacion enviada al cliente.

### Entidades del modelo de datos

| Entidad | Campos principales | Relaciones |
|---|---|---|
| \`User\` | id, username, email, password, fullname, role | OneToMany con Task |
| \`Task\` | id, title, description, createdAt, updatedAt, deadline, completed, priority | ManyToOne con User y Category, ManyToMany con Tag |
| \`Category\` | id, title | OneToMany con Task |
| \`Tag\` | id, name | ManyToMany con Task, ManyToOne con User |

### Sistema de roles

| Rol | Permisos |
|---|---|
| \`USER\` | CRUD de sus tareas, tags, ver categorias, dashboard, perfil |
| \`GESTOR\` | Todo lo de USER + CRUD de categorias |
| \`ADMIN\` | Todo lo de GESTOR + gestion de usuarios, promover/degradar roles |

---

## 2. Proceso de dockerizacion

### 2.1. Dockerfile

Se ha creado un \`Dockerfile\` en la raiz del proyecto para contenerizar el backend de Spring Boot:

\`\`\`dockerfile
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY . .
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests
CMD ["sh", "-c", "java -jar target/*.jar"]
\`\`\`

**Explicacion de cada instruccion:**

| Instruccion | Descripcion |
|---|---|
| \`FROM eclipse-temurin:17-jdk\` | Imagen base con Java 17 (JDK de Eclipse Temurin) |
| \`WORKDIR /app\` | Establece el directorio de trabajo dentro del contenedor |
| \`COPY . .\` | Copia todo el proyecto al contenedor |
| \`RUN chmod +x mvnw\` | Da permisos de ejecucion al wrapper de Maven |
| \`RUN ./mvnw clean package -DskipTests\` | Compila el proyecto y genera el archivo JAR |
| \`CMD [...]\` | Ejecuta el JAR generado. El perfil se controla mediante la variable de entorno SPRING_PROFILES_ACTIVE |

### 2.2. compose.yaml

Se ha creado un archivo \`compose.yaml\` para facilitar el despliegue local de la aplicacion completa (backend + base de datos):

\`\`\`yaml
services:
mysql:
image: mysql:8.0
environment:
MYSQL_ROOT_PASSWORD: 1234
MYSQL_DATABASE: todo_rest_daw
ports:
- "3306:3306"
volumes:
- mysql_data:/var/lib/mysql
healthcheck:
test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
interval: 10s
timeout: 5s
retries: 5

backend:
image: wolion/todo-rest:1.0
ports:
- "8080:8080"
command: >
sh -c "java -jar target/*.jar
--spring.datasource.url=jdbc:mysql://mysql:3306/todo_rest_daw?useSSL=false
--spring.datasource.username=root
--spring.datasource.password=1234
--spring.jpa.hibernate.ddl-auto=update"
depends_on:
mysql:
condition: service_healthy
restart: on-failure

volumes:
mysql_data:
\`\`\`

**Descripcion de los servicios:**

| Servicio | Imagen | Puerto | Funcion |
|---|---|---|---|
| \`mysql\` | \`mysql:8.0\` | 3306 | Base de datos MySQL con volumen persistente y healthcheck |
| \`backend\` | \`wolion/todo-rest:1.0\` | 8080 | API REST de Spring Boot con reinicio automatico en caso de fallo |

**Persistencia de datos:** se utiliza un volumen Docker (\`mysql_data\`) que garantiza que los datos de MySQL se conservan aunque se detenga o elimine el contenedor.

**Healthcheck:** el servicio MySQL incluye un healthcheck que verifica que la base de datos esta lista antes de arrancar el backend, evitando errores de conexion al inicio.

### 2.3. Perfiles de configuracion de Spring Boot

Se han configurado dos perfiles para diferenciar los entornos:

**\`application.properties\`** - Configuracion por defecto (desarrollo local con XAMPP):

\`\`\`properties
spring.application.name=todo-rest
spring.datasource.url=jdbc:mysql://localhost:3306/todo_rest_daw?useSSL=false&serverTimezone=Europe/Madrid
spring.datasource.username=root
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
server.port=\${PORT:8080}
\`\`\`

**\`application-prod.properties\`** - Configuracion de produccion (Railway):

\`\`\`properties
spring.datasource.url=jdbc:mysql://kodama.proxy.rlwy.net:52923/railway?useSSL=false&serverTimezone=Europe/Madrid
spring.datasource.username=root
spring.datasource.password=fuqQfeHMVlUiGfKZgpqUdcUvGhceyiBG
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.h2.console.enabled=false
\`\`\`

El perfil se activa mediante la variable de entorno \`SPRING_PROFILES_ACTIVE=prod\` en Render, o mediante argumentos de linea de comandos en el compose.yaml.

### 2.4. Comandos utilizados

Construccion de la imagen Docker:

\`\`\`bash
docker build -t wolion/todo-rest:1.0 .
\`\`\`

Etiquetado con tag \`latest\`:

\`\`\`bash
docker tag wolion/todo-rest:1.0 wolion/todo-rest:latest
\`\`\`

Subida a Docker Hub:

\`\`\`bash
docker login
docker push wolion/todo-rest:1.0
docker push wolion/todo-rest:latest
\`\`\`

Ejecucion local con Docker Compose:

\`\`\`bash
docker compose up -d
\`\`\`

Detener los servicios:

\`\`\`bash
docker compose down
\`\`\`

---

## 3. Problemas encontrados y soluciones

### 3.1. Hashes de contraseñas BCrypt no validos

**Problema:** los usuarios insertados mediante \`import.sql\` tenian hashes BCrypt generados externamente que no coincidian con el \`BCryptPasswordEncoder\` de Spring Security, lo que provocaba error 401 al intentar autenticarse.

**Solucion:** se registro un usuario nuevo a traves de la API (\`POST /auth/register\`), se copio el hash BCrypt generado por la aplicacion y se actualizaron las contraseñas de los usuarios del \`import.sql\` con ese hash valido.

### 3.2. Secuencias de IDs duplicados

**Problema:** al combinar datos insertados manualmente mediante \`import.sql\` (con IDs fijos 1, 2, 3...) con registros creados por la API, Hibernate intentaba usar IDs ya existentes, provocando errores de clave primaria duplicada.

**Solucion:** se actualizaron las secuencias de las tablas para que empiecen a partir del ID 100, evitando colisiones:

\`\`\`sql
UPDATE user_entity_seq SET next_val = 100;
UPDATE task_seq SET next_val = 100;
UPDATE category_seq SET next_val = 100;
\`\`\`

### 3.3. CORS entre frontend y backend

**Problema:** al desplegar el frontend en GitHub Pages y el backend en Render, el navegador bloqueaba las peticiones por la politica de Same-Origin. El frontend en \`github.io\` no podia comunicarse con el backend en \`onrender.com\`.

**Solucion:** se configuro \`CorsConfig.java\` en el backend para permitir peticiones desde los origenes del frontend:

\`\`\`java
.allowedOrigins(
"http://127.0.0.1:5500",
"http://localhost:5500",
"http://localhost:63342",
"https://jsg20032000-ship-it.github.io"
)
\`\`\`

### 3.4. Cold start en Render

**Problema:** en el plan gratuito de Render, el servidor se apaga tras un periodo de inactividad. La primera peticion despues de la inactividad tarda entre 30 segundos y 2 minutos en responder.

**Solucion:** no tiene solucion tecnica en el plan gratuito. Se recomienda acceder a la URL unos minutos antes de realizar demostraciones para que el servidor este activo.

### 3.5. Interpolacion de variables en Railway

**Problema:** al intentar insertar hashes BCrypt directamente mediante SQL en la consola de Railway, los caracteres \`$\` eran interpretados como variables del sistema, truncando el hash.

**Solucion:** se registraron los usuarios mediante la API (\`POST /auth/register\`) y posteriormente se actualizaron sus roles mediante SQL, evitando manipular directamente los hashes.

---

## 4. Instrucciones para reproducir el despliegue

### 4.1. Requisitos previos

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) instalado
- [Git](https://git-scm.com/) instalado

### 4.2. Pasos

**1. Clonar el repositorio:**

\`\`\`bash
git clone https://github.com/jsg20032000-ship-it/api-rest.git
cd api-rest
\`\`\`

**2. Ejecutar con Docker Compose:**

\`\`\`bash
docker compose up -d
\`\`\`

Esto levanta dos contenedores: MySQL (con healthcheck) y el backend de Spring Boot.

**3. Esperar a que arranque la aplicacion** (aproximadamente 1-2 minutos).

**4. Verificar que funciona:**

\`\`\`bash
curl http://localhost:8080/swagger-ui.html
\`\`\`

**5. Registrar un usuario:**

\`\`\`bash
curl -X POST http://localhost:8080/auth/register \
-H "Content-Type: application/json" \
-d '{"username":"test","email":"test@test.com","password":"1234","fullname":"Test"}'
\`\`\`

**6. Probar la API:**

\`\`\`bash
curl -u test:1234 http://localhost:8080/task
\`\`\`

**7. Acceder al frontend:**

Abrir \`cliente-todo/index.html\` en un navegador con Live Server o similar.

**8. Detener los servicios:**

\`\`\`bash
docker compose down
\`\`\`

Para eliminar tambien los datos persistidos:

\`\`\`bash
docker compose down -v
\`\`\`

### 4.3. Variables de entorno configurables

| Variable | Descripcion | Valor por defecto |
|---|---|---|
| \`SPRING_DATASOURCE_URL\` | URL de conexion a MySQL | \`jdbc:mysql://mysql:3306/todo_rest_daw\` |
| \`SPRING_DATASOURCE_USERNAME\` | Usuario de la base de datos | \`root\` |
| \`SPRING_DATASOURCE_PASSWORD\` | Contraseña de la base de datos | \`1234\` |
| \`SPRING_JPA_HIBERNATE_DDL_AUTO\` | Estrategia de creacion de tablas | \`update\` |
| \`SPRING_PROFILES_ACTIVE\` | Perfil de Spring Boot | \`prod\` |

---

## 5. Proceso de despliegue en hosting

El despliegue se ha realizado en tres servicios diferentes, uno para cada componente:

### 5.1. Base de datos - Railway

1. Crear cuenta en [railway.app](https://railway.app) con GitHub.
2. Crear un nuevo proyecto.
3. Añadir un servicio de base de datos MySQL.
4. Railway genera automaticamente las credenciales de conexion (host, puerto, usuario, contraseña, nombre de la BD).
5. Copiar las variables de conexion para configurar el backend.

**Datos de conexion proporcionados por Railway:**

| Variable | Valor |
|---|---|
| Host | \`kodama.proxy.rlwy.net\` |
| Puerto | \`52923\` |
| Usuario | \`root\` |
| Base de datos | \`railway\` |

### 5.2. Backend - Render

1. Crear cuenta en [render.com](https://render.com) con GitHub.
2. Crear un nuevo **Web Service**.
3. Conectar el repositorio de GitHub (\`api-rest\`).
4. Configurar:
    - **Runtime:** Docker
    - **Branch:** main
    - **Instance:** Free
5. Añadir la variable de entorno \`SPRING_PROFILES_ACTIVE=prod\`.
6. Pulsar **Create Web Service**.
7. Render construye la imagen Docker automaticamente a partir del \`Dockerfile\` y despliega la aplicacion.
8. Cada \`git push\` al repositorio provoca un redespliegue automatico.

### 5.3. Frontend - GitHub Pages

1. En el repositorio de GitHub, ir a **Settings > Pages**.
2. En **Source** seleccionar **Deploy from a branch**.
3. Seleccionar la rama **main** y la carpeta **/ (root)**.
4. GitHub Pages publica automaticamente el contenido estatico.
5. El frontend queda accesible en la URL de GitHub Pages.
6. En \`app.js\` se configura \`API_URL\` apuntando a la URL del backend en Render.

### 5.4. Configuracion de CORS

Para que el frontend desplegado en GitHub Pages pueda comunicarse con el backend en Render, se ha añadido el dominio de GitHub Pages a la configuracion de CORS en \`CorsConfig.java\`.

---

## 6. Enlaces

| Recurso | URL |
|---|---|
| **Frontend desplegado** | [https://jsg20032000-ship-it.github.io/api-rest/cliente-todo/index.html](https://jsg20032000-ship-it.github.io/api-rest/cliente-todo/index.html) |
| **Backend desplegado** | [https://api-rest-ur95.onrender.com](https://api-rest-ur95.onrender.com) |
| **Swagger UI** | [https://api-rest-ur95.onrender.com/swagger-ui.html](https://api-rest-ur95.onrender.com/swagger-ui.html) |
| **Imagen en Docker Hub** | [https://hub.docker.com/r/wolion/todo-rest](https://hub.docker.com/r/wolion/todo-rest) |
| **Repositorio en GitHub** | [https://github.com/jsg20032000-ship-it/api-rest](https://github.com/jsg20032000-ship-it/api-rest) |

### Usuarios de prueba

Todos los usuarios tienen la contraseña \`1234\`:

| Usuario | Rol | Permisos |
|---|---|---|
| \`admin\` | ADMIN | Gestion total (usuarios, categorias, tareas) |
| \`gestor\` | GESTOR | Gestion de categorias + funciones de usuario |
| \`user\` | USER | Gestion de tareas propias, tags y perfil |

---

## 7. Documentacion en Docker Hub

### Imagen publicada

- **Nombre:** \`wolion/todo-rest\`
- **Tags:** \`1.0\`, \`latest\`
- **URL:** [https://hub.docker.com/r/wolion/todo-rest](https://hub.docker.com/r/wolion/todo-rest)

### Descripcion

API REST para gestion de tareas personales desarrollada con Spring Boot 4.0.5 y Java. Incluye autenticacion Basic Auth, sistema de tres roles (USER, GESTOR, ADMIN), CRUD de tareas con prioridades, categorias y etiquetas, busquedas avanzadas, dashboard con estadisticas y documentacion Swagger.

### Instrucciones de uso

**Ejecucion rapida con Docker Compose:**

\`\`\`bash
git clone https://github.com/jsg20032000-ship-it/api-rest.git
cd api-rest
docker compose up -d
\`\`\`

La aplicacion estara disponible en \`http://localhost:8080\`.

**Ejecucion manual:**

\`\`\`bash
docker run -p 8080:8080 \
-e SPRING_DATASOURCE_URL=jdbc:mysql://HOST:PUERTO/BASEDATOS \
-e SPRING_DATASOURCE_USERNAME=usuario \
-e SPRING_DATASOURCE_PASSWORD=contraseña \
wolion/todo-rest:1.0
\`\`\`

### Variables de entorno necesarias

| Variable | Obligatoria | Descripcion |
|---|---|---|
| \`SPRING_DATASOURCE_URL\` | Si | URL JDBC de conexion a MySQL |
| \`SPRING_DATASOURCE_USERNAME\` | Si | Usuario de MySQL |
| \`SPRING_DATASOURCE_PASSWORD\` | Si | Contraseña de MySQL |
| \`SPRING_JPA_HIBERNATE_DDL_AUTO\` | No | Estrategia DDL (por defecto: \`update\`) |
| \`SPRING_PROFILES_ACTIVE\` | No | Perfil de Spring Boot |

### Requisitos

- Requiere una base de datos **MySQL 8.0** accesible.
- El puerto por defecto es **8080**.
- La documentacion Swagger esta disponible en \`/swagger-ui.html\`.

### Otras consideraciones

- La imagen esta basada en \`eclipse-temurin:17-jdk\`.
- El perfil de Spring Boot se controla mediante la variable de entorno \`SPRING_PROFILES_ACTIVE\`. En produccion se establece a \`prod\`.
- Para produccion se recomienda utilizar \`eclipse-temurin:17-jre\` para reducir el tamaño de la imagen.
