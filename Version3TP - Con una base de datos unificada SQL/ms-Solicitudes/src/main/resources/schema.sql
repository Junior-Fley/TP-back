-- Script para asegurar que la tabla solicitud tenga todas las columnas necesarias
-- SQLite no soporta ALTER TABLE IF NOT EXISTS, así que usamos un enfoque diferente

-- Crear tabla temporal con la estructura correcta
CREATE TABLE IF NOT EXISTS solicitud_new (
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

-- Copiar datos de la tabla antigua si existe
INSERT OR IGNORE INTO solicitud_new (numero_solicitud, id_contenedor, id_cliente, costo_estimado, tiempo_estimado, costo_final, tiempo_real, id_tarifa, id_estado)
SELECT numero_solicitud, id_contenedor, id_cliente, costo_estimado, tiempo_estimado, costo_final, tiempo_real, id_tarifa, id_estado
FROM solicitud WHERE EXISTS (SELECT 1 FROM sqlite_master WHERE type='table' AND name='solicitud');

-- Eliminar tabla antigua
DROP TABLE IF EXISTS solicitud;

-- Renombrar tabla nueva
ALTER TABLE solicitud_new RENAME TO solicitud;

