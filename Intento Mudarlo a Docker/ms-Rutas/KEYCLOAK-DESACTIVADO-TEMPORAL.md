# 🔓 Keycloak Temporalmente Desactivado

## ⚠️ Estado Actual

Keycloak está **DESACTIVADO** en el microservicio ms-Rutas para facilitar las pruebas de desarrollo sin necesidad de autenticación.

---

## 🚀 Cambios Realizados

### 1. **SecurityConfig.java** - Configuración simplificada
- ✅ Eliminada configuración OAuth2 Resource Server
- ✅ Todos los endpoints permiten acceso sin autenticación
- ✅ La configuración completa de Keycloak está comentada al final del archivo

### 2. **application.properties** - Propiedades comentadas
```properties
# ⚠️ KEYCLOAK TEMPORALMENTE DESACTIVADO
# spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8081/realms/bda-realm
# spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost:8081/realms/bda-realm/protocol/openid-connect/certs
# logging.level.org.springframework.security=DEBUG
```

### 3. **Controladores** - @PreAuthorize comentados
- ✅ `RutasTentativasController.java` - Sin validación de roles
- ✅ `RutasController.java` - Endpoint crear-desde-tentativa sin autenticación

### 4. **pruebas-rutas-tentativas.http** - Sin headers de autorización
- ✅ Todas las peticiones funcionan sin token JWT

---

## 🔒 Cómo REACTIVAR Keycloak

Cuando quieras volver a activar la seguridad con Keycloak, sigue estos pasos:

### Paso 1: Reactivar SecurityConfig.java

Editar: `ms-Rutas/src/main/java/com/microservicio/rutas/config/SecurityConfig.java`

```java
// 1. COMENTAR la configuración simple (líneas 30-48):
/*
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            );
        return http.build();
    }
    // ...
}
*/

// 2. DESCOMENTAR la configuración completa con Keycloak (líneas 70-157)
// Busca el bloque comentado que empieza con:
/*
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;
    // ...
}
*/
```

### Paso 2: Reactivar application.properties

Editar: `ms-Rutas/src/main/resources/application.properties`

```properties
# DESCOMENTAR estas líneas:
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8081/realms/bda-realm
spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost:8081/realms/bda-realm/protocol/openid-connect/certs
logging.level.org.springframework.security=DEBUG
```

### Paso 3: Reactivar @PreAuthorize en controladores

**RutasTentativasController.java:**
```java
// DESCOMENTAR:
@PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
@PostMapping("/tentativas")
public ResponseEntity<?> generarRutasTentativas(...) {
```

**RutasController.java:**
```java
// DESCOMENTAR:
@PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
@PostMapping("/crear-desde-tentativa")
public ResponseEntity<?> crearRutaDesdeTentativa(...) {
```

### Paso 4: Levantar Keycloak

```bash
cd seguridad-keycloak
docker-compose up -d
```

Esperar unos segundos hasta que Keycloak esté completamente iniciado (puerto 8081).

### Paso 5: Actualizar pruebas-rutas-tentativas.http

Agregar el header de autorización en cada petición:

```http
POST {{baseUrl}}/api/rutas/tentativas
Content-Type: application/json
Authorization: Bearer {{token}}

{
  "latitudOrigen": -31.4201,
  ...
}
```

### Paso 6: Obtener Token JWT

```http
POST http://localhost:8081/realms/bda-realm/protocol/openid-connect/token
Content-Type: application/x-www-form-urlencoded

grant_type=password
&client_id=bda-client
&username=admin1
&password=password123
```

---

## ✅ Verificación

### Con Keycloak DESACTIVADO (actual):
```bash
curl http://localhost:8095/api/rutas/tentativas \
  -H "Content-Type: application/json" \
  -d '{"latitudOrigen":-31.4201,"longitudOrigen":-64.1888,"latitudDestino":-34.6037,"longitudDestino":-58.3816}'
```
✅ Funciona sin token

### Con Keycloak ACTIVADO:
```bash
curl http://localhost:8095/api/rutas/tentativas \
  -H "Content-Type: application/json" \
  -d '{"latitudOrigen":-31.4201,"longitudOrigen":-64.1888,"latitudDestino":-34.6037,"longitudDestino":-58.3816}'
```
❌ Error 401 Unauthorized (sin token)

```bash
curl http://localhost:8095/api/rutas/tentativas \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGc..." \
  -d '{"latitudOrigen":-31.4201,"longitudOrigen":-64.1888,"latitudDestino":-34.6037,"longitudDestino":-58.3816}'
```
✅ Funciona con token válido

---

## 📝 Resumen de Archivos Modificados

| Archivo | Estado | Acción para Reactivar |
|---------|--------|----------------------|
| `SecurityConfig.java` | Simplificado | Comentar simple, descomentar completo |
| `application.properties` | Props comentadas | Descomentar líneas de Keycloak |
| `RutasTentativasController.java` | @PreAuthorize comentado | Descomentar anotación |
| `RutasController.java` | @PreAuthorize comentado | Descomentar anotación |
| `pruebas-rutas-tentativas.http` | Sin headers Auth | Agregar `Authorization: Bearer {{token}}` |

---

## 🎯 Estado Actual del Sistema

```
┌─────────────────────────────┐
│   ms-Rutas (Puerto 8095)    │
│   ✅ SIN Autenticación       │
│   ✅ Todos los endpoints     │
│      accesibles sin token   │
└─────────────────────────────┘
```

---

## ⚡ Inicio Rápido para Pruebas (SIN Keycloak)

```bash
# 1. Levantar ms-Rutas
cd ms-Rutas
mvn spring-boot:run

# 2. Probar endpoint (sin token)
curl -X POST http://localhost:8095/api/rutas/tentativas \
  -H "Content-Type: application/json" \
  -d '{
    "latitudOrigen": -31.4201,
    "longitudOrigen": -64.1888,
    "latitudDestino": -34.6037,
    "longitudDestino": -58.3816
  }'
```

✅ Debería devolver las 3 rutas tentativas sin necesidad de autenticación.

---

## 📌 Recordatorio

**Keycloak está desactivado TEMPORALMENTE solo para desarrollo.**

Para producción, **SIEMPRE** debes reactivar Keycloak siguiendo los pasos de arriba.

