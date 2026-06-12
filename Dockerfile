# Build stage
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
# Prepare Maven wrapper
RUN chmod +x mvnw
# Resolve dependencies
RUN ./mvnw dependency:go-offline

COPY src ./src
RUN ./mvnw clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/mesalista-0.0.1-SNAPSHOT.jar app.jar

# Render asigna dinámicamente el puerto en $PORT pero podemos exponer el 8080 por defecto
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
