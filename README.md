# AnalizadorDeImagenes
## Descripcion
Aplicacion fullstack utilizada para procesamiento de imagenes con AI, Dirigirse a la wiki del proyecto para mas informacion relacionada a desiciones de diseño,arquitectura y explicacion de uso de la aplicacion https://github.com/juanjice/AnalizadorDeImagenes/wiki/Arquitectura.
## Stack tecnologico
Backend : Java 17, Spring Boot, Gradle , Lombok, JPA Hibernate, Flyway migraciones, Spring Open AI API , AWS SDK , Apache Kafka, Red Panda, Minio

FrontEnd : React, React Router,Vite , Css , Axios

DataBase : Postgres


## Arranque rápido (Docker Compose)

- Requisitos: Docker y Docker Compose instalados.

## 1) Variables de entorno (archivo `.env` en la raíz)
- Obligatorias: `POSTGRES_USER`-> Recomendada(postgres), `POSTGRES_PASSWORD` -> Recomendada(postgres), `S3_ACCESS_KEY` -> Recomendada(minio), `S3_SECRET_KEY` -> Recomendada(minio123), `S3_BUCKET` -> Recomendada(images) ,
`OPENAI_API_KEY` (llave de open api secreta)
- Opcionales: `SPRING_PROFILES_ACTIVE`, `DB_HOST`, `DB_PORT`, `DB_NAME`, `S3_ENDPOINT`, `S3_PUBLIC_ENDPOINT`, `S3_REGION`, `KAFKA_BOOTSTRAP` .
- Frontend(Opcional): en `frontend/image-analyzer/.env` ajustar `VITE_ANALYZE_PATH` (URL del backend, por defecto `http://localhost:8081`).

## 2) Construir e iniciar
- `docker compose build`
- `docker compose up -d`

## 3) Puertos útiles
- Backend: `http://localhost:8080`
- Frontend: `http://localhost:5173`
- MinIO: `http://localhost:9001` (S3 en `:9000`)
- Redpanda Console: `http://localhost:8085` (Kafka externo `:9094`)

Notas:
- Postgres expone `5435` en el host.
- El bucket S3 (`S3_BUCKET`) se crea automáticamente en el arranque con el nombre de bucket configurado en la variable  `S3_BUCKET` , esto lo ejecuta el servicio minio-init .
- Si cambias el puerto del backend, actualiza `VITE_ANALYZE_PATH` en el frontend.
