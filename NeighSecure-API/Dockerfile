FROM ubuntu:latest AS build

RUN apt-get update
RUN apt-get install openjdk-21-jdk -y

# Copiar todo el proyecto al contenedor
COPY . .
# Compilar el proyecto y generar el archivo .war
RUN ./gradlew bootWar --no-daemon

# Iniciar una nueva etapa de construcción
FROM openjdk:21-jdk-slim
# Exponer el puerto 8080
EXPOSE 8080

# Copiar el archivo .war de la etapa de construcción a la nueva etapa
COPY --from=build /build/libs/NeighSecure-API-0.0.1-SNAPSHOT.war app.war
# Iniciar la aplicación Spring Boot
CMD ["java", "-jar", "app.war"]
