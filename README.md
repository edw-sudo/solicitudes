Sistema de Solicitudes Administrativas
Sistema web para mejorar la gestión y seguimiento de solicitudes administrativas de estudiantes en un instituto de educación superior, Lima - 2026.
Permite a los estudiantes registrar sus trámites (constancias, rectificación de datos, documentos académicos) y hacer seguimiento a su estado; al personal administrativo, atenderlos y derivarlos; y al administrador, gestionar usuarios, tipos de solicitud y auditoría.
Tecnologías
Capa	Tecnología
Lenguaje y framework	Java 17, Spring Boot 4.1.1
Seguridad	Spring Security, BCrypt
Presentación	Spring MVC, Thymeleaf, Bootstrap 5.3.3
Persistencia	Spring Data JPA (Hibernate)
Base de datos y archivos	Supabase (PostgreSQL y Storage)
Construcción	Maven (Maven Wrapper)
Cómo ejecutarlo
Requisitos: JDK 17 y una base de datos en Supabase con las tablas del proyecto.
Clona el repositorio:
```
   git clone https://github.com/edw-sudo/solicitudes.git
   cd solicitudes
   ```
Copia `secretos.properties.example` con el nombre `secretos.properties` y completa tus datos de conexión. Este archivo no se sube al repositorio.
```
   DB_URL=jdbc:postgresql://<host-del-pooler>:5432/postgres?sslmode=require
   DB_USERNAME=postgres.<id-del-proyecto>
   DB_PASSWORD=<tu-contraseña>
   ```
Guárdalo en UTF-8 sin BOM.
Ejecuta la aplicación:
```
   mvnw spring-boot:run
   ```
Abre http://localhost:8081/login
Avance por sprint
Sprint	Semanas	Objetivo	Estado
Sprint 1	t1–t8	Autenticación, roles, usuarios, tipos de solicitud y auditoría	Terminado
Sprint 2	t9–t11	Arquitectura, estado del arte y presupuesto (documentación)	Terminado
Sprint 3	t12–t13	Registro de solicitudes, código de seguimiento y adjuntos	Pendiente
Sprint 4	t14–t15	Bandeja administrativa, estados, observaciones e historial	Pendiente
Sprint 5	t16–t17	Indicadores, pruebas y evaluación	Pendiente
Cierre	t18	Informe final, manuales y sustentación	Pendiente
Documentación
El informe del proyecto y los diagramas de arquitectura (PlantUML) están en la carpeta `docs/`.
