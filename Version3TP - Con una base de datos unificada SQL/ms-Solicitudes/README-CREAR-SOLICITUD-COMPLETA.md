# Endpoint: Crear Solicitud Completa de Transporte

## Descripción
Este endpoint permite registrar una nueva solicitud de transporte de contenedor de manera completa, incluyendo:

1. ✅ **Creación del contenedor** con su identificación única
2. ✅ **Registro del cliente** si no existe previamente (verifica por DNI o mail)
3. ✅ **Registro de la solicitud** con un estado inicial (borrador, programada, en tránsito, entregada)

## Endpoint
```
POST /api/solicitudes/completa
```

## Request Body (JSON)

```json
{
  "pesoContenedor": 1500.5,
  "volumenContenedor": 25.0,
  "nombreCliente": "Juan",
  "apellidoCliente": "Pérez",
  "dniCliente": "12345678",
  "telefonoCliente": "+54 9 11 1234-5678",
  "mailCliente": "juan.perez@email.com",
  "direccionCliente": "Av. Corrientes 1234, CABA",
  "estadoInicial": "borrador"
}

```

### Parámetros

| Campo | Tipo | Requerido | Descripción |
|-------|------|-----------|-------------|
| `pesoContenedor` | Double | Sí | Peso del contenedor en kg |
| `volumenContenedor` | Double | Sí | Volumen del contenedor en m³ |
| `nombreCliente` | String | Sí | Nombre del cliente |
| `apellidoCliente` | String | Sí | Apellido del cliente |
| `dniCliente` | String | Sí | DNI del cliente (se usa para verificar si existe) |
| `telefonoCliente` | String | No | Teléfono del cliente |
| `mailCliente` | String | No | Email del cliente (se usa para verificar si existe) |
| `direccionCliente` | String | No | Dirección del cliente |
| `estadoInicial` | String | No | Estado inicial de la solicitud. Por defecto: "borrador" |

### Estados Disponibles
- `borrador` (por defecto)
- `programada`
- `en tránsito`
- `entregada`

## Response

### Éxito (201 Created)
```json
{
  "numeroSolicitud": 1,
  "contenedor": {
    "idContenedor": 1,
    "peso": 1500.5,
    "volumen": 25.0
  },
  "cliente": {
    "idCliente": 1,
    "nombre": "Juan",
    "apellido": "Pérez",
    "dni": "12345678",
    "telefono": "+54 9 11 1234-5678",
    "mail": "juan.perez@email.com",
    "direccion": "Av. Corrientes 1234, CABA"
  },
  "estadoSolicitud": {
    "idEstado": 1,
    "nombre": "borrador"
  },
  "costoEstimado": null,
  "tiempoEstimado": null,
  "costoFinal": null,
  "tiempoReal": null,
  "idTarifa": null,
  "idRuta": null
}
```

### Error (400 Bad Request)
```json
{
  "timestamp": "2025-11-11T10:30:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "path": "/api/solicitudes/completa"
}
```

## Ejemplos de Uso

### 1. Cliente Nuevo - Estado Borrador
```bash
curl -X POST http://localhost:8090/api/solicitudes/completa \
  -H "Content-Type: application/json" \
  -d '{
    "pesoContenedor": 1500.5,
    "volumenContenedor": 25.0,
    "nombreCliente": "Juan",
    "apellidoCliente": "Pérez",
    "dniCliente": "12345678",
    "telefonoCliente": "+54 9 11 1234-5678",
    "mailCliente": "juan.perez@email.com",
    "direccionCliente": "Av. Corrientes 1234, CABA"
  }'
```

### 2. Cliente Existente (por DNI) - Estado Programada
```bash
curl -X POST http://localhost:8090/api/solicitudes/completa \
  -H "Content-Type: application/json" \
  -d '{
    "pesoContenedor": 2000.0,
    "volumenContenedor": 30.0,
    "dniCliente": "12345678",
    "estadoInicial": "programada"
  }'
```
*Nota: Si el cliente con ese DNI existe, se reutiliza. Los demás datos del cliente no son necesarios.*

### 3. Cliente Nuevo - Estado En Tránsito
```bash
curl -X POST http://localhost:8090/api/solicitudes/completa \
  -H "Content-Type: application/json" \
  -d '{
    "pesoContenedor": 1800.0,
    "volumenContenedor": 28.5,
    "nombreCliente": "María",
    "apellidoCliente": "González",
    "dniCliente": "87654321",
    "mailCliente": "maria.gonzalez@email.com",
    "estadoInicial": "en tránsito"
  }'
```

### 4. Con Postman
1. Método: **POST**
2. URL: `http://localhost:8090/api/solicitudes/completa`
3. Headers:
   - `Content-Type: application/json`
4. Body (raw - JSON):
```json
{
  "pesoContenedor": 1500.5,
  "volumenContenedor": 25.0,
  "nombreCliente": "Juan",
  "apellidoCliente": "Pérez",
  "dniCliente": "12345678",
  "telefonoCliente": "+54 9 11 1234-5678",
  "mailCliente": "juan.perez@email.com",
  "direccionCliente": "Av. Corrientes 1234, CABA",
  "estadoInicial": "borrador"
}
```

## Lógica de Negocio

### 1. Creación del Contenedor
- Se crea un nuevo contenedor con peso y volumen especificados
- Se genera un ID único automáticamente

### 2. Gestión del Cliente
- **Si existe por DNI**: Se reutiliza el cliente existente
- **Si existe por Email**: Se reutiliza el cliente existente
- **Si no existe**: Se crea un nuevo cliente con los datos proporcionados

### 3. Estado de la Solicitud
- **Si no se especifica**: Se asigna "borrador" por defecto
- **Si el estado no existe**: Se crea automáticamente
- **Si el estado existe**: Se reutiliza

## Notas Técnicas

- La operación es **transaccional** (`@Transactional`)
- Si ocurre un error, se hace rollback de toda la operación
- El contenedor siempre es nuevo y único para cada solicitud
- El cliente puede ser reutilizado si ya existe en el sistema

