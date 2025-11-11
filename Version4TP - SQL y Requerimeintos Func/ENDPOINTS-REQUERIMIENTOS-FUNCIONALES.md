# 📋 ENDPOINTS DEL SISTEMA - Requerimientos Funcionales

Este documento mapea cada requerimiento funcional con sus endpoints correspondientes.

---

## 🔧 **RF4: Asignar una ruta con todos sus tramos a la solicitud**
**Rol:** Operador/Administrador

### Endpoint Principal
```http
PUT /api/solicitudes/{numeroSolicitud}/asignar-ruta/{idRuta}
```
**Descripción:** Asigna una ruta completa con todos sus tramos a una solicitud específica.

**Parámetros:**
- `numeroSolicitud` (path): ID de la solicitud
- `idRuta` (path): ID de la ruta a asignar

**Response:** Solicitud actualizada con la ruta asignada

---

## 🔧 **RF5: Consultar todos los contenedores pendientes de entrega y su ubicación/estado con filtros**
**Rol:** Operador/Administrador

### Endpoint Principal
```http
GET /api/solicitudes/contenedores-pendientes?estado={estado}
```
**Descripción:** Obtiene una lista de todos los contenedores pendientes de entrega con opción de filtrar por estado.

**Parámetros:**
- `estado` (query, opcional): Estado para filtrar (ej: "En tránsito", "En depósito", etc.)

**Response:** Lista de `ContenedorPendienteDTO` con información completa de ubicación y estado

**Ejemplo:**
```json
{
  "idContenedor": 1,
  "numeroSolicitud": 123,
  "estadoActual": "En tránsito",
  "ubicacionActual": "Depósito Central",
  "latitudActual": -34.603722,
  "longitudActual": -58.381592,
  "peso": 1500.0,
  "volumen": 45.5,
  "nombreCliente": "Juan Pérez",
  "telefonoCliente": "1123456789"
}
```

---

## 🔧 **RF6: Asignar camión a un tramo de traslado de un contenedor**
**Rol:** Operador/Administrador

### Endpoint Principal
```http
POST /api/tramos/asignar-camion
```
**Descripción:** Asigna un camión disponible a un tramo específico del traslado.

**Body:**
```json
{
  "idTramo": 1,
  "idCamion": 5
}
```

**Response:** Tramo actualizado con el camión asignado

**Ejemplo de Response:**
```json
{
  "idTramo": 1,
  "latitudOrigen": -31.4201,
  "longitudOrigen": -64.1888,
  "latitudDestino": -31.4427,
  "longitudDestino": -64.1952,
  "costoAproximado": 15000.50,
  "costoReal": null,
  "fechaHoraInicio": null,
  "fechaHoraFin": null,
  "idCamion": 5,
  "tipoTramo": {
    "idTipoTramo": 1,
    "nombre": "Urbano"
  },
  "estado": {
    "idEstado": 1,
    "nombre": "Pendiente"
  },
  "ruta": {
    "idRuta": 10
  },
  "depositoOrigen": {
    "idDeposito": 1,
    "nombre": "Depósito Central Córdoba"
  },
  "depositoDestino": {
    "idDeposito": 2,
    "nombre": "Depósito San Francisco"
  }
}
```

### Endpoints Relacionados

#### Obtener camiones disponibles
```http
GET /api/camiones/disponibles
```
**Descripción:** Lista todos los camiones libres para asignar.

#### Obtener camiones compatibles
```http
GET /api/camiones/compatibles?peso={peso}&volumen={volumen}
```
**Descripción:** Lista camiones que cumplen con los requisitos de peso y volumen del contenedor.

**Parámetros:**
- `peso` (query): Peso del contenedor en kg
- `volumen` (query): Volumen del contenedor en m³

---

## 🔧 **RF7: Determinar el inicio o fin de un tramo de traslado**
**Rol:** Transportista

### Endpoints Principales

#### Iniciar viaje
```http
POST /api/tramos/iniciar-viaje
```
**Descripción:** Registra el inicio de un tramo con fecha/hora actual.

**Body:**
```json
{
  "idTramo": 1,
  "idCamion": 5
}
```

**Response:** Tramo actualizado con `fechaHoraInicio` y estado "En viaje"

#### Finalizar viaje
```http
POST /api/tramos/finalizar-viaje
```
**Descripción:** Registra el fin de un tramo con fecha/hora actual y costo real.

**Body:**
```json
{
  "idTramo": 1,
  "idCamion": 5,
  "costoReal": 1250.50
}
```

**Response:** Tramo actualizado con `fechaHoraFin`, `costoReal` y estado "Finalizado"

---

## 🔧 **RF8: Calcular el costo total de la entrega**
**Rol:** Sistema/Operador

### Endpoint Principal
```http
GET /api/solicitudes/{numeroSolicitud}/calcular-costo
```
**Descripción:** Calcula el costo total incluyendo:
1. Recorrido total (distancia entre origen → depósitos → destino)
2. Peso y volumen del contenedor
3. Estadía en depósitos (diferencia entre fechas de entrada/salida)

**Parámetros:**
- `numeroSolicitud` (path): ID de la solicitud

**Response:**
```json
{
  "numeroSolicitud": 123,
  "distanciaTotal": 450.5,
  "pesoContenedor": 1500.0,
  "volumenContenedor": 45.5,
  "diasEstadiaTotal": 3,
  "costoRecorrido": 22525.00,
  "costoEstadia": 1500.00,
  "costoTotal": 24025.00
}
```

### Endpoints Relacionados

#### Obtener tramos de una ruta
```http
GET /api/tramos/ruta/{idRuta}
```
**Descripción:** Obtiene todos los tramos de una ruta para calcular distancias y tiempos.

---

## 🔧 **RF9: Finalizar y registrar cálculo de tiempo real y costo real en la solicitud**
**Rol:** Sistema/Operador

### Endpoint Principal
```http
PUT /api/solicitudes/finalizar
```
**Descripción:** Registra los valores finales de tiempo y costo al completar toda la entrega.

**Body:**
```json
{
  "numeroSolicitud": 123,
  "costoFinal": 24025.50,
  "tiempoReal": 72
}
```
**Nota:** `tiempoReal` en horas

**Response:** Solicitud actualizada con estado "Entregado" y valores finales registrados

---

## 🔧 **RF10: Registrar y actualizar depósitos, camiones y tarifas**
**Rol:** Administrador

### Depósitos

#### Listar depósitos
```http
GET /api/depositos
```

#### Obtener depósito por ID
```http
GET /api/depositos/{id}
```

#### Crear depósito
```http
POST /api/depositos
```
**Body:**
```json
{
  "nombre": "Depósito Norte",
  "direccion": "Av. Libertador 5000",
  "latitud": -34.543,
  "longitud": -58.456,
  "idCiudad": 1
}
```

#### Actualizar depósito
```http
PUT /api/depositos/{id}
```

#### Eliminar depósito
```http
DELETE /api/depositos/{id}
```

### Camiones

#### Listar camiones
```http
GET /api/camiones
```

#### Obtener camión por ID
```http
GET /api/camiones/{id}
```

#### Crear camión
```http
POST /api/camiones
```
**Body:**
```json
{
  "patente": "ABC123",
  "telefono": "1123456789",
  "capacidadPeso": 5000.0,
  "capacidadVolumen": 150.0,
  "disponibilidad": true,
  "costoBaseKm": 50.0,
  "consumoCombustibleKm": 0.25,
  "transportista": {
    "idTransportista": 1
  }
}
```

#### Actualizar camión
```http
PUT /api/camiones/{id}
```

#### Eliminar camión
```http
DELETE /api/camiones/{id}
```

#### Actualizar disponibilidad de camión
```http
PUT /api/camiones/{id}/disponibilidad?disponible={true|false}
```

### Tarifas
**Nota:** Los endpoints de tarifas deben implementarse en el microservicio ms-Tarifas

```http
GET /api/tarifas
POST /api/tarifas
PUT /api/tarifas/{id}
DELETE /api/tarifas/{id}
```

---

## 🔧 **RF11: Validar que un camión no supere su capacidad máxima en peso ni volumen**
**Rol:** Sistema/Operador

### Endpoint Principal
```http
GET /api/camiones/{id}/validar-capacidad?peso={peso}&volumen={volumen}
```
**Descripción:** Valida que el camión especificado tenga capacidad suficiente para transportar el contenedor.

**Parámetros:**
- `id` (path): ID del camión
- `peso` (query): Peso del contenedor en kg
- `volumen` (query): Volumen del contenedor en m³

**Response:**
```json
{
  "idCamion": 5,
  "peso": 1500.0,
  "volumen": 45.5,
  "capacidadValida": true,
  "mensaje": "El camión tiene capacidad suficiente"
}
```

**Response (capacidad insuficiente):**
```json
{
  "idCamion": 5,
  "peso": 8000.0,
  "volumen": 45.5,
  "capacidadValida": false,
  "mensaje": "El camión ABC123 no tiene capacidad suficiente de peso. Capacidad: 5000.00 kg, Requerido: 8000.00 kg"
}
```

---

## 📊 **Endpoints Adicionales de Consulta**

### Contenedores en Depósito
```http
GET /api/tramos/deposito/{idDeposito}/contenedores
```
**Descripción:** Lista todos los contenedores que están actualmente en un depósito específico para asignarles camiones a su próximo tramo.

### Camiones Ocupados
```http
GET /api/camiones/ocupados
```
**Descripción:** Lista todos los camiones que están actualmente en uso.

### Seguimiento de Contenedor
```http
GET /api/seguimiento/contenedor/{idContenedor}
```
**Descripción:** Obtiene información detallada del seguimiento de un contenedor (retirado, en viaje, en depósito, entregado).

**Response:**
```json
{
  "idContenedor": 1,
  "numeroSolicitud": 123,
  "estadoActual": "En viaje",
  "ubicacionActual": "En ruta a Depósito Norte",
  "latitudActual": -34.543,
  "longitudActual": -58.456,
  "retirado": true,
  "enViaje": true,
  "enDeposito": false,
  "entregado": false,
  "progresoTramos": 2,
  "totalTramos": 5,
  "costoEstimado": 24000.00,
  "tiempoEstimado": 72
}
```

---

## 🔐 Roles y Permisos

| Endpoint | Cliente | Transportista | Operador | Administrador |
|----------|---------|---------------|----------|---------------|
| POST /api/solicitudes/completa | ✅ | ❌ | ✅ | ✅ |
| PUT /api/solicitudes/.../asignar-ruta/... | ❌ | ❌ | ✅ | ✅ |
| GET /api/solicitudes/contenedores-pendientes | ❌ | ❌ | ✅ | ✅ |
| POST /api/tramos/asignar-camion | ❌ | ❌ | ✅ | ✅ |
| POST /api/tramos/iniciar-viaje | ❌ | ✅ | ✅ | ✅ |
| POST /api/tramos/finalizar-viaje | ❌ | ✅ | ✅ | ✅ |
| GET /api/solicitudes/.../calcular-costo | ❌ | ❌ | ✅ | ✅ |
| PUT /api/solicitudes/finalizar | ❌ | ❌ | ✅ | ✅ |
| POST/PUT/DELETE /api/depositos | ❌ | ❌ | ❌ | ✅ |
| POST/PUT/DELETE /api/camiones | ❌ | ❌ | ❌ | ✅ |
| GET /api/camiones/.../validar-capacidad | ❌ | ❌ | ✅ | ✅ |
| GET /api/seguimiento/contenedor/... | ✅ | ❌ | ✅ | ✅ |

---

## 🌐 Puertos de los Microservicios

- **Gateway:** `8080`
- **ms-Solicitudes:** `8091`
- **ms-Rutas:** `8095`
- **ms-Flotas:** `8092`
- **ms-Tarifas:** `8093`

**Base URL (a través del Gateway):**
```
http://localhost:8080
```

**URLs Directas (sin Gateway):**
```
http://localhost:8091  (Solicitudes)
http://localhost:8095  (Rutas)
http://localhost:8092  (Flotas)
http://localhost:8093  (Tarifas)
```

---

## 📝 Notas Importantes

1. **Validación de Capacidad:** Siempre debe validarse la capacidad del camión antes de asignarlo a un tramo.

2. **Cálculo de Costos:** El costo total se calcula considerando:
   - Distancia total del recorrido
   - Peso y volumen del contenedor
   - Días de estadía en depósitos

3. **Flujo de Estados:** 
   - Borrador → Asignada → En tránsito → En depósito → En viaje → Entregado

4. **Fechas y Tiempos:** Las fechas se registran automáticamente al iniciar/finalizar tramos.

5. **Swagger UI:** Todos los endpoints están documentados en:
   ```
   http://localhost:8091/swagger-ui.html (Solicitudes)
   http://localhost:8095/swagger-ui.html (Rutas)
   http://localhost:8092/swagger-ui.html (Flotas)
   ```
