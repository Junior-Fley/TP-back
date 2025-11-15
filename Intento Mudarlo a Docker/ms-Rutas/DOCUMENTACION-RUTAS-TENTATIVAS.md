# 🚀 Generación de Rutas Tentativas con OSRM

## 📋 Descripción

Sistema completo para generar **3 rutas tentativas** entre un origen y destino usando OSRM (Open Source Routing Machine), permitiendo al usuario seleccionar la mejor opción y crear una ruta definitiva en la base de datos.

---

## 🎯 Funcionalidad Implementada

### ✅ Componentes Creados

1. **DTOs (Data Transfer Objects):**
   - `GenerarRutasTentativasRequestDTO` - Request con coordenadas origen/destino
   - `RutasTentativasResponseDTO` - Response con las 3 rutas generadas
   - `RutaTentativaDTO` - Una ruta completa con sus tramos
   - `TramoTentativoDTO` - Un tramo individual de la ruta
   - `CrearRutaDesdeTeantativaDTO` - Para crear ruta definitiva

2. **Servicios:**
   - `RutasTentativasService` - Genera las 3 opciones de ruta
   - `RutasService.crearRutaDesdeTentativa()` - Crea ruta definitiva

3. **Controladores:**
   - `RutasTentativasController` - Endpoint POST /api/rutas/tentativas
   - `RutasController` - Endpoint POST /api/rutas/crear-desde-tentativa

---

## 🟦 Ruta 1: Directa (1 tramo)

**Descripción:** Ruta directa sin paradas intermedias.

**Características:**
- ✅ Un único tramo: origen → destino
- ✅ Consulta OSRM para distancia y duración real
- ✅ Calcula costo aproximado basado en:
  - Costo base por km: $150/km
  - Costo combustible por km: $80/km
  - Cargo gestión: $5,000 por tramo

**Ventajas:**
- ⏱️ Menor tiempo de entrega
- 💰 Menor costo total
- 🎯 Sin paradas intermedias

**Desventajas:**
- ❌ No hay respaldo si falla el camión
- ❌ Mayor riesgo en largas distancias

---

## 🟧 Ruta 2: Con 1 Depósito Intermedio (2 tramos)

**Descripción:** Ruta con una parada estratégica en un depósito.

**Lógica de Selección:**
1. Calcula el punto medio entre origen y destino
2. Busca el depósito más cercano al punto medio
3. Crea dos tramos:
   - Tramo A: origen → depósito
   - Tramo B: depósito → destino

**Características:**
- ✅ Cada tramo consulta OSRM independientemente
- ✅ Incluye costo de estadía del depósito
- ✅ Total = suma de ambos tramos

**Ventajas:**
- 🏢 Punto de respaldo en caso de problemas
- 📦 Posibilidad de consolidar cargas
- 🔄 Cambio de camión si es necesario

**Desventajas:**
- ⏱️ Mayor tiempo por la parada
- 💰 Costo adicional de estadía

---

## 🟩 Ruta 3: Con 2 Depósitos Intermedios (3 tramos)

**Descripción:** Ruta con dos paradas estratégicas.

**Lógica de Selección:**
1. Calcula punto al 33% del recorrido (primer depósito)
2. Calcula punto al 66% del recorrido (segundo depósito)
3. Busca los depósitos más cercanos a cada punto
4. Crea tres tramos:
   - Tramo A: origen → depósito1
   - Tramo B: depósito1 → depósito2
   - Tramo C: depósito2 → destino

**Características:**
- ✅ Tres consultas independientes a OSRM
- ✅ Incluye costos de estadía de ambos depósitos
- ✅ Total = suma de los tres tramos

**Ventajas:**
- 🏢🏢 Máximo respaldo y flexibilidad
- 📦 Múltiples puntos de consolidación
- 🔄 Mayor control del proceso
- 🚛 Rotación de camiones eficiente

**Desventajas:**
- ⏱️ Mayor tiempo de entrega
- 💰 Mayor costo total (3 tramos + 2 estadías)

---

## 📡 Endpoints

### 1️⃣ Generar Rutas Tentativas

```http
POST /api/rutas/tentativas
Content-Type: application/json
Authorization: Bearer {token_admin_o_operador}

{
  "latitudOrigen": -31.4201,
  "longitudOrigen": -64.1888,
  "latitudDestino": -34.6037,
  "longitudDestino": -58.3816
}
```

**Respuesta (200 OK):**
```json
{
  "rutaDirecta": {
    "tipo": "DIRECTA",
    "descripcion": "Ruta directa sin paradas intermedias",
    "cantidadTramos": 1,
    "tramos": [
      {
        "orden": 1,
        "tipoTramo": "directo",
        "latitudOrigen": -31.4201,
        "longitudOrigen": -64.1888,
        "nombreOrigen": "Punto de origen",
        "idDepositoOrigen": null,
        "latitudDestino": -34.6037,
        "longitudDestino": -58.3816,
        "nombreDestino": "Punto de destino",
        "idDepositoDestino": null,
        "distanciaKm": 715.5,
        "duracionMinutos": 540.2,
        "costoAproximado": 169465.00,
        "costoEstadiaDiario": null
      }
    ],
    "distanciaTotalKm": 715.5,
    "duracionTotalMinutos": 540.2,
    "costoTotalAproximado": 169465.00,
    "depositosIntermedios": []
  },
  "rutaCon1Deposito": {
    "tipo": "CON_1_DEPOSITO",
    "descripcion": "Ruta con parada en Centro Logístico Santa Fe",
    "cantidadTramos": 2,
    "tramos": [
      {
        "orden": 1,
        "tipoTramo": "a_deposito",
        "latitudOrigen": -31.4201,
        "longitudOrigen": -64.1888,
        "nombreOrigen": "Punto de origen",
        "idDepositoOrigen": null,
        "latitudDestino": -31.6107,
        "longitudDestino": -60.6973,
        "nombreDestino": "Centro Logístico Santa Fe",
        "idDepositoDestino": 40,
        "distanciaKm": 345.8,
        "duracionMinutos": 260.5,
        "costoAproximado": 84534.00,
        "costoEstadiaDiario": 3500.00
      },
      {
        "orden": 2,
        "tipoTramo": "desde_deposito",
        "latitudOrigen": -31.6107,
        "longitudOrigen": -60.6973,
        "nombreOrigen": "Centro Logístico Santa Fe",
        "idDepositoOrigen": 40,
        "latitudDestino": -34.6037,
        "longitudDestino": -58.3816,
        "nombreDestino": "Punto de destino",
        "idDepositoDestino": null,
        "distanciaKm": 385.2,
        "duracionMinutos": 290.8,
        "costoAproximado": 93596.00,
        "costoEstadiaDiario": null
      }
    ],
    "distanciaTotalKm": 731.0,
    "duracionTotalMinutos": 551.3,
    "costoTotalAproximado": 178130.00,
    "depositosIntermedios": ["Centro Logístico Santa Fe"]
  },
  "rutaCon2Depositos": {
    "tipo": "CON_2_DEPOSITOS",
    "descripcion": "Ruta con paradas en Parque Logístico Córdoba y Centro Logístico Santa Fe",
    "cantidadTramos": 3,
    "tramos": [...],
    "distanciaTotalKm": 750.3,
    "duracionTotalMinutos": 565.0,
    "costoTotalAproximado": 187500.00,
    "depositosIntermedios": ["Parque Logístico Córdoba", "Centro Logístico Santa Fe"]
  },
  "latitudOrigen": -31.4201,
  "longitudOrigen": -64.1888,
  "latitudDestino": -34.6037,
  "longitudDestino": -58.3816
}
```

---

### 2️⃣ Crear Ruta Definitiva desde Tentativa

```http
POST /api/rutas/crear-desde-tentativa
Content-Type: application/json
Authorization: Bearer {token_admin_o_operador}

{
  "idSolicitud": 1,
  "tipoRuta": "CON_1_DEPOSITO",
  "latitudOrigen": -31.4201,
  "longitudOrigen": -64.1888,
  "latitudDestino": -34.6037,
  "longitudDestino": -58.3816,
  "tramos": [
    {
      "orden": 1,
      "tipoTramo": "a_deposito",
      "latitudOrigen": -31.4201,
      "longitudOrigen": -64.1888,
      "nombreOrigen": "Punto de origen",
      "idDepositoOrigen": null,
      "latitudDestino": -31.6107,
      "longitudDestino": -60.6973,
      "nombreDestino": "Centro Logístico Santa Fe",
      "idDepositoDestino": 40,
      "distanciaKm": 345.8,
      "duracionMinutos": 260.5,
      "costoAproximado": 84534.00,
      "costoEstadiaDiario": 3500.00
    },
    {
      "orden": 2,
      "tipoTramo": "desde_deposito",
      "latitudOrigen": -31.6107,
      "longitudOrigen": -60.6973,
      "nombreOrigen": "Centro Logístico Santa Fe",
      "idDepositoOrigen": 40,
      "latitudDestino": -34.6037,
      "longitudDestino": -58.3816,
      "nombreDestino": "Punto de destino",
      "idDepositoDestino": null,
      "distanciaKm": 385.2,
      "duracionMinutos": 290.8,
      "costoAproximado": 93596.00,
      "costoEstadiaDiario": null
    }
  ]
}
```

**Respuesta (201 Created):**
```json
{
  "idRuta": 5,
  "idSolicitud": 1,
  "cantidadTramos": 2,
  "cantidadDepositos": 1,
  "distanciaTotal": 731.0,
  "tiempoEstimadoMin": 551.3,
  "costoTotal": 178130.00,
  "tramos": [
    {
      "idTramo": 12,
      "latitudOrigen": -31.4201,
      "longitudOrigen": -64.1888,
      "latitudDestino": -31.6107,
      "longitudDestino": -60.6973,
      "distanciaKm": 345.8,
      "costoAproximado": 84534.00,
      "estado": {
        "idEstado": 1,
        "nombre": "pendiente"
      },
      "tipoTramo": {
        "idTipoTramo": 1,
        "nombre": "a_deposito"
      },
      "depositoDestino": {
        "idDeposito": 40,
        "nombre": "Centro Logístico Santa Fe"
      }
    },
    {
      "idTramo": 13,
      "latitudOrigen": -31.6107,
      "longitudOrigen": -60.6973,
      "latitudDestino": -34.6037,
      "longitudDestino": -58.3816,
      "distanciaKm": 385.2,
      "costoAproximado": 93596.00,
      "estado": {
        "idEstado": 1,
        "nombre": "pendiente"
      },
      "tipoTramo": {
        "idTipoTramo": 2,
        "nombre": "desde_deposito"
      },
      "depositoOrigen": {
        "idDeposito": 40,
        "nombre": "Centro Logístico Santa Fe"
      }
    }
  ]
}
```

---

## 💰 Cálculo de Costos

### Fórmula por Tramo:

```
Costo Tramo = (Distancia × Costo Base/km) + 
              (Distancia × Costo Combustible/km) + 
              Cargo Gestión

Donde:
- Costo Base/km = $150
- Costo Combustible/km = $80
- Cargo Gestión = $5,000

Ejemplo para 345.8 km:
= (345.8 × 150) + (345.8 × 80) + 5,000
= 51,870 + 27,664 + 5,000
= $84,534
```

### Costo Total de la Ruta:

```
Costo Total = Σ(Costo de cada tramo) + Σ(Estadías en depósitos)
```

---

## 🔄 Flujo Completo del Usuario

### Paso 1: Generar Rutas Tentativas
```
Usuario → POST /api/rutas/tentativas
       ← Recibe 3 opciones de ruta
```

### Paso 2: Seleccionar Ruta
```
Usuario analiza:
- ⏱️ Tiempo de entrega
- 💰 Costo total
- 🏢 Depósitos intermedios
- 🎯 Necesidades específicas
```

### Paso 3: Crear Ruta Definitiva
```
Usuario → POST /api/rutas/crear-desde-tentativa
        (Envía la ruta seleccionada con todos sus datos)
       ← Recibe ruta creada en BD con ID
```

### Paso 4: Asignar a Solicitud
```
Usuario → PUT /api/solicitudes/{id}/asignar-ruta
        Body: { "idRuta": 5 }
       ← Solicitud actualizada con ruta asignada
```

---

## 📊 Comparación de Rutas

| Característica | Directa | 1 Depósito | 2 Depósitos |
|----------------|---------|------------|-------------|
| **Tramos** | 1 | 2 | 3 |
| **Tiempo** | ⚡ Rápido | ⏱️ Medio | ⏳ Lento |
| **Costo** | 💰 Bajo | 💰💰 Medio | 💰💰💰 Alto |
| **Flexibilidad** | ❌ Baja | ✅ Media | ✅✅ Alta |
| **Respaldo** | ❌ Ninguno | ✅ 1 punto | ✅✅ 2 puntos |
| **Uso Ideal** | Distancias cortas | Distancias medias | Distancias largas |

---

## 🛠️ Configuración

### Constantes de Costo (RutasTentativasService.java)

```java
private static final double COSTO_BASE_POR_KM = 150.0;
private static final double COSTO_COMBUSTIBLE_POR_KM = 80.0;
private static final double CARGO_GESTION_TRAMO = 5000.0;
```

Puedes ajustar estos valores según tus necesidades de negocio.

---

## ✅ Estados de los Tramos

Cuando se crea una ruta definitiva, los tramos inician en estado **"pendiente"**:

1. **pendiente** - Tramo creado, esperando asignación de camión
2. **asignado** - Camión asignado al tramo
3. **iniciado** - Tramo en curso
4. **finalizado** - Tramo completado

---

## 🎯 Casos de Uso

### Caso 1: Envío Urgente
**Seleccionar:** Ruta Directa
- ⚡ Menor tiempo de entrega
- 💰 Menor costo
- 🎯 Sin demoras por paradas

### Caso 2: Carga Consolidada
**Seleccionar:** Ruta con 1 Depósito
- 📦 Posibilidad de agregar más carga en el depósito
- 🔄 Cambio de vehículo si es necesario
- 🏢 Respaldo en caso de problemas

### Caso 3: Envío de Alta Importancia
**Seleccionar:** Ruta con 2 Depósitos
- ✅✅ Máximo control y seguridad
- 🔄 Múltiples puntos de verificación
- 📍 Tracking preciso en cada etapa

---

## 🔍 Validaciones Implementadas

✅ Coordenadas dentro de rangos válidos (-90 a 90 lat, -180 a 180 lon)
✅ Depósitos válidos y existentes en la base de datos
✅ OSRM responde correctamente para cada tramo
✅ Cálculos matemáticos precisos (BigDecimal)
✅ Logging detallado de todo el proceso
✅ Manejo de errores robusto

---

## 📝 Notas Importantes

1. **OSRM:** Usa la API pública de OSRM. Para producción, considera instalar tu propio servidor OSRM.

2. **Depósitos:** Asegúrate de cargar los depósitos en la base de datos usando el script SQL proporcionado.

3. **Permisos:** Los endpoints requieren rol ADMIN u OPERADOR.

4. **Performance:** Las consultas a OSRM son síncronas. Para mejor performance, considera hacerlas asíncronas.

5. **Costos:** Los costos son aproximados. El costo real se calcula al finalizar cada tramo considerando variables adicionales.

---

## 🚀 ¡Listo para Usar!

El sistema está completamente implementado y listo para generar rutas tentativas inteligentes usando OSRM y datos reales de depósitos en Argentina.

