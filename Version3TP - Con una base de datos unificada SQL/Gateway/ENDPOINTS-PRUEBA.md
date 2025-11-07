# 🎯 ENDPOINTS DEL GATEWAY - GUÍA DE PRUEBA

## ✅ Gateway funcionando correctamente en puerto 8080

El error 404 en `http://localhost:8080/` es **NORMAL** - el Gateway solo responde a rutas específicas.

---

## 📋 REQUISITOS PREVIOS

Asegúrate de tener **los 3 microservicios corriendo**:

- ✅ **ms-Solicitudes** en puerto **8090**
- ✅ **ms-Rutas** en puerto **8095**
- ✅ **ms-Transporte** en puerto **8085**

---

## 🌐 ENDPOINTS A TRAVÉS DEL GATEWAY (Puerto 8080)

### 1️⃣ Microservicio de SOLICITUDES

```http
# Listar todas las solicitudes
GET http://localhost:8080/api/solicitudes/

# Obtener solicitud por ID
GET http://localhost:8080/api/solicitudes/1
GET http://localhost:8080/api/solicitudes/2

# Crear nueva solicitud
POST http://localhost:8080/api/solicitudes/
Content-Type: application/json

{
  "idCliente": 201,
  "costoEstimado": 5000.00,
  "tiempoEstimado": 5
}

# Actualizar solicitud
PUT http://localhost:8080/api/solicitudes/1
Content-Type: application/json

{
  "costoFinal": 5100.00,
  "tiempoReal": 6
}

# Eliminar solicitud
DELETE http://localhost:8080/api/solicitudes/1
```

### 2️⃣ Microservicio de RUTAS

```http
# Listar todas las rutas
GET http://localhost:8080/api/rutas/

# Obtener ruta por ID
GET http://localhost:8080/api/rutas/1
GET http://localhost:8080/api/rutas/2

# Crear nueva ruta
POST http://localhost:8080/api/rutas/
Content-Type: application/json

{
  "origen": "Buenos Aires",
  "destino": "Córdoba",
  "distancia": 700
}
```

### 3️⃣ Microservicio de CAMIONES/TRANSPORTE

```http
# Listar todos los camiones
GET http://localhost:8080/api/camiones/

# Obtener camión por ID
GET http://localhost:8080/api/camiones/1

# Crear nuevo camión
POST http://localhost:8080/api/camiones/
Content-Type: application/json

{
  "patente": "ABC123",
  "modelo": "Mercedes Benz",
  "capacidad": 15000
}
```

---

## 🔍 PRUEBAS DESDE EL NAVEGADOR

Abre tu navegador y prueba estos enlaces:

- **Solicitudes:** http://localhost:8080/api/solicitudes/
- **Rutas:** http://localhost:8080/api/rutas/
- **Camiones:** http://localhost:8080/api/camiones/

---

## 💻 PRUEBAS CON CURL (CMD de Windows)

```cmd
:: Listar solicitudes
curl http://localhost:8080/api/solicitudes/

:: Obtener solicitud específica
curl http://localhost:8080/api/solicitudes/1

:: Listar rutas
curl http://localhost:8080/api/rutas/

:: Listar camiones
curl http://localhost:8080/api/camiones/
```

---

## 📊 ACCESO DIRECTO A LOS MICROSERVICIOS (Sin Gateway)

Si necesitas acceder directamente a los microservicios:

### Solicitudes (Puerto 8090)
- http://localhost:8090/api/solicitudes/
- **Swagger:** http://localhost:8090/swagger-ui/index.html

### Rutas (Puerto 8095)
- http://localhost:8095/api/rutas/
- **Swagger:** http://localhost:8095/swagger-ui/index.html

### Transporte (Puerto 8085)
- http://localhost:8085/api/camiones/
- **Swagger:** http://localhost:8085/swagger-ui/index.html

---

## ⚙️ CONFIGURACIÓN DEL GATEWAY

El Gateway está configurado para enrutar:

| Ruta Gateway | Microservicio | Puerto |
|--------------|---------------|--------|
| `/api/solicitudes/**` | ms-Solicitudes | 8090 |
| `/api/rutas/**` | ms-Rutas | 8095 |
| `/api/camiones/**` | ms-Transporte | 8085 |

---

## 🐛 SOLUCIÓN DE PROBLEMAS

### ❌ Error 404 en `http://localhost:8080/`
✅ **NORMAL** - El Gateway no tiene ruta raíz. Usa `/api/solicitudes/`, `/api/rutas/` o `/api/camiones/`

### ❌ Error 503 Service Unavailable
❌ El microservicio destino no está corriendo. Verifica que los 3 microservicios estén activos.

### ❌ Connection refused
❌ El Gateway no está corriendo o el puerto está ocupado. Verifica que el Gateway esté en el puerto 8080.

---

## ✅ CONFIRMACIÓN DE QUE TODO FUNCIONA

1. **Gateway corriendo:** ✅ Puerto 8080 activo
2. **Error 404 en raíz:** ✅ Comportamiento esperado
3. **Siguiente paso:** Probar `http://localhost:8080/api/solicitudes/`

---

## 🎉 ¡FELICIDADES!

Tu Gateway está funcionando correctamente. Ahora puedes acceder a todos tus microservicios a través del puerto 8080.

