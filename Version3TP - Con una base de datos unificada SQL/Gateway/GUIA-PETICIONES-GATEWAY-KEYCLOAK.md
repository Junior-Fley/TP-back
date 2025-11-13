# 🚀 Guía Completa de Peticiones al Gateway con Keycloak

## 📋 Índice
1. [Configuración de Keycloak](#1-configuración-de-keycloak)
2. [Obtener Token de Acceso](#2-obtener-token-de-acceso)
3. [Hacer Peticiones al Gateway](#3-hacer-peticiones-al-gateway)
4. [Ejemplos por Rol](#4-ejemplos-por-rol)

---

## 1. Configuración de Keycloak

### Paso 1: Iniciar Keycloak
```bash
cd seguridad-keycloak
docker-compose up -d
```

### Paso 2: Acceder a la Consola de Administración
- URL: http://localhost:8081
- Usuario: `admin`
- Contraseña: `admin123`

### Paso 3: Crear un Realm
1. Click en el dropdown "master" → "Create Realm"
2. Nombre: `bda-realm`
3. Click en "Create"

### Paso 4: Crear los Roles
En el realm `bda-realm`:
1. Ir a **Realm roles** → **Create role**
2. Crear los siguientes roles:
   - `CLIENTE`
   - `ADMIN`
   - `TRANSPORTISTA`

### Paso 5: Crear un Cliente (aplicación)
1. Ir a **Clients** → **Create client**
2. Configurar:
   - **Client ID**: `gateway-client`
   - **Client authentication**: ON (importante)
   - **Authorization**: OFF
   - **Standard flow**: ON
   - **Direct access grants**: ON (importante para obtener tokens)
3. En la pestaña **Credentials**, copiar el **Client Secret**

### Paso 6: Crear Usuarios
1. Ir a **Users** → **Add user**
2. Crear usuarios de ejemplo:

#### Usuario Cliente
- Username: `cliente1`
- Email: `cliente1@example.com`
- Email verified: ON
- Click "Create"
- Ir a **Credentials** → Set password: `password123` (Temporary: OFF)
- Ir a **Role mapping** → Assign role → Seleccionar `CLIENTE`

#### Usuario Admin
- Username: `admin1`
- Email: `admin1@example.com`
- Email verified: ON
- Click "Create"
- Ir a **Credentials** → Set password: `admin123` (Temporary: OFF)
- Ir a **Role mapping** → Assign role → Seleccionar `ADMIN`

#### Usuario Transportista
- Username: `transportista1`
- Email: `transportista1@example.com`
- Email verified: ON
- Click "Create"
- Ir a **Credentials** → Set password: `trans123` (Temporary: OFF)
- Ir a **Role mapping** → Assign role → Seleccionar `TRANSPORTISTA`

---

## 2. Obtener Token de Acceso

Para hacer peticiones al Gateway, primero necesitas obtener un **token JWT** desde Keycloak.

### 🔑 Obtener Token (usando cURL)

```bash
curl -X POST http://localhost:8081/realms/bda-realm/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=gateway-client" \
  -d "client_secret=TU_CLIENT_SECRET_AQUI" \
  -d "username=cliente1" \
  -d "password=password123" \
  -d "grant_type=password"
```

### 🔑 Obtener Token (usando Postman)

1. **Método**: POST
2. **URL**: `http://localhost:8081/realms/bda-realm/protocol/openid-connect/token`
3. **Headers**:
   - `Content-Type`: `application/x-www-form-urlencoded`
4. **Body** (x-www-form-urlencoded):
   - `client_id`: `gateway-client`
   - `client_secret`: `[TU_CLIENT_SECRET]`
   - `username`: `cliente1`
   - `password`: `password123`
   - `grant_type`: `password`

**Respuesta**:
```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJ...",
  "expires_in": 300,
  "refresh_expires_in": 1800,
  "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJ...",
  "token_type": "Bearer"
}
```

📝 **Copia el valor de `access_token`** para usarlo en tus peticiones.

---

## 3. Hacer Peticiones al Gateway

Una vez que tengas el token, debes incluirlo en el header `Authorization` de tus peticiones.

### Formato del Header
```
Authorization: Bearer {access_token}
```

### Ejemplo completo (cURL)
```bash
curl -X GET http://localhost:8080/api/solicitudes \
  -H "Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJ..."
```

### Ejemplo en Postman
1. **Método**: GET
2. **URL**: `http://localhost:8080/api/solicitudes`
3. **Authorization**:
   - Type: `Bearer Token`
   - Token: `[PEGAR_ACCESS_TOKEN]`

---

## 4. Ejemplos por Rol

### 🔵 CLIENTE (`ROLE_CLIENTE`)

#### ✅ Endpoints permitidos:
- `/api/solicitudes/**` (GET, POST, PUT, DELETE)

#### Ejemplo 1: Listar solicitudes
```bash
GET http://localhost:8080/api/solicitudes
Authorization: Bearer {token_cliente}
```

#### Ejemplo 2: Crear una solicitud
```bash
POST http://localhost:8080/api/solicitudes
Authorization: Bearer {token_cliente}
Content-Type: application/json

{
  "clienteId": 1,
  "contenedorId": 5,
  "origen": "Buenos Aires",
  "destino": "Córdoba",
  "descripcion": "Transporte de mercadería"
}
```

#### Ejemplo 3: Consultar una solicitud específica
```bash
GET http://localhost:8080/api/solicitudes/1
Authorization: Bearer {token_cliente}
```

#### ❌ Endpoints denegados para CLIENTE:
- `/api/rutas/**` → 403 Forbidden
- `/api/camiones/**` → 403 Forbidden
- `/api/tarifas/**` → 403 Forbidden

---

### 🔴 ADMIN (`ROLE_ADMIN`)

#### ✅ Endpoints permitidos:
- `/api/rutas/**`
- `/api/tarifas/**`
- `/api/depositos/**`
- `/api/camiones/**`
- `/api/ciudades/**`
- `/api/contenedores/**`

#### Ejemplo 1: Listar rutas
```bash
GET http://localhost:8080/api/rutas
Authorization: Bearer {token_admin}
```

#### Ejemplo 2: Crear un camión
```bash
POST http://localhost:8080/api/camiones
Authorization: Bearer {token_admin}
Content-Type: application/json

{
  "patente": "ABC123",
  "modelo": "Scania R450",
  "capacidadCarga": 25000,
  "estado": "disponible"
}
```

#### Ejemplo 3: Actualizar tarifa
```bash
PUT http://localhost:8080/api/tarifas/1
Authorization: Bearer {token_admin}
Content-Type: application/json

{
  "precioPorKm": 150.50,
  "pesoMaximo": 5000
}
```

#### Ejemplo 4: Eliminar una ruta
```bash
DELETE http://localhost:8080/api/rutas/5
Authorization: Bearer {token_admin}
```

---

### 🟢 TRANSPORTISTA (`ROLE_TRANSPORTISTA`)

#### ✅ Endpoints permitidos:
- `/api/tramos/**`
- `/api/transportistas/**`

#### Ejemplo 1: Ver tramos asignados
```bash
GET http://localhost:8080/api/tramos/mis-tramos
Authorization: Bearer {token_transportista}
```

#### Ejemplo 2: Registrar inicio de tramo
```bash
POST http://localhost:8080/api/tramos/1/iniciar
Authorization: Bearer {token_transportista}
Content-Type: application/json

{
  "fechaInicio": "2025-11-12T10:30:00"
}
```

#### Ejemplo 3: Registrar fin de tramo
```bash
POST http://localhost:8080/api/tramos/1/finalizar
Authorization: Bearer {token_transportista}
Content-Type: application/json

{
  "fechaFin": "2025-11-12T15:45:00"
}
```

---

## 🛠️ Troubleshooting

### Error: "401 Unauthorized"
**Causa**: Token inválido o expirado
**Solución**: Obtén un nuevo token (los tokens expiran en 5 minutos por defecto)

### Error: "403 Forbidden"
**Causa**: Tu usuario no tiene el rol requerido para ese endpoint
**Solución**: 
- Verifica que el usuario tenga el rol correcto en Keycloak
- Usa un usuario con el rol apropiado

### Error: "No SecurityContext found"
**Causa**: El token no se está enviando correctamente
**Solución**: 
- Verifica que el header sea: `Authorization: Bearer {token}`
- Asegúrate de que no haya espacios extra

### Error: "Invalid token"
**Causa**: Problema con la configuración de Keycloak
**Solución**:
- Verifica que Keycloak esté corriendo: http://localhost:8081
- Verifica que el realm sea `bda-realm`
- Revisa los logs del Gateway

---

## 📱 Configuración de Colección Postman

### Variables de Entorno
Crear las siguientes variables:

```json
{
  "gateway_url": "http://localhost:8080",
  "keycloak_url": "http://localhost:8081",
  "realm": "bda-realm",
  "client_id": "gateway-client",
  "client_secret": "TU_CLIENT_SECRET",
  "access_token": ""
}
```

### Pre-request Script para Auto-Login
Agregar este script a nivel de colección para obtener el token automáticamente:

```javascript
const keycloakUrl = pm.environment.get("keycloak_url");
const realm = pm.environment.get("realm");
const clientId = pm.environment.get("client_id");
const clientSecret = pm.environment.get("client_secret");

// Cambiar según el usuario que quieras usar
const username = "cliente1";
const password = "password123";

pm.sendRequest({
    url: `${keycloakUrl}/realms/${realm}/protocol/openid-connect/token`,
    method: 'POST',
    header: {
        'Content-Type': 'application/x-www-form-urlencoded'
    },
    body: {
        mode: 'urlencoded',
        urlencoded: [
            { key: 'client_id', value: clientId },
            { key: 'client_secret', value: clientSecret },
            { key: 'username', value: username },
            { key: 'password', value: password },
            { key: 'grant_type', value: 'password' }
        ]
    }
}, function (err, response) {
    if (err) {
        console.error(err);
    } else {
        const jsonResponse = response.json();
        pm.environment.set("access_token", jsonResponse.access_token);
    }
});
```

Luego en cada petición, usar en Authorization:
- Type: Bearer Token
- Token: `{{access_token}}`

---

## 🔍 Ver contenido del Token (Debugging)

Puedes decodificar el JWT en: https://jwt.io

Verás algo como:
```json
{
  "realm_access": {
    "roles": ["CLIENTE", "default-roles-bda-realm", "offline_access", "uma_authorization"]
  },
  "email": "cliente1@example.com",
  "preferred_username": "cliente1",
  "exp": 1699823456
}
```

---

## 📝 Resumen de Flujo

1. **Usuario se loguea** → Keycloak genera token JWT
2. **Cliente envía petición** → Incluye token en header `Authorization`
3. **Gateway valida token** → Verifica firma JWT con Keycloak
4. **Gateway extrae roles** → Lee `realm_access.roles` del token
5. **Gateway verifica permisos** → Compara rol con endpoint solicitado
6. **Si autorizado** → Redirige al microservicio
7. **Si no autorizado** → Retorna 403 Forbidden

---

¡Listo! Ahora puedes hacer peticiones seguras a través del Gateway usando tokens de Keycloak. 🎉

