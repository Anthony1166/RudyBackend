# Rudy Backend (Spring Boot)

Este es el repositorio del backend para el **Proyecto Sami**, desarrollado utilizando **Spring Boot** y **Java 17**. Proporciona una API RESTful para gestionar el portafolio (proyectos, productos, categorías, imágenes, procesos de diseño) y la administración segura del panel de control.

## 🛠️ Tecnologías Utilizadas

- **Lenguaje:** Java 17
- **Framework:** Spring Boot 3.3.2
- **Base de Datos:** PostgreSQL (con compatibilidad para Supabase PgBouncer)
- **Seguridad:** Spring Security + JWT (JSON Web Tokens)
- **Almacenamiento de Archivos:** Cloudflare R2 (Compatible con AWS S3)
- **Mapeo de Datos:** ModelMapper
- **Gestión de Boilerplate:** Lombok
- **ORM:** Spring Data JPA / Hibernate

## 📂 Estructura del Proyecto

La arquitectura sigue el patrón típico de capas de Spring Boot:

```text
src/main/java/portafolio/Sami/Rudy/
├── config/         # Configuraciones generales (CORS, S3, etc.)
├── controllers/    # Controladores REST que exponen la API (/sami/...)
├── dto/            # Data Transfer Objects (Peticiones y Respuestas)
├── entities/       # Entidades JPA (Modelos de la base de datos)
├── exceptions/     # Manejo global de excepciones
├── repositories/   # Interfaces de repositorios (Spring Data JPA)
├── security/       # Filtros, servicios y configuración de Spring Security + JWT
├── services/       # Interfaces con la lógica de negocio
└── servicesImp/    # Implementaciones de los servicios
```

## 🗄️ Modelos Principales

El sistema se divide en dos dominios principales:

1. **Portafolio General:**
   - `Proyecto`
   - `ProcesoDiseno`
   - `Categoria`
   - `Imagen`

2. **Productos (`/prod`):**
   - `Producto`
   - `CategoriaProducto`
   - `ImagenProducto`
   - `ProcesoProducto`

## 🔐 Seguridad y Autenticación

El backend utiliza **JWT (JSON Web Token)** para asegurar las rutas de administración.
- Los endpoints de tipo `GET` (como consultar proyectos o productos) son públicos.
- Los endpoints de creación, modificación o eliminación (`POST`, `PUT`, `DELETE`) requieren un token Bearer en las cabeceras de la petición.
- La configuración de seguridad define a un administrador, que es validado a través de las rutas de login.

## ☁️ Almacenamiento de Imágenes

Las imágenes subidas al sistema no se guardan de forma local en producción, sino que se suben directamente a **Cloudflare R2** utilizando el SDK de `software.amazon.awssdk`. 

## 🚀 Variables de Entorno

Para levantar el entorno local, se necesitan las siguientes variables en un archivo `.env` o configuradas en tu sistema:

```env
DB_URL=jdbc:postgresql://<host>:<port>/<dbname>
DB_USERNAME=usuario_db
DB_PASSWORD=password_db
JWT_SECRET=tu_secreto_super_seguro
JWT_EXPIRATION=tiempo_en_milisegundos
AWS_BUCKET_NAME=nombre_del_bucket
AWS_ACCESS_KEY=tu_access_key
AWS_SECRET_KEY=tu_secret_key
AWS_ENDPOINT=https://<id>.r2.cloudflarestorage.com
AWS_PUBLIC_URL=url_publica_de_tus_assets
AWS_REGION=auto
```

## 🏃 Cómo ejecutar el proyecto

1. Asegúrate de tener instalado Java 17 y PostgreSQL.
2. Clona el repositorio y navega a la carpeta.
3. Puedes compilar y ejecutar el proyecto con Maven:
   ```bash
   ./mvnw spring-boot:run
   ```
4. El backend se levantará por defecto en `http://localhost:8080`.
