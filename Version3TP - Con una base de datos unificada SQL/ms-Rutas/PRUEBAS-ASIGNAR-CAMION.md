# Pruebas - Asignar Camión a Tramo

## Endpoint implementado
```
PUT http://localhost:8095/api/tramos/{idTramo}/asignar-camion/{idCamion}
```

## Pre-requisitos
1. El microservicio de Transporte debe estar ejecutándose en el puerto 8085
2. El microservicio de Rutas debe estar ejecutándose en el puerto 8095
3. Debe existir un tramo con el ID especificado
4. Debe existir un camión con el ID especificado en el ms-Transporte

## Ejemplos de prueba

### ✅ Caso exitoso
```http
PUT http://localhost:8095/api/tramos/1/asignar-camion/1
```

**Respuesta esperada (200 OK):**
```json
{
  "idTramo": 1,
  "idCamion": 1,
  "latitudOrigen": -34.6037,
  "longitudOrigen": -58.3816,
  "latitudDestino": -34.6158,
  "longitudDestino": -58.5033,
  ...resto de campos del tramo...
}
```

### ❌ Caso de error - Tramo no existe
```http
PUT http://localhost:8095/api/tramos/999/asignar-camion/1
```

**Respuesta esperada (500):**
```json
{
  "timestamp": "2025-11-12T10:30:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Tramo no encontrado con ID: 999",
  "path": "/api/tramos/999/asignar-camion/1"
}
```

### ❌ Caso de error - Camión no existe
```http
PUT http://localhost:8095/api/tramos/1/asignar-camion/999
```

**Respuesta esperada (500):**
```json
{
  "timestamp": "2025-11-12T10:30:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Camión no encontrado con ID: 999 en el microservicio de Transporte",
  "path": "/api/tramos/1/asignar-camion/999"
}
```

## Logs esperados

### En ms-Rutas (puerto 8095):
```
🚛 Verificando existencia del camión con ID: 1
✅ Camión ID 1 encontrado: ABC123
```

### En ms-Transporte (puerto 8085):
```
Hibernate: select c1_0.id_camion,c1_0.capacidad_peso,c1_0.capacidad_volumen,c1_0.consumo_combustible_km,c1_0.costo_base_km,c1_0.disponibilidad,c1_0.patente,c1_0.telefono,c1_0.id_transportista from camion c1_0 where c1_0.id_camion=?
```

## Verificación de la asignación

Después de asignar el camión, puedes verificar con:

```http
GET http://localhost:8095/api/tramos/1
```

Deberías ver que el campo `idCamion` tiene el valor del camión asignado.

## Comandos curl para probar

### Asignar camión
```bash
curl -X PUT http://localhost:8095/api/tramos/1/asignar-camion/1
```

### Ver el tramo actualizado
```bash
curl -X GET http://localhost:8095/api/tramos/1
```

### Ver todos los tramos
```bash
curl -X GET http://localhost:8095/api/tramos
```

