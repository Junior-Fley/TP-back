# R4: Asignar una Ruta con Todos sus Tramos a la Solicitud

## Descripción
Este endpoint permite al **Operador/Administrador** asignar una ruta existente con todos sus tramos a una solicitud de transporte. Al asignar la ruta, el sistema automáticamente:

1. ✅ **Obtiene información completa de la ruta** desde el microservicio ms-rutas
2. ✅ **Actualiza el costo estimado** basado en la ruta seleccionada
3. ✅ **Actualiza el tiempo estimado** de entrega
4. ✅ **Cambia el estado a "programada"** si la solicitud estaba en "borrador"
5. ✅ **Retorna todos los tramos** de la ruta asignada

## Endpoint

```
PUT /api/solicitudes/{idSolicitud}/asignar-ruta
```

## Parámetros

### Parámetro de Ruta
- `idSolicitud` (Long, requerido): ID de la solicitud a la que se asignará la ruta

### Body de la Petición

```json
{
  "idRuta": 1
}
```

## Ejemplo de Request

```http
PUT http://localhost:8090/api/solicitudes/1/asignar-ruta
Content-Type: application/json

{
  "idRuta": 5
}
```

## Ejemplo de Response Exitoso (200 OK)

```json
{
  "numeroSolicitud": 1,
  "idRuta": 5,
  "cantidadTramos": 3,
  "cantidadDepositos": 2,
  "costoEstimado": 15750.50,
  "tiempoEstimado": "08:30:00",
  "estadoSolicitud": "programada",
  "mensaje": "Ruta asignada exitosamente a la solicitud",
  "tramos": [
    {
      "idTramo": 10,
      "idDeposito": 1,
      "nombreDeposito": "Depósito Norte",
      "orden": 1,
      "distanciaKm": 45.5,
      "tiempoEstimadoHoras": 2.5,
      "costoTramo": 5250.00
    },
    {
      "idTramo": 11,
      "idDeposito": 3,
      "nombreDeposito": "Depósito Centro",
      "orden": 2,
      "distanciaKm": 30.0,
      "tiempoEstimadoHoras": 3.0,
      "costoTramo": 5500.50
    },
    {
      "idTramo": 12,
      "idDeposito": 5,
      "nombreDeposito": "Depósito Sur",
      "orden": 3,
      "distanciaKm": 38.2,
      "tiempoEstimadoHoras": 3.0,
      "costoTramo": 5000.00
    }
  ]
}
```

## Comportamiento

### 1. Validación de Solicitud
El sistema verifica que la solicitud exista. Si no existe, retorna un error 404.

### 2. Validación de Ruta
El sistema consulta al microservicio ms-rutas para verificar que la ruta existe y obtener todos sus tramos.

### 3. Actualización de Datos
- **Costo Estimado**: Se obtiene el costo total calculado de la ruta
- **Tiempo Estimado**: Se convierte de formato HH:MM:SS a minutos y se guarda
- **ID de Ruta**: Se asocia la ruta con la solicitud

### 4. Cambio de Estado Automático
Si la solicitud está en estado "borrador", se cambia automáticamente a "programada".

### 5. Información de Tramos
La respuesta incluye el detalle completo de todos los tramos que conforman la ruta:
- Orden de los tramos
- Depósitos involucrados
- Distancias
- Tiempos estimados por tramo
- Costos por tramo

## Códigos de Respuesta

### ✅ 200 OK
Ruta asignada exitosamente.

### ❌ 404 Not Found
No se encontró la solicitud o la ruta especificada.

**Ejemplos de respuesta:**
```json
"Error: No se encontró la solicitud con ID: 999"
```
```json
"Error: No se encontró la ruta con ID: 999 en ms-rutas"
```

### ❌ 500 Internal Server Error
Error interno del servidor (por ejemplo, ms-rutas no disponible).

## Flujo de Trabajo Recomendado

### Paso 1: Crear una Solicitud (R1)
```http
POST /api/solicitudes/completa
```

### Paso 2: Consultar Rutas Tentativas Disponibles
```http
GET /api/solicitudes/rutas-tentativas
```

### Paso 3: Asignar una Ruta a la Solicitud (R4)
```http
PUT /api/solicitudes/{idSolicitud}/asignar-ruta
```

### Paso 4: Verificar el Estado (R2)
```http
GET /api/solicitudes/contenedor/{idContenedor}/estado
```

## Ejemplo con CURL

```bash
curl -X PUT http://localhost:8090/api/solicitudes/1/asignar-ruta \
  -H "Content-Type: application/json" \
  -d '{"idRuta": 5}'
```

## Roles Autorizados

- **Operador**: Puede asignar rutas a solicitudes
- **Administrador**: Puede asignar rutas a solicitudes

❌ **Cliente**: NO tiene acceso a este endpoint (solo consulta estados)

## Notas Importantes

- ⚠️ Una solicitud puede tener solo una ruta asignada a la vez
- ⚠️ Si se asigna una nueva ruta, reemplazará la anterior
- ⚠️ El microservicio ms-rutas debe estar activo para este endpoint
- ✅ Los tramos se ordenan automáticamente según el campo `orden`
- ✅ El estado cambia automáticamente de "borrador" a "programada"

## Integración con ms-rutas

Este endpoint consume los siguientes servicios de ms-rutas:

```
GET http://localhost:8095/api/rutas/{idRuta}/resumen
```

La respuesta de ms-rutas incluye:
- Información general de la ruta
- Lista completa de tramos con depósitos
- Cálculos de costos y tiempos totales

