@echo off
echo ========================================
echo Configuracion de Keycloak para Sistema de Transporte
echo ========================================
echo.

echo [1/4] Iniciando Keycloak en Docker...
docker run -d -p 8081:8080 --name keycloak-bda -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:latest start-dev

echo.
echo [2/4] Esperando que Keycloak inicie (60 segundos)...
timeout /t 60 /nobreak

echo.
echo [3/4] Keycloak esta listo!
echo.
echo ========================================
echo Accede a Keycloak Admin Console:
echo URL: http://localhost:8081
echo Usuario: admin
echo Password: admin
echo ========================================
echo.
echo [4/4] Configuracion Manual Requerida:
echo.
echo 1. Crear Realm: bda-realm
echo 2. Crear Client: bda-client (sin client secret)
echo 3. Crear Roles: CLIENTE, ADMIN, TRANSPORTISTA
echo 4. Crear Usuarios:
echo    - cliente1 / cliente123 (rol: CLIENTE)
echo    - admin1 / admin123 (rol: ADMIN)
echo    - transportista1 / trans123 (rol: TRANSPORTISTA)
echo.
echo Sigue la guia en GUIA-SEGURIDAD-KEYCLOAK.md para mas detalles
echo.
pause

