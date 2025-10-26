# Scripts para gestionar los microservicios

## 🚀 Iniciar todos los microservicios

Haz doble clic en:
```
start-all-microservices.bat
```

Esto abrirá **2 ventanas de terminal**, cada una corriendo un microservicio:
- **MS-SOLICITUDES** en puerto 8090
- **MS-CLIENTES** en puerto 8091

## 🛑 Detener todos los microservicios

Haz doble clic en:
```
stop-all-microservices.bat
```

Esto cerrará todas las ventanas de los microservicios.

## 📝 Endpoints disponibles

Una vez iniciados, puedes acceder a:

### MS-SOLICITUDES (Puerto 8090)
- GET http://localhost:8090/api/solicitudes
- GET http://localhost:8090/api/solicitudes/{id}
- POST http://localhost:8090/api/solicitudes
- PUT http://localhost:8090/api/solicitudes/{id}
- DELETE http://localhost:8090/api/solicitudes/{id}

### MS-CLIENTES (Puerto 8091)
- GET http://localhost:8091/api/clientes
- GET http://localhost:8091/api/clientes/{id}
- POST http://localhost:8091/api/clientes
- PUT http://localhost:8091/api/clientes/{id}
- DELETE http://localhost:8091/api/clientes/{id}

## 🗄️ Consolas H2

Para ver las bases de datos en memoria:
- MS-SOLICITUDES: http://localhost:8090/h2-console
- MS-CLIENTES: http://localhost:8091/h2-console

Credenciales:
- Usuario: `sa`
- Contraseña: (dejar en blanco)

## 📌 Notas

- Cada microservicio tarda aproximadamente 15-20 segundos en arrancar
- Si un microservicio falla al iniciar, verifica que el puerto no esté ocupado
- Puedes cerrar las ventanas manualmente o usar el script `stop-all-microservices.bat`
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

