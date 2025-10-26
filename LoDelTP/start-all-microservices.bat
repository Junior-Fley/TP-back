@echo off
echo ========================================
echo Iniciando todos los microservicios...
echo ========================================

REM Iniciar ms-solicitudes en una nueva ventana
start "MS-SOLICITUDES (8090)" cmd /k "cd ms-solicitudes && mvnw.cmd spring-boot:run"

REM Esperar 3 segundos antes de iniciar el siguiente
timeout /t 3 /nobreak >nul

REM Iniciar ms-clientes en una nueva ventana
start "MS-CLIENTES (8091)" cmd /k "cd ms-clientes && mvnw.cmd spring-boot:run"

echo.
echo ========================================
echo Microservicios iniciados!
echo ========================================
echo.
echo MS-SOLICITUDES: http://localhost:8090/api/solicitudes
echo MS-CLIENTES: http://localhost:8091/api/clientes
echo.
echo Presiona cualquier tecla para salir...
pause >nul

