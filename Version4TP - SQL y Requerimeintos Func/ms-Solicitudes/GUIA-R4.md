# Guía Completa: Requerimiento R4 - Asignar Ruta a Solicitud

## ✅ Implementación Completada

Se ha implementado exitosamente el **Requerimiento Funcional R4: Asignar una ruta con todos sus tramos a la solicitud**.

### 📁 Archivos Creados/Modificados

#### Nuevos DTOs:
1. ✅ `AsignarRutaRequestDTO.java` - DTO para la petición de asignación
2. ✅ `AsignarRutaResponseDTO.java` - DTO para la respuesta con información completa

#### Servicios Modificados:
3. ✅ `SolicitudService.java` - Método `asignarRutaASolicitud()` agregado

#### Controladores Modificados:
4. ✅ `SolicitudController.java` - Endpoint `PUT /api/solicitudes/{idSolicitud}/asignar-ruta` agregado

#### Documentación:
5. ✅ `R4-ASIGNAR-RUTA.md` - Documentación completa del requerimiento
6. ✅ `R4-asignar-ruta.http` - Archivo de pruebas HTTP

---

## 🎯 Funcionalidades Implementadas

### 1. Asignación de Ruta
- Asigna una ruta existente (desde ms-rutas) a una solicitud
- Valida que tanto la solicitud como la ruta existan

### 2. Actualización Automática de Datos
- **Costo Estimado**: Se obtiene de la ruta y se guarda en la solicitud
- **Tiempo Estimado**: Se convierte de formato HH:MM:SS a minutos
- **ID de Ruta**: Se vincula la ruta con la solicitud

### 3. Cambio de Estado Automático
- Si la solicitud está en **"borrador"** → cambia a **"programada"**
- Si ya está en otro estado, se mantiene

### 4. Información Completa de Tramos
- Retorna todos los tramos de la ruta con:
  - Orden del tramo
  - Depósito asociado
  - Distancia y tiempo
  - Costo por tramo

---

## 🚀 Cómo Ejecutar y Probar

### Paso 1: Iniciar los Microservicios

Necesitas tener activos ambos microservicios:

**Terminal 1 - ms-Solicitudes:**
```cmd
cd "C:\Users\pc_ju\OneDrive\Escritorio\TP-back\TP-back\Version3TP - Con una base de datos unificada SQL\ms-Solicitudes"
mvnw.cmd spring-boot:run
```

**Terminal 2 - ms-Rutas:**
```cmd
cd "C:\Users\pc_ju\OneDrive\Escritorio\TP-back\TP-back\Version3TP - Con una base de datos unificada SQL\ms-Rutas"
mvnw.cmd spring-boot:run
```

### Paso 2: Flujo de Prueba Completo

#### 2.1. Crear una Solicitud (R1)
```http
POST http://localhost:8090/api/solicitudes/completa
Content-Type: application/json

{
  "pesoContenedor": 1500.5,
  "volumenContenedor": 50.75,
  "nombreCliente": "Carlos",
  "apellidoCliente": "Rodriguez",
  "dniCliente": "98765432",
  "telefonoCliente": "5491198765432",
  "mailCliente": "carlos.rodriguez@example.com",
  "direccionCliente": "Av. Libertador 2000, CABA",
  "estadoInicial": "borrador"
}
```

**Respuesta esperada:** (Guarda el `numeroSolicitud`)
```json
{
  "numeroSolicitud": 1,
  "estadoSolicitud": {
    "nombre": "borrador"
  },
  ...
}
```

#### 2.2. Consultar Rutas Disponibles
```http
GET http://localhost:8090/api/solicitudes/rutas-tentativas
```

**Respuesta:** Lista de rutas con sus tramos, costos y tiempos

#### 2.3. Asignar Ruta a la Solicitud (R4) ⭐
```http
PUT http://localhost:8090/api/solicitudes/1/asignar-ruta
Content-Type: application/json

{
  "idRuta": 1
}
```

**Respuesta esperada:**
```json
{
  "numeroSolicitud": 1,
  "idRuta": 1,
  "cantidadTramos": 3,
  "cantidadDepositos": 2,
  "costoEstimado": 15750.50,
  "tiempoEstimado": "08:30:00",
  "estadoSolicitud": "programada",
  "mensaje": "Ruta asignada exitosamente a la solicitud",
  "tramos": [
    {
      "idTramo": 10,
      "nombreDeposito": "Depósito Norte",
      "orden": 1,
      "distanciaKm": 45.5,
      "costoTramo": 5250.00
    },
    ...
  ]
}
```

#### 2.4. Verificar Estado Actualizado (R2)
```http
GET http://localhost:8090/api/solicitudes/contenedor/1/estado
```

Deberías ver:
- Estado cambiado a **"programada"**
- Costo estimado actualizado
- ID de ruta asignado

---

## 📝 Ejemplos de Uso con CURL

### Asignar ruta a solicitud
```bash
curl -X PUT http://localhost:8090/api/solicitudes/1/asignar-ruta ^
  -H "Content-Type: application/json" ^
  -d "{\"idRuta\": 1}"
```

---

## ⚠️ Casos de Error

### Error 404 - Solicitud no existe
```json
"Error: No se encontró la solicitud con ID: 999"
```

### Error 404 - Ruta no existe
```json
"Error: No se encontró la ruta con ID: 999 en ms-rutas"
```

### Error 500 - ms-rutas no disponible
Asegúrate de que el microservicio ms-rutas esté activo en el puerto 8095.

---

## 🔗 Endpoints Relacionados

### Resumen de Todos los Endpoints Implementados:

| Método | Endpoint | Descripción | Req |
|--------|----------|-------------|-----|
| POST | `/api/solicitudes/completa` | Crear solicitud completa | R1 |
| GET | `/api/solicitudes/contenedor/{id}/estado` | Consultar estado contenedor | R2 |
| GET | `/api/solicitudes/rutas-tentativas` | Ver rutas disponibles | - |
| **PUT** | **`/api/solicitudes/{id}/asignar-ruta`** | **Asignar ruta a solicitud** | **R4** |
| GET | `/api/solicitudes/{id}/rutas` | Ver ruta asignada completa | - |

---

## 🎉 Próximos Pasos Sugeridos

1. **Probar el flujo completo** usando el archivo `R4-asignar-ruta.http`
2. **Verificar la integración** con ms-rutas
3. **Implementar validaciones adicionales** si es necesario:
   - Validar que la ruta sea compatible con el tipo de contenedor
   - Validar disponibilidad de depósitos
   - Calcular tarifas específicas

---

## 📚 Documentación Adicional

- Ver `R4-ASIGNAR-RUTA.md` para documentación detallada
- Ver `R4-asignar-ruta.http` para ejemplos de pruebas
- Ver `R1-SOLICITUD-COMPLETA.md` para crear solicitudes
- Ver `R2-CONSULTAR-ESTADO-CONTENEDOR.md` para consultar estados

---

## ✅ Checklist de Implementación

- [x] DTOs creados (Request y Response)
- [x] Servicio implementado con lógica de negocio
- [x] Endpoint REST creado
- [x] Integración con ms-rutas configurada
- [x] Actualización automática de costos y tiempos
- [x] Cambio automático de estado
- [x] Documentación completa
- [x] Archivo de pruebas HTTP
- [x] Manejo de errores
- [x] Validaciones implementadas

---

## 🎯 ¡Listo para Usar!

El requerimiento funcional R4 está **completamente implementado y listo para ser probado**.

Puedes empezar a usar el endpoint inmediatamente siguiendo los pasos de prueba en este documento o usando el archivo `R4-asignar-ruta.http`.

