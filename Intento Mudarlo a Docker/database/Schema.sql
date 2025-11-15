-- =========================================
--   TABLA CLIENTE
-- =========================================
CREATE TABLE cliente (
                         id_cliente SERIAL PRIMARY KEY,
                         apellido VARCHAR(255),
                         nombre VARCHAR(255),
                         dni VARCHAR(20),
                         mail VARCHAR(255),
                         direccion VARCHAR(255),
                         telefono VARCHAR(20)
);

-- =========================================
--   TABLA ESTADO_SOLICITUD
-- =========================================
CREATE TABLE estado_solicitud (
                                  id_estado SERIAL PRIMARY KEY,
                                  nombre VARCHAR(255) NOT NULL
);

-- =========================================
--   TABLA CONTENEDOR
-- =========================================
CREATE TABLE contenedor (
                            id_contenedor SERIAL PRIMARY KEY,
                            peso NUMERIC(10,2),
                            volumen NUMERIC(10,2),
                            estado VARCHAR(255)
);

-- =========================================
--   TABLA SOLICITUD
-- =========================================
CREATE TABLE solicitud (
                           numero_solicitud SERIAL PRIMARY KEY,
                           id_cliente INT REFERENCES cliente(id_cliente),
                           id_contenedor INT REFERENCES contenedor(id_contenedor),
                           id_estado INT REFERENCES estado_solicitud(id_estado),
                           fecha TIMESTAMP DEFAULT NOW()
);

-- =========================================
--   TABLA CIUDAD
-- =========================================
CREATE TABLE ciudad (
                        id_ciudad SERIAL PRIMARY KEY,
                        nombre VARCHAR(100) NOT NULL
);

-- =========================================
--   TABLA DEPOSITO
-- =========================================
CREATE TABLE deposito (
                          id_deposito SERIAL PRIMARY KEY,
                          nombre VARCHAR(100),
                          direccion VARCHAR(200),
                          costo_estadia_diario NUMERIC(10,2),
                          latitud NUMERIC(10,6),
                          longitud NUMERIC(10,6),
                          id_ciudad INT REFERENCES ciudad(id_ciudad)
);

-- =========================================
--   TABLA ESTADO_TRAMO
-- =========================================
CREATE TABLE estado_tramo (
                              id_estado SERIAL PRIMARY KEY,
                              nombre VARCHAR(50) NOT NULL
);

-- =========================================
--   TABLA TIPO_TRAMO
-- =========================================
CREATE TABLE tipo_tramo (
                            id_tipo_tramo SERIAL PRIMARY KEY,
                            nombre VARCHAR(50) NOT NULL
);

-- =========================================
--   TABLA TRANSPORTISTA
-- =========================================
CREATE TABLE transportista (
                               id_transportista SERIAL PRIMARY KEY,
                               nombre VARCHAR(100),
                               apellido VARCHAR(100),
                               dni VARCHAR(20),
                               telefono VARCHAR(20)
);

-- =========================================
--   TABLA CAMION
-- =========================================
CREATE TABLE camion (
                        id_camion SERIAL PRIMARY KEY,
                        capacidad_peso NUMERIC(10,2),
                        capacidad_volumen NUMERIC(10,2),
                        marca VARCHAR(255),
                        modelo VARCHAR(255),
                        patente VARCHAR(20),
                        id_transportista INT REFERENCES transportista(id_transportista)
);

-- =========================================
--   TABLA RUTA
-- =========================================
CREATE TABLE ruta (
                      id_ruta SERIAL PRIMARY KEY,
                      cantidad_depositos INT,
                      cantidad_tramos INT,
                      costo_total NUMERIC(10,2),
                      distancia_total_km NUMERIC(10,2),
                      tiempo_estimado_min NUMERIC(10,2)
);

-- =========================================
--   TABLA TRAMO
-- =========================================
CREATE TABLE tramo (
                       id_tramo SERIAL PRIMARY KEY,
                       latitud_origen NUMERIC(10,6),
                       longitud_origen NUMERIC(10,6),
                       latitud_destino NUMERIC(10,6),
                       longitud_destino NUMERIC(10,6),
                       fecha_hora_inicio TIMESTAMP,
                       fecha_hora_fin TIMESTAMP,
                       distancia_km NUMERIC(10,2),
                       costo_aproximado NUMERIC(10,2),
                       costo_real NUMERIC(10,2),

                       id_deposito_origen INT REFERENCES deposito(id_deposito),
                       id_deposito_destino INT REFERENCES deposito(id_deposito),
                       id_camion INT REFERENCES camion(id_camion),
                       id_ruta INT REFERENCES ruta(id_ruta),
                       id_tipo_tramo INT REFERENCES tipo_tramo(id_tipo_tramo),
                       id_estado INT REFERENCES estado_tramo(id_estado)
);


-- =========================================
--   TABLA TARIFAS
-- =========================================
CREATE TABLE tarifas (
                         id SERIAL PRIMARY KEY,
                         activo BOOLEAN NOT NULL,
                         descripcion VARCHAR(255),
                         tipo VARCHAR(255),
                         unidad VARCHAR(20),
                         valor NUMERIC(10,2) NOT NULL
);
