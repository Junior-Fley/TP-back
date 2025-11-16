@echo off
echo ========================================
echo SOLUCION DEFINITIVA - LIMPIEZA TOTAL
echo ========================================

echo.
echo [1/7] Deteniendo todos los contenedores...
docker-compose down

echo.
echo [2/7] Eliminando TODAS las imagenes de microservicios...
docker rmi -f tp-version-final-ms-solicitudes 2>nul
docker rmi -f tp-version-final-ms-rutas 2>nul
docker rmi -f tp-version-final-ms-transporte 2>nul
docker rmi -f tp-version-final-api-gateway 2>nul

echo.
echo [3/7] Limpiando cache de Maven local (si existe)...
docker run --rm -v maven-cache:/root/.m2 maven:3.9-eclipse-temurin-21 sh -c "rm -rf /root/.m2/repository/com/microservicio /root/.m2/repository/com/ms /root/.m2/repository/com/TrabajoPractico" 2>nul

echo.
echo [4/7] Eliminando cache completo de Docker...
docker builder prune -af
docker system prune -af --volumes

echo.
echo [5/7] Verificando archivos application-docker.properties...
echo.
echo === ms-Solicitudes ===
type ms-Solicitudes\src\main\resources\application-docker.properties | findstr "issuer-uri"
echo.
echo === ms-Rutas ===
type ms-Rutas\src\main\resources\application-docker.properties | findstr "issuer-uri"
echo.
echo === ms-Transporte ===
type ms-Transporte\src\main\resources\application-docker.properties | findstr "issuer-uri"
echo.

echo.
echo [6/7] Reconstruyendo TODO desde cero sin cache...
docker-compose build --no-cache --pull

echo.
echo [7/7] Iniciando todos los servicios...
docker-compose up -d

echo.
echo ========================================
echo ESPERANDO QUE LOS SERVICIOS INICIEN...
echo ========================================
timeout /t 30 >nul

echo.
echo === Estado de los contenedores ===
docker-compose ps

echo.
echo ========================================
echo PROCESO COMPLETADO
echo ========================================
echo.
echo Para verificar los logs:
echo   docker-compose logs -f ms-solicitudes
echo.
echo Para probar en Postman:
echo   1. Obtener token: POST http://localhost:8081/realms/bda-realm/protocol/openid-connect/token
echo   2. Probar API: GET http://localhost:8090/api/solicitudes
echo.
pause

