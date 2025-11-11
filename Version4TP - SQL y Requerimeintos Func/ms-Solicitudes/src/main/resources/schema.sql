-- Script para crear la estructura de la base de datos de solicitudes

-- Tabla de estados
CREATE TABLE IF NOT EXISTS estado (
    idEstado INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre VARCHAR(50) NOT NULL
);

-- Tabla de clientes
CREATE TABLE IF NOT EXISTS cliente (
    id_cliente INTEGER PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    dni VARCHAR(20) NOT NULL,
    telefono VARCHAR(20),
    mail VARCHAR(100),
    direccion VARCHAR(255)
);

-- Tabla de contenedores
CREATE TABLE IF NOT EXISTS contenedor (
    id_contenedor INTEGER PRIMARY KEY,
    peso DECIMAL(10,2),
    volumen DECIMAL(10,2)
);

-- Eliminar tabla solicitud si existe (para recrearla con la estructura correcta)
DROP TABLE IF EXISTS solicitud;

-- Tabla de solicitudes con todas las columnas necesarias
CREATE TABLE solicitud (
    numero_solicitud INTEGER PRIMARY KEY AUTOINCREMENT,
    id_contenedor INTEGER,
    id_cliente INTEGER NOT NULL,
    costo_estimado DECIMAL(10,2),
    tiempo_estimado INTEGER,
    costo_final DECIMAL(10,2),
    tiempo_real INTEGER,
    id_tarifa INTEGER,
    id_estado INTEGER,
    id_ruta INTEGER,
    FOREIGN KEY (id_contenedor) REFERENCES contenedor(id_contenedor),
    FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente),
    FOREIGN KEY (id_estado) REFERENCES estado(idEstado)
);
