@echo off
echo ========================================
echo Limpiando contenedores y reconstruyendo
echo ========================================

echo.
echo [1/6] Deteniendo todos los contenedores...
docker-compose down

echo.
echo [2/6] Eliminando imagenes antiguas...
docker rmi -f tp-version-final-ms-transporte 2>nul
docker rmi -f tp-version-final-ms-rutas 2>nul
docker rmi -f tp-version-final-ms-solicitudes 2>nul
docker rmi -f tp-version-final-api-gateway 2>nul

echo.
echo [3/6] Eliminando volumenes...
docker volume rm tp-version-final_pgdata 2>nul
docker volume rm tp-version-final_pgadmin 2>nul
docker volume rm tp-version-final_keycloak_data 2>nul

echo.
echo [4/6] Limpiando cache de build...
docker builder prune -f

echo.
echo [5/6] Reconstruyendo imagenes con arquitectura correcta...
docker-compose build --no-cache

echo.
echo [6/6] Levantando servicios...
docker-compose up -d

echo.
echo ========================================
echo Proceso completado!
echo ========================================
echo.
echo Para ver los logs ejecuta: docker-compose logs -f
echo Para ver el estado: docker-compose ps
pause

