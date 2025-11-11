# Endpoint para Crear Solicitud Completa

## Descripción
Este endpoint permite crear una solicitud de transporte completa que incluye:

1. ✅ **Creación del contenedor** con su identificación única (generada automáticamente)
2. ✅ **Registro del cliente** si no existe previamente (busca por DNI)
3. ✅ **Asignación del estado** inicial (borrador, programada, en tránsito, entregada)

## Endpoint

```
POST /api/solicitudes/completa
```

## Ejemplo de Request

```json
{
  "pesoContenedor": 1500.5,
  "volumenContenedor": 50.75,
  "nombreCliente": "Juan",
  "apellidoCliente": "Pérez",
  "dniCliente": "12345678",
  "telefonoCliente": "+54 9 11 1234-5678",
  "mailCliente": "juan.perez@example.com",
  "direccionCliente": "Av. Corrientes 1234, CABA",
  "estadoInicial": "borrador"
}
```

## Estados Disponibles

- `borrador` (por defecto si no se especifica)
- `programada`
- `en tránsito`
- `entregada`

## Comportamiento

### Caso 1: Cliente nuevo
Si el DNI no existe en la base de datos, se crea un nuevo cliente con todos los datos proporcionados.

### Caso 2: Cliente existente
Si el DNI ya existe, se utiliza el cliente existente y se ignoran los demás datos del cliente en el request.

### Contenedor
Siempre se crea un nuevo contenedor con un ID único generado automáticamente.

### Estado
Si el estado especificado no existe en la base de datos, se crea automáticamente.

## Ejemplo de Response

```json
{
  "numeroSolicitud": 1,
  "contenedor": {
    "idContenedor": 1,
    "peso": 1500.5,
    "volumen": 50.75
  },
  "cliente": {
    "idCliente": 1,
    "nombre": "Juan",
    "apellido": "Pérez",
    "dni": "12345678",
    "telefono": "+54 9 11 1234-5678",
    "mail": "juan.perez@example.com",
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

## Códigos de Respuesta

- **201 Created**: Solicitud creada exitosamente
- **400 Bad Request**: Error en los datos proporcionados (por ejemplo, DNI duplicado al intentar crear cliente)

## Ejemplo con CURL

```bash
curl -X POST http://localhost:8090/api/solicitudes/completa \
  -H "Content-Type: application/json" \
  -d '{
    "pesoContenedor": 1500.5,
    "volumenContenedor": 50.75,
    "nombreCliente": "Juan",
    "apellidoCliente": "Pérez",
    "dniCliente": "12345678",
    "telefonoCliente": "+54 9 11 1234-5678",
    "mailCliente": "juan.perez@example.com",
    "direccionCliente": "Av. Corrientes 1234, CABA",
    "estadoInicial": "borrador"
  }'
```

## Notas Importantes

- El campo `estadoInicial` es opcional. Si no se proporciona, se usa "borrador" por defecto.
- El contenedor siempre es nuevo y obtiene un ID único automáticamente.
- Si el cliente ya existe (mismo DNI), se reutiliza el existente.
- La transacción es atómica: si algo falla, no se crea nada.

