@echo off
echo ========================================
echo Deteniendo todos los microservicios...
echo ========================================

REM Detener todos los procesos de Maven/Spring Boot
taskkill /F /FI "WINDOWTITLE eq MS-SOLICITUDES*" 2>nul
taskkill /F /FI "WINDOWTITLE eq MS-CLIENTES*" 2>nul

echo.
echo ========================================
echo Microservicios detenidos!
echo ========================================
pause

