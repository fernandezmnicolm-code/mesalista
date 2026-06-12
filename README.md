# MesaLista

Plataforma Web Gastronómica para Sucre, Bolivia.

## Tecnologías
- **Backend:** Java 17, Spring Boot 3.2.5
- **Frontend:** Thymeleaf, HTML5, CSS3, Bootstrap 5
- **Seguridad:** Spring Security
- **Base de Datos:** SQLite

## Despliegue en Render (Guía Rápida)

Este proyecto está configurado y optimizado para ser desplegado gratuitamente en [Render](https://render.com/).

### Pasos para desplegar:

1. **Sube este proyecto a tu repositorio de GitHub.**
   Asegúrate de incluir el archivo `Dockerfile` y `mesalista.db` (si deseas conservar los datos locales).

2. **Crea un nuevo Web Service en Render.**
   - Inicia sesión en Render y haz clic en "New" -> "Web Service".
   - Conecta tu cuenta de GitHub y selecciona el repositorio de `MesaLista`.

3. **Configuración del despliegue:**
   - **Name:** mesalista (o el nombre que prefieras).
   - **Environment:** Selecciona `Docker` (Render detectará automáticamente el archivo `Dockerfile` en la raíz).
   - **Region:** Ohio (o la más cercana).
   - **Branch:** main (o master).

4. **Variables de Entorno (Opcional pero recomendado):**
   Render asigna automáticamente el puerto a través de la variable `PORT`. El archivo `application.properties` ya está configurado con `server.port=${PORT:8080}` para escuchar correctamente. No necesitas configurar variables extra a menos que lo desees.

5. **Lanzamiento:**
   - Haz clic en **Create Web Service**.
   - Render empezará a construir el contenedor Docker (descargará dependencias de Maven, compilará el .jar y levantará el servidor). 
   - *Nota:* El primer despliegue puede tardar unos minutos.

### Sobre la Base de Datos SQLite en Render
En la capa gratuita (Free Tier) de Render, el sistema de archivos es efímero. Esto significa que si Render reinicia la instancia (por inactividad), los datos nuevos generados durante la ejecución podrían borrarse y volver al estado inicial del repositorio. 
- Dado que este es un proyecto para **Defensa Académica**, esto es beneficioso porque siempre tendrás una versión "limpia" para demostrar.
- El proyecto ya cuenta con un `DataInitializer` interno y un archivo `mesalista.db` preparado para funcionar inmediatamente.

### Ejecución Local

Si deseas probar el proyecto de forma local antes del despliegue:

```bash
# Compilar el proyecto (Windows)
.\mvnw.cmd clean package -DskipTests

# Compilar el proyecto (Mac/Linux)
./mvnw clean package -DskipTests

# Ejecutar el proyecto
.\mvnw.cmd spring-boot:run
```
