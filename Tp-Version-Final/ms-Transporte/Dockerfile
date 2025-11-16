# Etapa 1: Compilación
FROM --platform=linux/amd64 eclipse-temurin:21-jdk AS builder

WORKDIR /build

# Copiar el pom.xml padre al directorio padre relativo
COPY pom.xml ../pom.xml

# Copiar el pom.xml del módulo
COPY ms-Transporte/pom.xml .

# Copiar código fuente
COPY ms-Transporte/src ./src

# Instalar Maven y compilar el proyecto
RUN apt-get update && apt-get install -y maven && \
    mvn clean package -DskipTests

# Etapa 2: Imagen final
FROM --platform=linux/amd64 eclipse-temurin:21-jre

WORKDIR /app

# Copiar el JAR compilado desde la etapa de build
COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "app.jar"]
