# R2: Consultar el Estado del Transporte de un Contenedor

## Descripción
Este endpoint permite al **cliente** consultar el estado actual del transporte de un contenedor usando su identificación única. Retorna información completa sobre la solicitud, el estado actual, costos y tiempos.

## Endpoint

```
GET /api/solicitudes/contenedor/{idContenedor}/estado
```

## Parámetros de Ruta

- `idContenedor` (Long, requerido): ID único del contenedor a consultar

## Ejemplo de Request

```http
GET http://localhost:8090/api/solicitudes/contenedor/1/estado
```

## Ejemplo de Response Exitoso (200 OK)

```json
{
  "idContenedor": 1,
  "peso": 1500.5,
  "volumen": 50.75,
  "numeroSolicitud": 1,
  "estadoActual": "borrador",
  "idCliente": 1,
  "nombreCliente": "Juan",
  "apellidoCliente": "Perez",
  "dniCliente": "12345678",
  "costoEstimado": null,
  "tiempoEstimado": null,
  "costoFinal": null,
  "tiempoReal": null,
  "idRuta": null
}
```

## Ejemplo con Estado Avanzado

Cuando la solicitud tiene más información completada:

```json
{
  "idContenedor": 2,
  "peso": 2000.0,
  "volumen": 75.5,
  "numeroSolicitud": 2,
  "estadoActual": "en tránsito",
  "idCliente": 1,
  "nombreCliente": "Juan",
  "apellidoCliente": "Perez",
  "dniCliente": "12345678",
  "costoEstimado": 15000.50,
  "tiempoEstimado": 120,
  "costoFinal": 14500.00,
  "tiempoReal": 110,
  "idRuta": 5
}
```

## Estados Posibles

El campo `estadoActual` puede tener los siguientes valores:

- **`borrador`**: La solicitud fue creada pero aún no está confirmada
- **`programada`**: La solicitud fue confirmada y está programada para transporte
- **`en tránsito`**: El contenedor está siendo transportado actualmente
- **`entregada`**: El contenedor fue entregado en el destino

## Códigos de Respuesta

### ✅ 200 OK
Contenedor encontrado y estado retornado exitosamente.

### ❌ 404 Not Found
No se encontró ninguna solicitud asociada al contenedor especificado.

**Ejemplo de respuesta:**
```json
"Error: No se encontró ninguna solicitud para el contenedor con ID: 999"
```

### ❌ 500 Internal Server Error
Error interno del servidor.

## Información Retornada

### Datos del Contenedor
- `idContenedor`: Identificador único del contenedor
- `peso`: Peso del contenedor en kg
- `volumen`: Volumen del contenedor en m³

### Datos de la Solicitud
- `numeroSolicitud`: Número único de la solicitud de transporte
- `estadoActual`: Estado actual del transporte

### Datos del Cliente
- `idCliente`: ID del cliente propietario
- `nombreCliente`: Nombre del cliente
- `apellidoCliente`: Apellido del cliente
- `dniCliente`: DNI del cliente

### Datos de Costos y Tiempos
- `costoEstimado`: Costo estimado inicial (puede ser null)
- `tiempoEstimado`: Tiempo estimado en minutos (puede ser null)
- `costoFinal`: Costo final real (puede ser null hasta completar el transporte)
- `tiempoReal`: Tiempo real en minutos (puede ser null hasta completar el transporte)
- `idRuta`: ID de la ruta asignada (puede ser null si no se asignó ruta)

## Casos de Uso

### Caso 1: Consultar contenedor recién creado
El cliente crea una solicitud y quiere verificar que se creó correctamente.

```http
GET /api/solicitudes/contenedor/1/estado
```

**Respuesta:** Estado "borrador" sin costos ni rutas asignadas.

### Caso 2: Seguimiento durante el transporte
El cliente quiere saber dónde está su contenedor durante el transporte.

```http
GET /api/solicitudes/contenedor/2/estado
```

**Respuesta:** Estado "en tránsito" con tiempos y costos estimados.

### Caso 3: Verificar entrega
El cliente quiere confirmar que su contenedor fue entregado.

```http
GET /api/solicitudes/contenedor/3/estado
```

**Respuesta:** Estado "entregada" con costos finales y tiempos reales.

## Ejemplo con CURL

```bash
curl -X GET http://localhost:8090/api/solicitudes/contenedor/1/estado
```

## Ejemplo con JavaScript (Fetch)

```javascript
fetch('http://localhost:8090/api/solicitudes/contenedor/1/estado')
  .then(response => {
    if (!response.ok) {
      throw new Error('Contenedor no encontrado');
    }
    return response.json();
  })
  .then(data => {
    console.log('Estado del contenedor:', data.estadoActual);
    console.log('Número de solicitud:', data.numeroSolicitud);
  })
  .catch(error => {
    console.error('Error:', error);
  });
```

## Notas Importantes

- ✅ Este endpoint es de **solo lectura** (GET), no modifica datos
- ✅ Solo se necesita el **ID del contenedor** para consultar
- ✅ Retorna información completa del estado actual
- ✅ Los campos de costos y tiempos pueden ser `null` si aún no se calcularon
- ✅ Cada contenedor solo puede estar en **una solicitud** a la vez
- ✅ El `idRuta` será `null` hasta que se asigne una ruta al transporte

## Flujo Típico

1. **Cliente crea solicitud** → Obtiene ID del contenedor en la respuesta
2. **Cliente consulta estado** → Usa el ID del contenedor para verificar
3. **Sistema actualiza estado** → (Proceso interno del administrador)
4. **Cliente consulta nuevamente** → Ve el estado actualizado

## Integración con Otros Endpoints

Este endpoint se complementa con:
- `POST /api/solicitudes/completa` - Para crear la solicitud inicial
- `GET /api/solicitudes/{id}/rutas` - Para obtener detalles de la ruta (si existe)

