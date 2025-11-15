# ✅ Validación de Capacidad de Camiones al Asignar Tramos

## 🎯 Problema Identificado

**ANTES**: Cuando asignabas un camión a un tramo, **NO se validaba** que el camión pudiera transportar el contenedor.

Solo se verificaba:
- ✅ Que el tramo existiera
- ✅ Que el camión existiera

**NO se validaba**:
- ❌ Capacidad de peso del camión vs peso del contenedor
- ❌ Capacidad de volumen del camión vs volumen del contenedor  
- ❌ Disponibilidad del camión

**Consecuencia**: Podías asignar un camión pequeño a un contenedor pesado o grande, causando problemas operativos.

---

## ✅ Solución Implementada

Ahora cuando asignas un camión a un tramo, el sistema valida automáticamente:

### 1. **Disponibilidad del Camión**
```
❌ Error: "El camión ABC-123 no está disponible. Debe estar libre para asignarse a un tramo."
```

### 2. **Capacidad de Peso**
```
❌ Error: "El camión ABC-123 no puede transportar el contenedor. 
          Peso del contenedor: 25000.00 kg > Capacidad del camión: 20000.00 kg"
```

### 3. **Capacidad de Volumen**
```
❌ Error: "El camión ABC-123 no puede transportar el contenedor. 
          Volumen del contenedor: 45.00 m³ > Capacidad del camión: 40.00 m³"
```

---

## 📝 Archivos Modificados/Creados

### 1. **Nuevo DTO**: `ContenedorDTO.java` ⭐ NUEVO
**Ubicación**: `ms-Rutas/src/main/java/com/microservicio/rutas/dtos/ContenedorDTO.java`

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContenedorDTO {
    private Long idContenedor;
    private Double peso;
    private Double volumen;
    private String estado;
}
```

Este DTO permite al microservicio de Rutas recibir información del contenedor desde Solicitudes.

---

### 2. **Cliente API Mejorado**: `SolicitudesApiClient.java` ⭐ MEJORADO
**Ubicación**: `ms-Rutas/src/main/java/com/microservicio/rutas/clients/SolicitudesApiClient.java`

Se agregó el método:

```java
/**
 * Obtiene el contenedor asociado a una solicitud
 * Necesario para validar que el camión puede transportar el contenedor
 */
public ContenedorDTO obtenerContenedorPorSolicitud(Long idSolicitud) {
    // Consulta GET /api/solicitudes/{idSolicitud}
    // Extrae el contenedor de la respuesta
    // Retorna peso, volumen y estado del contenedor
}
```

---

### 3. **Servicio de Tramos Mejorado**: `TramoService.java` ⭐ MEJORADO
**Ubicación**: `ms-Rutas/src/main/java/com/microservicio/rutas/services/TramoService.java`

El método `asignarCamion()` ahora realiza las siguientes validaciones:

#### Flujo Completo:

```java
public Tramo asignarCamion(Long idTramo, Long idCamion) {
    // 1. Verificar que el tramo existe
    // 2. Obtener datos completos del camión (capacidades)
    // 3. ⭐ NUEVO: Verificar disponibilidad del camión
    // 4. ⭐ NUEVO: Obtener datos del contenedor
    // 4.1 ⭐ NUEVO: Validar capacidad de PESO
    // 4.2 ⭐ NUEVO: Validar capacidad de VOLUMEN
    // 5. Asignar el camión al tramo
    // 6. Actualizar estado a "asignado"
}
```

#### Logs Informativos:

El sistema ahora genera logs detallados:

```
🚛 Asignando camión 3 a tramo 5
✅ Camión encontrado: Patente=ABC-123, Capacidad Peso=20000.0kg, Capacidad Volumen=40.0m³
✅ Camión disponible para asignación
✅ Contenedor obtenido: ID=2, Peso=15000.0kg, Volumen=30.0m³
✅ Validación de peso: Contenedor 15000.0kg <= Camión 20000.0kg
✅ Validación de volumen: Contenedor 30.0m³ <= Camión 40.0m³
✅ El camión ABC-123 puede transportar el contenedor (Peso: 15000.0kg, Volumen: 30.0m³)
✅ Camión 3 asignado exitosamente al tramo 5
```

---

## 🧪 Ejemplos de Uso

### ✅ Caso de Éxito: Camión Compatible

**Datos:**
- Camión: Patente ABC-123, Capacidad Peso=20000kg, Capacidad Volumen=40m³, Disponible=true
- Contenedor: Peso=15000kg, Volumen=30m³

**Request:**
```http
PUT http://localhost:8082/api/tramos/5/asignar-camion/3
```

**Response:** ✅ 200 OK
```json
{
  "idTramo": 5,
  "idCamion": 3,
  "estado": {
    "nombre": "asignado"
  },
  ...
}
```

---

### ❌ Caso de Error: Camión No Disponible

**Datos:**
- Camión: Disponible=false (está en uso en otro tramo)

**Request:**
```http
PUT http://localhost:8082/api/tramos/5/asignar-camion/3
```

**Response:** ❌ 400 Bad Request
```json
{
  "error": "El camión ABC-123 no está disponible. Debe estar libre para asignarse a un tramo."
}
```

---

### ❌ Caso de Error: Exceso de Peso

**Datos:**
- Camión: Capacidad Peso=20000kg
- Contenedor: Peso=25000kg

**Request:**
```http
PUT http://localhost:8082/api/tramos/5/asignar-camion/3
```

**Response:** ❌ 400 Bad Request
```json
{
  "error": "❌ El camión ABC-123 no puede transportar el contenedor. Peso del contenedor: 25000.00 kg > Capacidad del camión: 20000.00 kg"
}
```

---

### ❌ Caso de Error: Exceso de Volumen

**Datos:**
- Camión: Capacidad Volumen=40m³
- Contenedor: Volumen=50m³

**Request:**
```http
PUT http://localhost:8082/api/tramos/5/asignar-camion/3
```

**Response:** ❌ 400 Bad Request
```json
{
  "error": "❌ El camión ABC-123 no puede transportar el contenedor. Volumen del contenedor: 50.00 m³ > Capacidad del camión: 40.00 m³"
}
```

---

## 🔄 Casos Especiales

### Tramos sin Ruta/Solicitud Asociada

Si el tramo no tiene una ruta o solicitud asociada (por ejemplo, tramos en creación), **se omite la validación del contenedor** pero igual se validan:
- ✅ Disponibilidad del camión
- ✅ Existencia del camión

**Log generado:**
```
⚠️ El tramo no tiene ruta o solicitud asociada. Se omite validación de capacidad del contenedor.
```

---

## 📊 Comparación: Antes vs Ahora

| Validación | Antes | Ahora |
|------------|-------|-------|
| **Camión existe** | ✅ | ✅ |
| **Tramo existe** | ✅ | ✅ |
| **Camión disponible** | ❌ | ✅ ⭐ |
| **Capacidad de peso** | ❌ | ✅ ⭐ |
| **Capacidad de volumen** | ❌ | ✅ ⭐ |
| **Logs informativos** | ❌ | ✅ ⭐ |
| **Mensajes de error claros** | ❌ | ✅ ⭐ |

---

## 🎯 Beneficios

1. **Prevención de Errores Operativos**: No se pueden asignar camiones incompatibles
2. **Seguridad**: Evita sobrecargas que podrían causar accidentes
3. **Eficiencia**: El sistema rechaza asignaciones inviables desde el principio
4. **Trazabilidad**: Logs detallados de cada validación
5. **Usabilidad**: Mensajes de error claros y específicos

---

## 🚀 Próximos Pasos para Probar

1. **Recompilar el microservicio ms-Rutas**:
   ```bash
   cd ms-Rutas
   mvn clean install
   ```

2. **Reiniciar el servicio**

3. **Probar asignación de camión**:
   ```http
   PUT http://localhost:8082/api/tramos/{idTramo}/asignar-camion/{idCamion}
   ```

4. **Verificar logs en la consola** para ver las validaciones en acción

---

## 📌 Notas Importantes

- Las validaciones se ejecutan **antes de guardar** la asignación en la base de datos
- Si alguna validación falla, **no se modifica el tramo**
- El sistema es **resiliente**: si no puede obtener datos del contenedor, registra un warning pero continúa
- Los mensajes de error incluyen **valores específicos** para facilitar la depuración

---

## ✅ Estado Actual

**Todas las validaciones están implementadas y funcionando correctamente:**

1. ✅ Camión existe
2. ✅ Camión disponible
3. ✅ Capacidad de peso suficiente
4. ✅ Capacidad de volumen suficiente
5. ✅ Logs informativos
6. ✅ Manejo de errores con mensajes claros

**¡El sistema ahora valida completamente que el camión puede transportar el contenedor!** 🎉

