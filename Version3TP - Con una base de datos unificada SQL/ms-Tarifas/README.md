# Microservicio de Tarifas

## Descripción
Microservicio para gestionar las tarifas del sistema de transporte de contenedores.

## Funcionalidades

### Tarifas configurables
- **COSTO_KM_BASE**: Costo base por kilómetro recorrido
- **COMBUSTIBLE**: Precio del combustible por litro
- **ESTADIA_DEPOSITO**: Costo de estadía en depósito por día

### Endpoints principales

#### GET /api/tarifas
Obtiene todas las tarifas del sistema

#### GET /api/tarifas/activas
Obtiene solo las tarifas activas

#### GET /api/tarifas/{id}
Obtiene una tarifa específica por ID

#### POST /api/tarifas
Crea una nueva tarifa
```json
{
  "tipo": "COSTO_KM_BASE",
  "descripcion": "Costo base por kilómetro",
  "valor": 5.00,
  "unidad": "km",
  "activo": true
}
```

#### PUT /api/tarifas/{id}
Actualiza una tarifa existente

#### DELETE /api/tarifas/{id}
Desactiva una tarifa

#### POST /api/tarifas/calcular-costo
Calcula el costo total de un transporte
```json
{
  "distanciaKm": 150.5,
  "volumenM3": 20.0,
  "pesoKg": 5000.0,
  "diasEstadia": 2,
  "consumoCombustibleLitrosPorKm": 0.35
}
```

Respuesta:
```json
{
  "distanciaKm": 150.5,
  "volumenM3": 20.0,
  "pesoKg": 5000.0,
  "diasEstadia": 2,
  "consumoCombustibleLitrosPorKm": 0.35,
  "costoKilometraje": 752.50,
  "costoCombustible": 78.99,
  "costoEstadia": 100.00,
  "costoTotal": 931.49
}
```

## Swagger UI
http://localhost:8092/swagger-ui.html

## Ejecutar localmente
```bash
cd ms-Tarifas
mvn clean install
mvn spring-boot:run
```

## Datos iniciales
Al iniciar, el microservicio crea automáticamente 3 tarifas por defecto si no existen.

