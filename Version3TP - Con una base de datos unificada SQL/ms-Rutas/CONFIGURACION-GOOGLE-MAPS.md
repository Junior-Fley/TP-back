# Configuración de Google Maps API

## Problema: REQUEST_DENIED y Billing Requerido

El error `REQUEST_DENIED` con el mensaje "You must enable Billing" indica que Google Maps API requiere una cuenta de facturación configurada en Google Cloud, incluso para usar el crédito gratuito mensual de $200 USD.

## ✅ Solución Implementada: Modo Sin Google Maps

He configurado el sistema para **NO usar Google Maps por defecto** y en su lugar usar cálculos matemáticos directos (fórmula de Haversine).

### Configuración Actual (application.properties):
```properties
google.maps.enabled=false
```

Con esta configuración:
- ✅ **No verás más errores** de API Key o billing en los logs
- ✅ El sistema usa **cálculo euclidiano** (distancia en línea recta sobre la superficie terrestre)
- ✅ Funciona **sin costos** y sin límites de consultas
- ✅ Cálculos **instantáneos** sin latencia de red

### Al iniciar el microservicio verás:
```
⚠️ Google Maps deshabilitado. Se usará cálculo euclidiano para todas las distancias.
```

### Al calcular distancias verás:
```
📐 Distancia euclidiana calculada: 123.45 km
⏱️ Tiempo estimado: 123 minutos (basado en 60 km/h)
```

---

## Si Deseas Habilitar Google Maps en el Futuro

### Opción 1: Habilitar Billing en Google Cloud (Recomendado para producción)

1. **Ir a Google Cloud Console**
   - Accede a: https://console.cloud.google.com/

2. **Habilitar Facturación**
   - Ve a "Facturación" en el menú
   - Agrega una tarjeta de crédito/débito
   - **IMPORTANTE**: Los primeros $200 USD/mes son GRATIS
   - Solo pagas si excedes ese límite

3. **Habilitar las APIs necesarias**
   - Ve a "APIs y servicios" > "Biblioteca"
   - Busca y habilita:
     - ✅ **Directions API** (requerida)
     - ✅ **Distance Matrix API** (opcional)

4. **Crear/Obtener API Key**
   - Ve a "APIs y servicios" > "Credenciales"
   - Crea una nueva API Key o usa una existente

5. **Actualizar application.properties**
   ```properties
   google.maps.enabled=true
   google.maps.api.key=TU_API_KEY_VALIDA
   google.maps.api.base-url=https://maps.googleapis.com/maps/api
   ```

### Opción 2: Continuar Sin Google Maps (Recomendado para desarrollo)

Simplemente mantén la configuración actual:
```properties
google.maps.enabled=false
```

---

## Comparación: Google Maps vs Cálculo Euclidiano

| Característica | Google Maps API | Cálculo Euclidiano |
|----------------|-----------------|-------------------|
| **Precisión** | ✅ Alta (rutas reales) | ⚠️ Media (línea recta) |
| **Costo** | 💰 $200 gratis/mes, luego $5/1000 | ✅ Gratis |
| **Requiere billing** | ❌ Sí | ✅ No |
| **Velocidad** | ⚠️ Depende de red | ✅ Instantáneo |
| **Límites** | ⚠️ Sí (cuota API) | ✅ Ilimitado |
| **Considera obstáculos** | ✅ Sí (rutas, tráfico) | ❌ No |

---

## Recomendaciones

### Para Desarrollo y Pruebas:
- ✅ **Usa google.maps.enabled=false**
- Más rápido, sin configuración, sin costos

### Para Producción:
- Evalúa si necesitas precisión de rutas reales
- Si sí: Habilita billing en Google Cloud y activa Google Maps
- Si no: El cálculo euclidiano es suficiente para distancias aproximadas

---

## Fórmula de Haversine (Usada en el Cálculo Euclidiano)

La fórmula de Haversine calcula la distancia entre dos puntos sobre una esfera (la Tierra):

```
a = sin²(Δlat/2) + cos(lat1) * cos(lat2) * sin²(Δlong/2)
c = 2 * atan2(√a, √(1−a))
distancia = R * c
```

Donde R = 6371 km (radio promedio de la Tierra)

Esta fórmula proporciona una **excelente aproximación** para distancias de transporte terrestre.

---

## Costos de Google Maps API (Solo si habilitas billing)

- **Primeros $200 USD/mes**: Gratis (crédito mensual recurrente)
- **Directions API**: ~$5 USD por 1000 solicitudes
- **Ejemplo**: Con $200 gratis = ~40,000 solicitudes gratis/mes

**Monitoreo**: Google Cloud Console te permite ver tu uso en tiempo real.

Para más información: https://mapsplatform.google.com/pricing/
