# 🐛 Error: Unable to commit against JDBC Connection

## ❌ Problema Identificado

Al crear una solicitud (POST), el log mostraba:
```
✅ Solicitud creada exitosamente con ID: X - Estado: disponible (ID=1)
```

Pero luego fallaba con:
```
Error: Unable to commit against JDBC Connection
```

---

## 🔍 Causa Raíz

En el modelo `Solicitud.java`, la relación con `Estado` tenía:

```java
// ❌ INCORRECTO
@ManyToOne(cascade = CascadeType.ALL)
@JoinColumn(name = "id_estado", referencedColumnName = "id_estado")
private Estado estadoSolicitud;
```

### ¿Por qué fallaba?

**`CascadeType.ALL`** le dice a Hibernate:
- "Cuando guardes una Solicitud, también guarda/actualiza/elimina el Estado asociado"

**El problema:**
1. Los estados YA EXISTEN en la base de datos (ID 1, 2, 3)
2. Al guardar la solicitud, Hibernate intentaba **insertar/actualizar** el estado
3. SQLite detectaba un conflicto (el estado ya existe) o violación de restricción
4. La transacción fallaba al hacer **commit**

---

## ✅ Solución Aplicada

### Cambio en `Solicitud.java`:

```java
// ✅ CORRECTO
@ManyToOne
@JoinColumn(name = "id_estado", referencedColumnName = "id_estado")
private Estado estadoSolicitud;
```

**Sin `cascade`**, Hibernate solo:
- ✅ Lee el estado existente de la BD
- ✅ Crea la referencia en la solicitud (guarda solo el ID)
- ✅ NO intenta modificar la tabla `estado`

---

## 📊 Regla General para Cascade

### ✅ USA `cascade` cuando:
- La entidad "hijo" es creada/eliminada junto con el "padre"
- Ejemplo: `Ruta` → `List<Tramo>` (los tramos se crean con la ruta)

```java
@OneToMany(mappedBy = "ruta", cascade = CascadeType.ALL)
private List<Tramo> tramos;
```

### ❌ NO uses `cascade` cuando:
- La entidad es un **catálogo fijo** (Estado, TipoVehiculo, etc.)
- La entidad ya existe y solo necesitas referenciarla
- No eres "dueño" del ciclo de vida de esa entidad

```java
// ❌ NO usar cascade con estados fijos
@ManyToOne(cascade = CascadeType.ALL)  // INCORRECTO
private Estado estadoSolicitud;

// ✅ Correcto
@ManyToOne
private Estado estadoSolicitud;
```

---

## 🔧 Cómo Funciona Ahora

### 1️⃣ Al crear solicitud:

```java
Estado estado = estadoService.obtenerEstadoDisponible(); // Obtiene ID=1 de BD
solicitud.setEstadoSolicitud(estado);                    // Solo asigna referencia
repo.save(solicitud);                                    // Guarda solicitud con id_estado=1
```

**SQL generado:**
```sql
INSERT INTO solicitud (id_cliente, id_contenedor, id_estado) 
VALUES (?, ?, 1);
```

**NO genera:**
```sql
-- Hibernate NO intenta esto porque no hay cascade:
INSERT INTO estado (id_estado, nombre) VALUES (1, 'disponible'); -- ❌
```

---

## 🎯 Resultado

Ahora el POST funciona correctamente:

**Request:**
```http
POST /api/solicitudes/completa
Content-Type: application/json

{
  "dniCliente": "12345678",
  "nombreCliente": "Juan",
  "apellidoCliente": "Pérez",
  "telefonoCliente": "123456789",
  "mailCliente": "juan@mail.com",
  "direccionCliente": "Calle 123",
  "pesoContenedor": 1500.0,
  "volumenContenedor": 20.5
}
```

**Response (200 OK):**
```json
{
  "numeroSolicitud": 1,
  "cliente": {
    "idCliente": 1,
    "nombre": "Juan",
    "apellido": "Pérez"
  },
  "contenedor": {
    "idContenedor": 1,
    "peso": 1500.0,
    "volumen": 20.5,
    "estado": "disponible"
  },
  "estadoSolicitud": {
    "idEstado": 1,
    "nombre": "disponible"
  },
  "costoEstimado": null,
  "costoFinal": null
}
```

---

## 📝 Verificación en Base de Datos

Después del POST exitoso:

```sql
SELECT * FROM solicitud;
-- numero_solicitud | id_cliente | id_contenedor | id_estado | id_ruta
-- 1                | 1          | 1             | 1         | NULL

SELECT * FROM estado;
-- id_estado | nombre
-- 1         | disponible
-- 2         | en proceso
-- 3         | completada
```

✅ La tabla `estado` NO se modifica (sigue con sus 3 registros fijos)
✅ La tabla `solicitud` tiene la referencia correcta a `id_estado = 1`

---

## 🚀 Resumen

**Problema:** `CascadeType.ALL` intentaba modificar estados fijos en la BD
**Solución:** Eliminar `cascade` de la relación `Solicitud` → `Estado`
**Resultado:** Transacción exitosa, sin intentar modificar tabla `estado`

---

## ⚠️ Otros Casos a Revisar

Si tienes otras entidades con catálogos fijos (como tipos, categorías, roles), verifica que NO tengan `cascade`:

```java
// Revisar modelos que referencian catálogos:
@ManyToOne  // ✅ Sin cascade
@JoinColumn(name = "id_tipo_vehiculo")
private TipoVehiculo tipoVehiculo;

@ManyToOne  // ✅ Sin cascade
@JoinColumn(name = "id_categoria")
private Categoria categoria;
```

