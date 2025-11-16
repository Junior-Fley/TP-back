# ==== build ====
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build

COPY pom.xml ./pom.xml
COPY ms-Transporte/pom.xml ms-Transporte/pom.xml
COPY ms-Transporte/src ms-Transporte/src
COPY ms-Rutas/pom.xml ms-Rutas/pom.xml
COPY ms-Rutas/src ms-Rutas/src
COPY ms-Solicitudes/pom.xml ms-Solicitudes/pom.xml
COPY ms-Solicitudes/src ms-Solicitudes/src
COPY Gateway/pom.xml Gateway/pom.xml
COPY Gateway/src Gateway/src

# Compilar solo ms-Rutas
RUN mvn -DskipTests -pl ms-Rutas -am clean package spring-boot:repackage

# ==== runtime ====
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /build/ms-Rutas/target/*.jar app.jar
EXPOSE 8095
ENTRYPOINT ["java", "-jar", "app.jar"]
