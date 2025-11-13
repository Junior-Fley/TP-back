# Endpoint: Consultar Estado del Transporte de un Contenedor

## Descripción
Este endpoint permite al cliente consultar el estado actual del transporte de un contenedor específico. Es útil para hacer seguimiento del contenedor durante todo el proceso de transporte.

## Endpoint
```
GET /api/solicitudes/contenedor/{idContenedor}/estado
```

## Parámetros de Ruta

| Parámetro | Tipo | Descripción |
|-----------|------|-------------|
| `idContenedor` | Long | ID único del contenedor a consultar |

## Response

### Éxito (200 OK)
```json
{
  "idContenedor": 1,
  "estadoActual": "en tránsito",
  "descripcionEstado": "El contenedor está siendo transportado",
  "numeroSolicitud": 5
}
```

### Campos de la Respuesta

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `idContenedor` | Long | ID del contenedor consultado |
| `estadoActual` | String | Estado actual del transporte (borrador, programada, en tránsito, entregada) |
| `descripcionEstado` | String | Descripción amigable del estado actual |
| `numeroSolicitud` | Long | Número de la solicitud asociada al contenedor |

### Estados Posibles

| Estado | Descripción |
|--------|-------------|
| `borrador` | La solicitud ha sido creada y está pendiente de confirmación |
| `programada` | El transporte ha sido programado y está listo para comenzar |
| `en tránsito` | El contenedor está siendo transportado |
| `entregada` | El contenedor ha sido entregado en su destino |

### Contenedor no encontrado (404 Not Found)
Si el contenedor no existe o no tiene una solicitud asociada, se retorna un 404.

## Ejemplos de Uso

### 1. Consultar estado de un contenedor con cURL
```bash
curl -X GET http://localhost:8090/api/solicitudes/contenedor/1/estado
```

**Respuesta:**
```json
{
  "idContenedor": 1,
  "estadoActual": "borrador",
  "descripcionEstado": "La solicitud ha sido creada y está pendiente de confirmación",
  "numeroSolicitud": 1
}
```

### 2. Contenedor en tránsito
```bash
curl -X GET http://localhost:8090/api/solicitudes/contenedor/5/estado
```

**Respuesta:**
```json
{
  "idContenedor": 5,
  "estadoActual": "en tránsito",
  "descripcionEstado": "El contenedor está siendo transportado",
  "numeroSolicitud": 8
}
```

### 3. Contenedor entregado
```bash
curl -X GET http://localhost:8090/api/solicitudes/contenedor/10/estado
```

**Respuesta:**
```json
{
  "idContenedor": 10,
  "estadoActual": "entregada",
  "descripcionEstado": "El contenedor ha sido entregado en su destino",
  "numeroSolicitud": 15
}
```

### 4. Contenedor no encontrado
```bash
curl -X GET http://localhost:8090/api/solicitudes/contenedor/999/estado
```

**Respuesta:** 404 Not Found

### 5. Con Postman
1. Método: **GET**
2. URL: `http://localhost:8090/api/solicitudes/contenedor/1/estado`
3. Headers: No se requieren headers especiales

## Flujo de Negocio

1. **Cliente crea solicitud**: Se le asigna un contenedor con ID único (ej: 1)
2. **Cliente consulta estado**: Realiza un GET a `/api/solicitudes/contenedor/1/estado`
3. **Sistema busca solicitud**: Se busca la solicitud asociada al contenedor
4. **Sistema retorna estado**: Se devuelve el estado actual con descripción amigable

## Casos de Uso

### Caso 1: Seguimiento del cliente
Un cliente creó una solicitud con el contenedor ID 1 y quiere saber si ya fue programada:

```bash
GET /api/solicitudes/contenedor/1/estado
```

Respuesta:
```json
{
  "idContenedor": 1,
  "estadoActual": "programada",
  "descripcionEstado": "El transporte ha sido programado y está listo para comenzar",
  "numeroSolicitud": 1
}
```

### Caso 2: Verificar entrega
Un cliente quiere verificar si su contenedor ya fue entregado:

```bash
GET /api/solicitudes/contenedor/3/estado
```

Respuesta:
```json
{
  "idContenedor": 3,
  "estadoActual": "entregada",
  "descripcionEstado": "El contenedor ha sido entregado en su destino",
  "numeroSolicitud": 3
}
```

## Integración con el Flujo Completo

### Paso 1: Crear solicitud completa
```bash
POST /api/solicitudes/completa
{
  "pesoContenedor": 1500.5,
  "volumenContenedor": 25.0,
  "nombreCliente": "Juan",
  "apellidoCliente": "Pérez",
  "dniCliente": "12345678",
  "estadoInicial": "borrador"
}
```

**Respuesta:**
```json
{
  "numeroSolicitud": 1,
  "contenedor": {
    "idContenedor": 1,
    "peso": 1500.5,
    "volumen": 25.0
  },
  "estadoSolicitud": {
    "idEstado": 1,
    "nombre": "borrador"
  }
}
```

### Paso 2: Consultar estado del contenedor
```bash
GET /api/solicitudes/contenedor/1/estado
```

**Respuesta:**
```json
{
  "idContenedor": 1,
  "estadoActual": "borrador",
  "descripcionEstado": "La solicitud ha sido creada y está pendiente de confirmación",
  "numeroSolicitud": 1
}
```

## Notas Técnicas

- La búsqueda se realiza por ID del contenedor
- Cada contenedor está asociado a una única solicitud
- El estado se obtiene directamente de la solicitud asociada
- Si no existe solicitud para el contenedor, se retorna 404
- El endpoint es **read-only** (solo consulta, no modifica)

## Códigos de Estado HTTP

| Código | Descripción |
|--------|-------------|
| 200 | Contenedor encontrado, estado retornado exitosamente |
| 404 | Contenedor no encontrado o sin solicitud asociada |
| 500 | Error interno del servidor |

## Ventajas del Diseño

1. ✅ **Simplicidad**: Solo se necesita el ID del contenedor
2. ✅ **Información clara**: Incluye descripción amigable del estado
3. ✅ **Trazabilidad**: Incluye el número de solicitud asociada
4. ✅ **Performance**: Consulta directa sin joins complejos

