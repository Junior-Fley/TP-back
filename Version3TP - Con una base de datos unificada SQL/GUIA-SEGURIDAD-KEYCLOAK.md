# 🔐 Guía de Configuración de Seguridad con Keycloak

## 📋 Resumen de la Configuración

Se ha implementado seguridad completa con Spring Security y Keycloak para:
- **Gateway** (puerto 8080): Valida tokens y rutea a microservicios
- **ms-Rutas** (puerto 8095): Gestión de rutas, tramos, depósitos (ADMIN/TRANSPORTISTA)
- **ms-Solicitudes** (puerto 8090): Gestión de solicitudes de transporte (CLIENTE)
- **ms-Transporte** (puerto 8085): Gestión de camiones y transportistas (ADMIN/TRANSPORTISTA)

---

## 🎭 Roles Configurados

| Rol | Permisos | Endpoints |
|-----|----------|-----------|
| **CLIENTE** | Crear y consultar solicitudes de transporte | `/api/solicitudes/**` |
| **ADMIN** | Gestionar rutas, tarifas, depósitos, camiones | `/api/rutas/**`, `/api/tarifas/**`, `/api/depositos/**`, `/api/camiones/**` |
| **TRANSPORTISTA** | Ver tramos asignados, registrar inicio/fin | `/api/tramos/**`, `/api/transportistas/**` |

---

## 🔧 Configuración de Keycloak

### 1. Configurar Keycloak (si aún no lo hiciste)

```bash
# Ejecutar Keycloak en Docker
docker run -d -p 8081:8080 \
  -e KEYCLOAK_ADMIN=admin \
  -e KEYCLOAK_ADMIN_PASSWORD=admin \
  quay.io/keycloak/keycloak:latest start-dev
```

Acceder a: http://localhost:8081

### 2. Crear el Realm y Client

1. **Crear Realm:**
   - Ir a "Create Realm"
   - Nombre: `bda-realm`
   - Guardar

2. **Crear Client:**
   - Ir a "Clients" → "Create client"
   - Client ID: `bda-client`
   - Client authentication: **OFF** (sin client secret)
   - Valid redirect URIs: `*`
   - Web origins: `*`
   - Guardar

3. **Crear Roles:**
   - Ir a "Realm roles" → "Create role"
   - Crear tres roles:
     - `CLIENTE`
     - `ADMIN`
     - `TRANSPORTISTA`

4. **Crear Usuarios de Prueba:**

   **Usuario CLIENTE:**
   - Username: `cliente1`
   - Email: `cliente1@test.com`
   - Password: `cliente123` (en la pestaña "Credentials", desmarcar "Temporary")
   - Asignar rol: `CLIENTE` (en "Role mapping")

   **Usuario ADMIN:**
   - Username: `admin1`
   - Email: `admin1@test.com`
   - Password: `admin123`
   - Asignar rol: `ADMIN`

   **Usuario TRANSPORTISTA:**
   - Username: `transportista1`
   - Email: `transportista1@test.com`
   - Password: `trans123`
   - Asignar rol: `TRANSPORTISTA`

---

## 🚀 Obtener Tokens JWT desde Keycloak

### Método 1: Con cURL (Terminal)

#### Token para CLIENTE:
```bash
curl -X POST "http://localhost:8081/realms/bda-realm/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=bda-client" \
  -d "username=cliente1" \
  -d "password=cliente123" \
  -d "grant_type=password"
```

#### Token para ADMIN:
```bash
curl -X POST "http://localhost:8081/realms/bda-realm/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=bda-client" \
  -d "username=admin1" \
  -d "password=admin123" \
  -d "grant_type=password"
```

#### Token para TRANSPORTISTA:
```bash
curl -X POST "http://localhost:8081/realms/bda-realm/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=bda-client" \
  -d "username=transportista1" \
  -d "password=trans123" \
  -d "grant_type=password"
```

### Método 2: Con Postman

1. **Crear nueva request:**
   - Método: `POST`
   - URL: `http://localhost:8081/realms/bda-realm/protocol/openid-connect/token`

2. **Headers:**
   - `Content-Type`: `application/x-www-form-urlencoded`

3. **Body (x-www-form-urlencoded):**
   - `client_id`: `bda-client`
   - `username`: `cliente1` (o admin1, transportista1)
   - `password`: `cliente123` (o admin123, trans123)
   - `grant_type`: `password`

4. **Enviar la request y copiar el `access_token` de la respuesta**

**Respuesta esperada:**
```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI...",
  "expires_in": 300,
  "refresh_expires_in": 1800,
  "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI...",
  "token_type": "Bearer",
  "not-before-policy": 0,
  "session_state": "...",
  "scope": "profile email"
}
```

---

## 🧪 Probar Endpoints con Postman

### 1. Endpoint Público (Sin autenticación)

```
GET http://localhost:8080/publico/health
```
- No requiere token
- Headers: ninguno especial
- **Respuesta esperada:** `200 OK`

### 2. Endpoint para CLIENTE (Crear solicitud)

```
POST http://localhost:8080/api/solicitudes/crear
```

**Headers:**
- `Authorization`: `Bearer {access_token_de_cliente1}`
- `Content-Type`: `application/json`

**Body (JSON):**
```json
{
  "origen": "Buenos Aires",
  "destino": "Córdoba",
  "tipo_contenedor": "20ft"
}
```

**Respuesta esperada:** `200 OK` con mensaje de éxito

**❌ Si usas token de ADMIN o TRANSPORTISTA:** `403 Forbidden`

### 3. Endpoint para ADMIN (Crear ruta)

```
POST http://localhost:8080/api/rutas
```

**Headers:**
- `Authorization`: `Bearer {access_token_de_admin1}`
- `Content-Type`: `application/json`

**Body (JSON):**
```json
{
  "nombre": "Ruta Nacional 1",
  "origen": "Buenos Aires",
  "destino": "Rosario"
}
```

**Respuesta esperada:** `201 Created`

**❌ Si usas token de CLIENTE o TRANSPORTISTA:** `403 Forbidden`

### 4. Endpoint para TRANSPORTISTA (Ver mis tramos)

```
GET http://localhost:8080/api/tramos/mis-tramos
```

**Headers:**
- `Authorization`: `Bearer {access_token_de_transportista1}`

**Respuesta esperada:** `200 OK` con lista de tramos

**❌ Si usas token de CLIENTE o ADMIN:** `403 Forbidden`

### 5. Endpoint para TRANSPORTISTA (Iniciar tramo)

```
POST http://localhost:8080/api/tramos/5/iniciar
```

**Headers:**
- `Authorization`: `Bearer {access_token_de_transportista1}`

**Respuesta esperada:** `200 OK` con confirmación

### 6. Endpoint para ADMIN (Ver todos los tramos)

```
GET http://localhost:8080/api/tramos/admin/todos
```

**Headers:**
- `Authorization`: `Bearer {access_token_de_admin1}`

**Respuesta esperada:** `200 OK` con todos los tramos del sistema

---

## 📝 Ejemplos de Colección Postman

### Variables de Entorno

Crear environment en Postman con:

```json
{
  "keycloak_url": "http://localhost:8081",
  "realm": "bda-realm",
  "client_id": "bda-client",
  "gateway_url": "http://localhost:8080",
  "cliente_username": "cliente1",
  "cliente_password": "cliente123",
  "admin_username": "admin1",
  "admin_password": "admin123",
  "transportista_username": "transportista1",
  "transportista_password": "trans123",
  "access_token": ""
}
```

### Pre-request Script (para obtener token automáticamente)

En Postman, agregar este script en "Pre-request Script" de cada request:

```javascript
// Para CLIENTE
pm.sendRequest({
    url: pm.environment.get("keycloak_url") + "/realms/" + pm.environment.get("realm") + "/protocol/openid-connect/token",
    method: 'POST',
    header: {
        'Content-Type': 'application/x-www-form-urlencoded'
    },
    body: {
        mode: 'urlencoded',
        urlencoded: [
            {key: "client_id", value: pm.environment.get("client_id")},
            {key: "username", value: pm.environment.get("cliente_username")},
            {key: "password", value: pm.environment.get("cliente_password")},
            {key: "grant_type", value: "password"}
        ]
    }
}, function (err, response) {
    const jsonResponse = response.json();
    pm.environment.set("access_token", jsonResponse.access_token);
});
```

Luego en los Headers de la request:
- `Authorization`: `Bearer {{access_token}}`

---

## 🔍 Verificar el Token JWT

Puedes decodificar el token en: https://jwt.io

El payload debe contener:

```json
{
  "exp": 1699999999,
  "iat": 1699999699,
  "jti": "...",
  "iss": "http://localhost:8081/realms/bda-realm",
  "sub": "...",
  "typ": "Bearer",
  "azp": "bda-client",
  "realm_access": {
    "roles": [
      "CLIENTE"  // o ADMIN, TRANSPORTISTA según el usuario
    ]
  },
  "scope": "profile email",
  "email_verified": false,
  "preferred_username": "cliente1"
}
```

---

## ⚠️ Troubleshooting

### Error: 401 Unauthorized

**Causa:** Token inválido o expirado

**Solución:** 
- Obtener un nuevo token desde Keycloak
- Verificar que el token está en el header `Authorization: Bearer {token}`
- Verificar que Keycloak esté ejecutándose en http://localhost:8081

### Error: 403 Forbidden

**Causa:** El usuario no tiene el rol requerido

**Solución:**
- Verificar que el usuario tiene el rol correcto en Keycloak
- Decodificar el token en jwt.io y verificar el campo `realm_access.roles`
- Asegurarse de usar el token del usuario correcto (cliente1, admin1, transportista1)

### Error: CORS

**Solución:** Verificar que en Keycloak el client `bda-client` tenga configurado:
- Valid redirect URIs: `*`
- Web origins: `*`

### Keycloak no inicia

**Solución:**
```bash
# Detener contenedores
docker stop $(docker ps -q)

# Limpiar y reiniciar
docker rm $(docker ps -aq)

# Ejecutar nuevamente Keycloak
docker run -d -p 8081:8080 \
  -e KEYCLOAK_ADMIN=admin \
  -e KEYCLOAK_ADMIN_PASSWORD=admin \
  quay.io/keycloak/keycloak:latest start-dev
```

---

## 🎯 Resumen de Endpoints por Rol

| Endpoint | CLIENTE | ADMIN | TRANSPORTISTA | Público |
|----------|---------|-------|---------------|---------|
| `/publico/**` | ✅ | ✅ | ✅ | ✅ |
| `/api/solicitudes/**` | ✅ | ❌ | ❌ | ❌ |
| `/api/rutas/**` | ❌ | ✅ | ❌ | ❌ |
| `/api/depositos/**` | ❌ | ✅ | ❌ | ❌ |
| `/api/camiones/**` | ❌ | ✅ | ❌ | ❌ |
| `/api/tramos/**` | ❌ | ❌ | ✅ | ❌ |
| `/api/transportistas/**` | ❌ | ❌ | ✅ | ❌ |

---

## 📦 Archivos Creados

1. **Gateway:**
   - `SecurityConfig.java` - Configuración de seguridad reactiva
   - `application.properties` - Configuración de Keycloak

2. **ms-Rutas:**
   - `SecurityConfig.java` - Configuración de seguridad
   - `PublicoController.java` - Endpoints de ejemplo públicos
   - `application.properties` - Configuración de Keycloak

3. **ms-Solicitudes:**
   - `SecurityConfig.java` - Configuración de seguridad
   - `SolicitudEjemploController.java` - Endpoints protegidos para CLIENTE
   - `application.properties` - Configuración de Keycloak

4. **ms-Transporte:**
   - `SecurityConfig.java` - Configuración de seguridad
   - `TramoEjemploController.java` - Endpoints protegidos para TRANSPORTISTA
   - `application.properties` - Configuración de Keycloak

---

## 🚀 Iniciar el Sistema

```bash
# 1. Iniciar Keycloak
docker run -d -p 8081:8080 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:latest start-dev

# 2. Compilar todos los microservicios (desde la raíz)
mvn clean install -DskipTests

# 3. Iniciar cada microservicio
cd ms-Rutas && mvn spring-boot:run &
cd ms-Solicitudes && mvn spring-boot:run &
cd ms-Transporte && mvn spring-boot:run &
cd Gateway && mvn spring-boot:run &
```

¡Listo! Ya tienes seguridad completa con Keycloak en tu sistema de microservicios. 🎉

