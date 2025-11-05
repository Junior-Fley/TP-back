# Microservicio Solicitudes - Implementación de Cliente

## Resumen
Se ha implementado la entidad **Cliente** dentro del microservicio de Solicitudes, junto con todas las capas necesarias siguiendo el patrón de arquitectura en capas.

## Estructura Implementada

### 1. Modelo (Entity)
**Archivo:** `models/Cliente.java`

Entidad JPA con los siguientes atributos:
- `idCliente` (Long) - Primary Key
- `nombre` (String)
- `apellido` (String)
- `dni` (String)
- `telefono` (String)
- `mail` (String)
- `direccion` (String)

### 2. Repositorio
**Archivo:** `repositories/ClienteRepository.java`

Interface que extiende `JpaRepository` con métodos de consulta:
- `findByDni(String dni)` - Buscar cliente por DNI
- `findByMail(String mail)` - Buscar cliente por email
- `existsByDni(String dni)` - Verificar si existe DNI
- `existsByMail(String mail)` - Verificar si existe email

### 3. Servicio
**Archivo:** `services/ClienteService.java`

Capa de lógica de negocio con los siguientes métodos:
- `listar()` - Obtener todos los clientes
- `obtenerPorId(Long id)` - Obtener cliente por ID
- `obtenerPorDni(String dni)` - Obtener cliente por DNI
- `obtenerPorMail(String mail)` - Obtener cliente por email
- `crear(Cliente cliente)` - Crear nuevo cliente (con validación de DNI y email únicos)
- `actualizar(Long id, Cliente actualizado)` - Actualizar cliente existente
- `eliminar(Long id)` - Eliminar cliente

### 4. Controlador REST
**Archivo:** `controllers/ClienteController.java`

Endpoints REST expuestos en `/api/clientes`:

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/clientes` | Listar todos los clientes |
| GET | `/api/clientes/{id}` | Obtener cliente por ID |
| GET | `/api/clientes/dni/{dni}` | Obtener cliente por DNI |
| GET | `/api/clientes/mail/{mail}` | Obtener cliente por email |
| POST | `/api/clientes` | Crear nuevo cliente |
| PUT | `/api/clientes/{id}` | Actualizar cliente |
| DELETE | `/api/clientes/{id}` | Eliminar cliente |

## Relación con Solicitud

La entidad `Solicitud` ahora tiene una relación `@ManyToOne` con `Cliente`:

```java
@ManyToOne
@JoinColumn(name = "id_cliente", insertable = false, updatable = false)
private Cliente cliente;
```

## Datos de Prueba

Se agregaron 9 clientes de prueba en `solicitud_data.sql`:
- IDs del 201 al 209
- Con datos completos (nombre, apellido, DNI, teléfono, email, dirección)

## Archivo de Pruebas HTTP

Se actualizó el archivo `client.http` con ejemplos de peticiones para todos los endpoints de clientes, contenedores y solicitudes.

## Tecnologías Utilizadas
- Spring Boot
- Spring Data JPA
- Lombok
- H2 Database (en memoria)
- Jackson para serialización JSON

## Próximos Pasos
- El microservicio está listo para ejecutarse
- Los endpoints pueden probarse usando el archivo `client.http`
- La base de datos H2 se inicializará automáticamente con los datos de prueba

