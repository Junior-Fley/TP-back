# 🧪 Ejemplos de Pruebas con cURL

## 📌 Prerequisitos
- Keycloak ejecutándose en http://localhost:8081
- Todos los microservicios iniciados
- Gateway ejecutándose en http://localhost:8080
- Usuarios configurados en Keycloak

---

## 1️⃣ OBTENER TOKENS

### Token para CLIENTE
```bash
curl -X POST "http://localhost:8081/realms/bda-realm/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=bda-client" \
  -d "username=cliente1" \
  -d "password=cliente123" \
  -d "grant_type=password" | jq -r '.access_token'
```

### Token para ADMIN
```bash
curl -X POST "http://localhost:8081/realms/bda-realm/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=bda-client" \
  -d "username=admin1" \
  -d "password=admin123" \
  -d "grant_type=password" | jq -r '.access_token'
```

### Token para TRANSPORTISTA
```bash
curl -X POST "http://localhost:8081/realms/bda-realm/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=bda-client" \
  -d "username=transportista1" \
  -d "password=trans123" \
  -d "grant_type=password" | jq -r '.access_token'
```

### Guardar tokens en variables (Windows CMD)
```bash
set TOKEN_CLIENTE=eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldU...
set TOKEN_ADMIN=eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldU...
set TOKEN_TRANSPORTISTA=eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldU...
```

---

## 2️⃣ ENDPOINTS PÚBLICOS (Sin Token)

### Health Check
```bash
curl -X GET "http://localhost:8080/publico/health"
```

**Respuesta esperada:**
```json
{
  "status": "UP",
  "service": "ms-rutas",
  "message": "Endpoint público - accesible sin autenticación"
}
```

### Info del Sistema
```bash
curl -X GET "http://localhost:8080/publico/info"
```

---

## 3️⃣ PRUEBAS CON ROL CLIENTE

### ✅ Crear Solicitud (DEBE FUNCIONAR)
```bash
curl -X POST "http://localhost:8080/api/solicitudes/crear" \
  -H "Authorization: Bearer %TOKEN_CLIENTE%" \
  -H "Content-Type: application/json" \
  -d "{\"origen\":\"Buenos Aires\",\"destino\":\"Cordoba\",\"tipo_contenedor\":\"20ft\"}"
```

**Respuesta esperada:** `200 OK`

### ✅ Ver Mis Solicitudes (DEBE FUNCIONAR)
```bash
curl -X GET "http://localhost:8080/api/solicitudes/mis-solicitudes" \
  -H "Authorization: Bearer %TOKEN_CLIENTE%"
```

### ❌ Intentar Crear Ruta (DEBE FALLAR - 403 Forbidden)
```bash
curl -X POST "http://localhost:8080/api/rutas" \
  -H "Authorization: Bearer %TOKEN_CLIENTE%" \
  -H "Content-Type: application/json" \
  -d "{\"nombre\":\"Ruta Test\"}"
```

**Respuesta esperada:** `403 Forbidden`

### ❌ Intentar Ver Tramos (DEBE FALLAR - 403 Forbidden)
```bash
curl -X GET "http://localhost:8080/api/tramos/mis-tramos" \
  -H "Authorization: Bearer %TOKEN_CLIENTE%"
```

---

## 4️⃣ PRUEBAS CON ROL ADMIN

### ✅ Ver Todas las Rutas (DEBE FUNCIONAR)
```bash
curl -X GET "http://localhost:8080/api/rutas" \
  -H "Authorization: Bearer %TOKEN_ADMIN%"
```

### ✅ Crear Ruta (DEBE FUNCIONAR)
```bash
curl -X POST "http://localhost:8080/api/rutas" \
  -H "Authorization: Bearer %TOKEN_ADMIN%" \
  -H "Content-Type: application/json" \
  -d "{\"nombre\":\"Ruta Nacional 1\",\"origen\":\"Buenos Aires\",\"destino\":\"Rosario\"}"
```

**Respuesta esperada:** `201 Created`

### ✅ Ver Todos los Depósitos (DEBE FUNCIONAR)
```bash
curl -X GET "http://localhost:8080/api/depositos" \
  -H "Authorization: Bearer %TOKEN_ADMIN%"
```

### ✅ Ver Todos los Tramos (DEBE FUNCIONAR)
```bash
curl -X GET "http://localhost:8080/api/tramos/admin/todos" \
  -H "Authorization: Bearer %TOKEN_ADMIN%"
```

### ❌ Intentar Crear Solicitud como Cliente (DEBE FALLAR - 403)
```bash
curl -X POST "http://localhost:8080/api/solicitudes/crear" \
  -H "Authorization: Bearer %TOKEN_ADMIN%" \
  -H "Content-Type: application/json" \
  -d "{\"origen\":\"Buenos Aires\"}"
```

---

## 5️⃣ PRUEBAS CON ROL TRANSPORTISTA

### ✅ Ver Mis Tramos Asignados (DEBE FUNCIONAR)
```bash
curl -X GET "http://localhost:8080/api/tramos/mis-tramos" \
  -H "Authorization: Bearer %TOKEN_TRANSPORTISTA%"
```

**Respuesta esperada:** `200 OK` con lista de tramos

### ✅ Iniciar Tramo (DEBE FUNCIONAR)
```bash
curl -X POST "http://localhost:8080/api/tramos/5/iniciar" \
  -H "Authorization: Bearer %TOKEN_TRANSPORTISTA%"
```

**Respuesta esperada:**
```json
{
  "tramoId": 5,
  "usuario": "transportista1",
  "accion": "Inicio de tramo",
  "fechaInicio": "2025-11-09T...",
  "message": "Tramo iniciado exitosamente"
}
```

### ✅ Finalizar Tramo (DEBE FUNCIONAR)
```bash
curl -X POST "http://localhost:8080/api/tramos/5/finalizar" \
  -H "Authorization: Bearer %TOKEN_TRANSPORTISTA%"
```

### ❌ Intentar Crear Ruta (DEBE FALLAR - 403)
```bash
curl -X POST "http://localhost:8080/api/rutas" \
  -H "Authorization: Bearer %TOKEN_TRANSPORTISTA%" \
  -H "Content-Type: application/json" \
  -d "{\"nombre\":\"Ruta Test\"}"
```

### ❌ Intentar Crear Solicitud (DEBE FALLAR - 403)
```bash
curl -X POST "http://localhost:8080/api/solicitudes/crear" \
  -H "Authorization: Bearer %TOKEN_TRANSPORTISTA%" \
  -H "Content-Type: application/json" \
  -d "{\"origen\":\"Buenos Aires\"}"
```

---

## 6️⃣ PRUEBAS SIN TOKEN (Deben Fallar)

### ❌ Intentar Ver Rutas sin Token (DEBE FALLAR - 401)
```bash
curl -X GET "http://localhost:8080/api/rutas"
```

**Respuesta esperada:** `401 Unauthorized`

### ❌ Intentar Crear Solicitud sin Token (DEBE FALLAR - 401)
```bash
curl -X POST "http://localhost:8080/api/solicitudes/crear" \
  -H "Content-Type: application/json" \
  -d "{\"origen\":\"Buenos Aires\"}"
```

---

## 7️⃣ VERIFICAR TOKEN JWT

### Decodificar Token
Copia el token y pégalo en: https://jwt.io

Deberías ver algo como:

**Header:**
```json
{
  "alg": "RS256",
  "typ": "JWT",
  "kid": "..."
}
```

**Payload:**
```json
{
  "exp": 1699999999,
  "iat": 1699999699,
  "iss": "http://localhost:8081/realms/bda-realm",
  "sub": "...",
  "preferred_username": "cliente1",
  "realm_access": {
    "roles": [
      "CLIENTE"
    ]
  }
}
```

---

## 8️⃣ TABLA DE RESULTADOS ESPERADOS

| Endpoint | CLIENTE | ADMIN | TRANSPORTISTA | Sin Token |
|----------|---------|-------|---------------|-----------|
| `GET /publico/health` | ✅ 200 | ✅ 200 | ✅ 200 | ✅ 200 |
| `POST /api/solicitudes/crear` | ✅ 200 | ❌ 403 | ❌ 403 | ❌ 401 |
| `GET /api/solicitudes/mis-solicitudes` | ✅ 200 | ❌ 403 | ❌ 403 | ❌ 401 |
| `GET /api/rutas` | ❌ 403 | ✅ 200 | ❌ 403 | ❌ 401 |
| `POST /api/rutas` | ❌ 403 | ✅ 201 | ❌ 403 | ❌ 401 |
| `GET /api/depositos` | ❌ 403 | ✅ 200 | ❌ 403 | ❌ 401 |
| `GET /api/tramos/mis-tramos` | ❌ 403 | ❌ 403 | ✅ 200 | ❌ 401 |
| `POST /api/tramos/5/iniciar` | ❌ 403 | ❌ 403 | ✅ 200 | ❌ 401 |
| `GET /api/tramos/admin/todos` | ❌ 403 | ✅ 200 | ❌ 403 | ❌ 401 |

✅ = Debe funcionar | ❌ = Debe fallar con código indicado

---

## 🔍 Troubleshooting

### Error: "Could not resolve host"
**Solución:** Verifica que Keycloak esté ejecutándose en http://localhost:8081

```bash
curl http://localhost:8081/realms/bda-realm
```

### Error: 401 Unauthorized
**Causas posibles:**
1. Token expirado (vida útil: 5 minutos por defecto)
2. Token no incluido en el header
3. Formato incorrecto del header (debe ser `Bearer {token}`)

**Solución:** Obtén un nuevo token

### Error: 403 Forbidden
**Causa:** El usuario no tiene el rol requerido

**Solución:** Verifica los roles en Keycloak:
1. Accede a Keycloak Admin Console
2. Ve a Users → Busca el usuario
3. Role mapping → Verifica que tenga el rol correcto

### Error: Connection refused
**Causa:** Microservicio no está ejecutándose

**Solución:**
```bash
# Verificar servicios
netstat -an | findstr "8080 8085 8090 8095"
```

---

## 📊 Resumen de Configuración

```
✅ Keycloak: http://localhost:8081
✅ Gateway: http://localhost:8080
✅ ms-Rutas: http://localhost:8095
✅ ms-Solicitudes: http://localhost:8090
✅ ms-Transporte: http://localhost:8085

✅ Realm: bda-realm
✅ Client: bda-client
✅ Roles: CLIENTE, ADMIN, TRANSPORTISTA

✅ Usuarios:
   - cliente1 / cliente123 → CLIENTE
   - admin1 / admin123 → ADMIN
   - transportista1 / trans123 → TRANSPORTISTA
```

---

¡Listo para probar! 🚀

