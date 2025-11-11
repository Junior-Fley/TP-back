# 🎯 Configuración Paso a Paso de Keycloak

## 📋 Índice
1. [Iniciar Keycloak](#1-iniciar-keycloak)
2. [Acceder a Admin Console](#2-acceder-a-admin-console)
3. [Crear Realm](#3-crear-realm)
4. [Crear Client](#4-crear-client)
5. [Crear Roles](#5-crear-roles)
6. [Crear Usuarios](#6-crear-usuarios)
7. [Verificar Configuración](#7-verificar-configuración)

---

## 1. Iniciar Keycloak

### Opción A: Usando el script
```bash
iniciar-keycloak.bat
```

### Opción B: Manualmente
```bash
docker run -d -p 8081:8080 --name keycloak-bda \
  -e KEYCLOAK_ADMIN=admin \
  -e KEYCLOAK_ADMIN_PASSWORD=admin \
  quay.io/keycloak/keycloak:latest start-dev
```

**Espera 60 segundos** para que Keycloak inicie completamente.

---

## 2. Acceder a Admin Console

1. Abre tu navegador
2. Ve a: **http://localhost:8081**
3. Haz clic en **"Administration Console"**
4. Credenciales:
   - **Username:** `admin`
   - **Password:** `admin`

![Keycloak Login](https://i.imgur.com/example.png)

---

## 3. Crear Realm

1. En el menú superior izquierdo, haz clic en **"master"** (dropdown)
2. Haz clic en **"Create Realm"**
3. Completa:
   - **Realm name:** `bda-realm`
   - **Enabled:** ✅ (activado)
4. Haz clic en **"Create"**

### ✅ Verificación
Deberías ver "bda-realm" en el dropdown superior.

---

## 4. Crear Client

1. En el menú lateral, ve a **"Clients"**
2. Haz clic en **"Create client"**

### Paso 1: General Settings
- **Client type:** `OpenID Connect`
- **Client ID:** `bda-client`
- Haz clic en **"Next"**

### Paso 2: Capability config
- **Client authentication:** ❌ **OFF** (muy importante)
- **Authorization:** ❌ OFF
- **Authentication flow:**
  - ✅ Standard flow
  - ✅ Direct access grants (habilitar)
- Haz clic en **"Next"**

### Paso 3: Login settings
- **Valid redirect URIs:** `*`
- **Valid post logout redirect URIs:** `*`
- **Web origins:** `*`
- Haz clic en **"Save"**

### ✅ Verificación
1. Ve a **"Clients"** → **"bda-client"**
2. En la pestaña **"Settings"**, verifica:
   - Client authentication: **OFF**
   - Standard flow: **Enabled**
   - Direct access grants: **Enabled**

---

## 5. Crear Roles

1. En el menú lateral, ve a **"Realm roles"**
2. Haz clic en **"Create role"**

### Crear rol CLIENTE
- **Role name:** `CLIENTE`
- **Description:** `Puede crear y consultar solicitudes de transporte`
- Haz clic en **"Save"**

### Crear rol ADMIN
- **Role name:** `ADMIN`
- **Description:** `Puede gestionar rutas, tarifas, depósitos y camiones`
- Haz clic en **"Save"**

### Crear rol TRANSPORTISTA
- **Role name:** `TRANSPORTISTA`
- **Description:** `Puede ver tramos asignados y registrar inicio/fin`
- Haz clic en **"Save"**

### ✅ Verificación
Deberías ver los 3 roles en **"Realm roles"**:
- CLIENTE
- ADMIN
- TRANSPORTISTA

---

## 6. Crear Usuarios

### 6.1 Crear Usuario CLIENTE

1. En el menú lateral, ve a **"Users"**
2. Haz clic en **"Add user"**
3. Completa:
   - **Username:** `cliente1`
   - **Email:** `cliente1@test.com`
   - **Email verified:** ✅ ON
   - **First name:** `Cliente`
   - **Last name:** `Uno`
4. Haz clic en **"Create"**

#### Configurar Password
1. Ve a la pestaña **"Credentials"**
2. Haz clic en **"Set password"**
3. Completa:
   - **Password:** `cliente123`
   - **Password confirmation:** `cliente123`
   - **Temporary:** ❌ **OFF** (importante)
4. Haz clic en **"Save"** → **"Save password"**

#### Asignar Rol
1. Ve a la pestaña **"Role mapping"**
2. Haz clic en **"Assign role"**
3. Busca y selecciona **"CLIENTE"**
4. Haz clic en **"Assign"**

### 6.2 Crear Usuario ADMIN

Repite los pasos anteriores con:
- **Username:** `admin1`
- **Email:** `admin1@test.com`
- **First name:** `Admin`
- **Last name:** `Uno`
- **Password:** `admin123`
- **Temporary:** ❌ OFF
- **Rol:** `ADMIN`

### 6.3 Crear Usuario TRANSPORTISTA

Repite los pasos con:
- **Username:** `transportista1`
- **Email:** `transportista1@test.com`
- **First name:** `Transportista`
- **Last name:** `Uno`
- **Password:** `trans123`
- **Temporary:** ❌ OFF
- **Rol:** `TRANSPORTISTA`

### ✅ Verificación de Usuarios

Para cada usuario, verifica:

1. Ve a **"Users"** → Busca el usuario
2. En **"Role mapping"** → **"Assigned roles"** debe aparecer el rol correcto
3. En **"Credentials"** → No debe aparecer el badge "Temporary"

---

## 7. Verificar Configuración

### 7.1 Verificar Realm
```bash
curl http://localhost:8081/realms/bda-realm
```

**Respuesta esperada:** JSON con información del realm

### 7.2 Obtener Token de Prueba

```bash
curl -X POST "http://localhost:8081/realms/bda-realm/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=bda-client" \
  -d "username=cliente1" \
  -d "password=cliente123" \
  -d "grant_type=password"
```

**Respuesta esperada:** JSON con `access_token`

### 7.3 Verificar Token en jwt.io

1. Copia el `access_token` de la respuesta
2. Ve a https://jwt.io
3. Pega el token
4. Verifica que en el payload aparezca:

```json
{
  "realm_access": {
    "roles": [
      "CLIENTE"
    ]
  },
  "preferred_username": "cliente1"
}
```

---

## 📊 Checklist de Configuración Completa

- [ ] Keycloak ejecutándose en http://localhost:8081
- [ ] Realm "bda-realm" creado
- [ ] Client "bda-client" creado (sin client secret)
- [ ] Roles creados: CLIENTE, ADMIN, TRANSPORTISTA
- [ ] Usuario "cliente1" creado con rol CLIENTE
- [ ] Usuario "admin1" creado con rol ADMIN
- [ ] Usuario "transportista1" creado con rol TRANSPORTISTA
- [ ] Passwords no son temporales
- [ ] Puedes obtener tokens para los 3 usuarios
- [ ] Los tokens contienen los roles correctos

---

## 🚨 Errores Comunes

### Error: "Invalid credentials"
**Causa:** Password incorrecto o temporal

**Solución:**
1. Ve a Users → {usuario} → Credentials
2. Verifica que "Temporary" esté en OFF
3. Resetea el password si es necesario

### Error: "Client not found"
**Causa:** Client ID incorrecto

**Solución:** Verifica que el client se llame exactamente `bda-client`

### Error: "Invalid redirect uri"
**Causa:** URIs no configuradas en el client

**Solución:**
1. Ve a Clients → bda-client → Settings
2. Valid redirect URIs: `*`
3. Web origins: `*`

### Error: Token sin roles
**Causa:** Roles no asignados al usuario

**Solución:**
1. Ve a Users → {usuario} → Role mapping
2. Assign role → Selecciona el rol
3. Verifica que aparezca en "Assigned roles"

---

## 🎉 ¡Configuración Completada!

Una vez completados todos los pasos, tu Keycloak está listo para autenticar los microservicios.

**Próximos pasos:**
1. Inicia los microservicios (ver README principal)
2. Importa la colección de Postman: `Postman-Collection-Keycloak.json`
3. Prueba los endpoints según la guía: `GUIA-SEGURIDAD-KEYCLOAK.md`

---

## 📚 Recursos Adicionales

- [Documentación oficial de Keycloak](https://www.keycloak.org/docs/latest/)
- [Spring Security OAuth2 Resource Server](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/index.html)
- [JWT.io - Decodificador de tokens](https://jwt.io)

---

**Documentación creada por:** Sistema de Seguridad BDA  
**Fecha:** 2025-11-09  
**Versión:** 1.0

