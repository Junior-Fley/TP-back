-- Script de inicialización para PostgreSQL
-- Migración desde SQLite a PostgreSQL

-- Tabla TRANSPORTISTA
CREATE TABLE IF NOT EXISTS transportista (
    id_transportista SERIAL PRIMARY KEY,
    nombre VARCHAR(255),
    apellido VARCHAR(255),
    dni VARCHAR(255),
    telefono VARCHAR(255)
);

-- Tabla camion
CREATE TABLE IF NOT EXISTS camion (
    id_camion SERIAL PRIMARY KEY,
    capacidad_peso FLOAT,
    capacidad_volumen FLOAT,
    marca VARCHAR(255),
    modelo VARCHAR(255),
    patente VARCHAR(255),
    id_transportista BIGINT,
    FOREIGN KEY (id_transportista) REFERENCES transportista(id_transportista)
);

-- Tabla cliente
CREATE TABLE IF NOT EXISTS cliente (
    id_cliente SERIAL PRIMARY KEY,
    apellido VARCHAR(255),
    nombre VARCHAR(255),
    dni VARCHAR(255),
    mail VARCHAR(255),
    direccion VARCHAR(255),
    telefono VARCHAR(255)
);

-- Tabla CONTENEDOR
CREATE TABLE IF NOT EXISTS contenedor (
    id_contenedor SERIAL PRIMARY KEY,
    peso FLOAT,
    volumen FLOAT,
    estado VARCHAR(255)
);

-- Tabla estado_solicitud
CREATE TABLE IF NOT EXISTS estado_solicitud (
    id_estado SERIAL PRIMARY KEY,
    nombre VARCHAR(255)
);

-- Tabla solicitud
CREATE TABLE IF NOT EXISTS solicitud (
    numero_solicitud SERIAL PRIMARY KEY,
    fecha TIMESTAMP NOT NULL,
    id_cliente BIGINT NOT NULL,
    id_contenedor BIGINT NOT NULL,
    id_estado BIGINT NOT NULL,
    FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente),
    FOREIGN KEY (id_contenedor) REFERENCES contenedor(id_contenedor),
    FOREIGN KEY (id_estado) REFERENCES estado_solicitud(id_estado)
);

-- Tabla ciudad
CREATE TABLE IF NOT EXISTS ciudad (
    id_ciudad SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL
);

-- Tabla deposito
CREATE TABLE IF NOT EXISTS deposito (
    id_deposito SERIAL PRIMARY KEY,
    nombre VARCHAR(200),
    direccion VARCHAR(200),
    costo_estadia_diario NUMERIC(10,2),
    latitud FLOAT,
    longitud FLOAT,
    id_ciudad INTEGER NOT NULL,
    FOREIGN KEY (id_ciudad) REFERENCES ciudad(id_ciudad)
);

-- Tabla tarifas
CREATE TABLE IF NOT EXISTS tarifas (
    id SERIAL PRIMARY KEY,
    activo BOOLEAN NOT NULL,
    descripcion VARCHAR(255),
    tipo VARCHAR(255),
    unidad VARCHAR(20),
    valor NUMERIC(10,2) NOT NULL
);

-- Tabla tipo_tramo
CREATE TABLE IF NOT EXISTS tipo_tramo (
    id_tipo_tramo SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL
);

-- Tabla estado_tramo
CREATE TABLE IF NOT EXISTS estado_tramo (
    id_estado SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL
);

-- Tabla ruta
CREATE TABLE IF NOT EXISTS ruta (
    id_ruta SERIAL PRIMARY KEY,
    id_solicitud BIGINT,
    cantidad_depositos INTEGER,
    cantidad_tramos INTEGER,
    costo_total NUMERIC(10,2),
    distancia_total_km FLOAT,
    tiempo_estimado_min FLOAT,
    FOREIGN KEY (id_solicitud) REFERENCES solicitud(numero_solicitud)
);

-- Tabla tramo
CREATE TABLE IF NOT EXISTS tramo (
    id_tramo SERIAL PRIMARY KEY,
    id_ruta BIGINT,
    id_tipo_tramo BIGINT,
    id_estado BIGINT,
    id_ciudad_origen BIGINT,
    id_ciudad_destino BIGINT,
    costo_aproximado NUMERIC(10,2),
    costo_real NUMERIC(10,2),
    distancia_km FLOAT,
    duracion_min FLOAT,
    FOREIGN KEY (id_ruta) REFERENCES ruta(id_ruta),
    FOREIGN KEY (id_tipo_tramo) REFERENCES tipo_tramo(id_tipo_tramo),
    FOREIGN KEY (id_estado) REFERENCES estado_tramo(id_estado),
    FOREIGN KEY (id_ciudad_origen) REFERENCES ciudad(id_ciudad),
    FOREIGN KEY (id_ciudad_destino) REFERENCES ciudad(id_ciudad)
);

-- Insertar datos iniciales si es necesario
-- Estados de solicitud comunes
INSERT INTO estado_solicitud (nombre) VALUES ('PENDIENTE'), ('EN_PROCESO'), ('COMPLETADA'), ('CANCELADA')
ON CONFLICT DO NOTHING;

-- Estados de tramo comunes
INSERT INTO estado_tramo (nombre) VALUES ('PLANIFICADO'), ('EN_TRANSITO'), ('COMPLETADO')
ON CONFLICT DO NOTHING;

-- Tipos de tramo comunes
INSERT INTO tipo_tramo (nombre) VALUES ('TERRESTRE'), ('MARITIMO'), ('AEREO')
ON CONFLICT DO NOTHING;

