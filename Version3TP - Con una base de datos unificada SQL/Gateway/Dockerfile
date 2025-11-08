# Imagen base con Java 21
FROM eclipse-temurin:21-jdk

# Carpeta de trabajo dentro del contenedor
WORKDIR /app

# Copiar el archivo JAR del microservicio
COPY target/*.jar app.jar

# Exponer el puerto del microservicio
EXPOSE 8080

# Comando para ejecutar el microservicio
ENTRYPOINT ["java", "-jar", "app.jar"]

