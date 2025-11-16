-- =======================================
-- SCRIPT DE INICIALIZACIÓN Y DATOS POSTGRESQL
-- Base de Datos: Sistema de Gestión de Transporte
-- =======================================

-- =======================================
-- TABLA: TRANSPORTISTA
-- =======================================
CREATE TABLE IF NOT EXISTS transportista (
    id_transportista BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    apellido VARCHAR(255),
    dni VARCHAR(50),
    telefono VARCHAR(50),
    mail VARCHAR(255),
    direccion VARCHAR(255)
);

-- =======================================
-- TABLA: CAMION
-- =======================================
CREATE TABLE IF NOT EXISTS camion (
    id_camion BIGSERIAL PRIMARY KEY,
    patente VARCHAR(255) NOT NULL UNIQUE,
    telefono VARCHAR(50),
    capacidad_peso DOUBLE PRECISION NOT NULL,
    capacidad_volumen DOUBLE PRECISION NOT NULL,
    disponibilidad BOOLEAN DEFAULT true,
    costo_base_km DOUBLE PRECISION NOT NULL,
    consumo_combustible_km DOUBLE PRECISION NOT NULL,
    id_transportista BIGINT REFERENCES transportista(id_transportista) ON DELETE SET NULL
);

-- =======================================
-- TABLA: CLIENTE
-- =======================================
CREATE TABLE IF NOT EXISTS cliente (
    id_cliente BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255),
    apellido VARCHAR(255),
    dni VARCHAR(255),
    telefono VARCHAR(255),
    mail VARCHAR(255),
    direccion VARCHAR(255)
);

-- =======================================
-- TABLA: CONTENEDOR
-- =======================================
CREATE TABLE IF NOT EXISTS contenedor (
    id_contenedor BIGSERIAL PRIMARY KEY,
    peso DOUBLE PRECISION NOT NULL,
    volumen DOUBLE PRECISION NOT NULL,
    estado VARCHAR(100)
);

-- =======================================
-- TABLA: CIUDAD
-- =======================================
CREATE TABLE IF NOT EXISTS ciudad (
    id_ciudad BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL
);

-- =======================================
-- TABLA: DEPOSITO
-- =======================================
CREATE TABLE IF NOT EXISTS deposito (
    id_deposito BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    direccion VARCHAR(200),
    latitud DOUBLE PRECISION,
    longitud DOUBLE PRECISION,
    costo_estadia_diario NUMERIC(10,2),
    id_ciudad BIGINT REFERENCES ciudad(id_ciudad) ON DELETE SET NULL
);

-- =======================================
-- TABLA: ESTADO (para solicitudes)
-- =======================================
CREATE TABLE IF NOT EXISTS estado (
    id_estado BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255)
);

-- =======================================
-- TABLA: TARIFAS
-- =======================================
CREATE TABLE IF NOT EXISTS tarifas (
    id BIGSERIAL PRIMARY KEY,
    activo BOOLEAN NOT NULL,
    descripcion VARCHAR(255) NOT NULL,
    fecha_actualizacion BIGINT NOT NULL,
    tipo VARCHAR(50) NOT NULL UNIQUE,
    unidad VARCHAR(20),
    valor NUMERIC(10,2) NOT NULL
);

-- =======================================
-- TABLA: SOLICITUD
-- =======================================
CREATE TABLE IF NOT EXISTS solicitud (
    numero_solicitud BIGSERIAL PRIMARY KEY,
    id_contenedor BIGINT REFERENCES contenedor(id_contenedor) ON DELETE SET NULL,
    id_cliente BIGINT NOT NULL REFERENCES cliente(id_cliente) ON DELETE CASCADE,
    costo_estimado NUMERIC(12,2),
    tiempo_estimado INTEGER,
    costo_final NUMERIC(12,2),
    tiempo_real INTEGER,
    id_tarifa INTEGER,
    id_estado BIGINT REFERENCES estado(id_estado) ON DELETE SET NULL,
    id_ruta BIGINT
);

-- =======================================
-- TABLA: RUTA
-- =======================================
CREATE TABLE IF NOT EXISTS ruta (
    id_ruta BIGSERIAL PRIMARY KEY,
    id_solicitud BIGINT,
    cantidad_tramos INTEGER,
    cantidad_depositos INTEGER,
    distancia_total_km DOUBLE PRECISION,
    tiempo_estimado_min DOUBLE PRECISION,
    costo_total DOUBLE PRECISION
);

-- Agregar FK circular después de crear ambas tablas
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_solicitud_ruta') THEN
        ALTER TABLE solicitud ADD CONSTRAINT fk_solicitud_ruta
        FOREIGN KEY (id_ruta) REFERENCES ruta(id_ruta) ON DELETE SET NULL;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_ruta_solicitud') THEN
        ALTER TABLE ruta ADD CONSTRAINT fk_ruta_solicitud
        FOREIGN KEY (id_solicitud) REFERENCES solicitud(numero_solicitud) ON DELETE CASCADE;
    END IF;
END $$;

-- =======================================
-- TABLA: ESTADO_TRAMO
-- =======================================
CREATE TABLE IF NOT EXISTS estado_tramo (
    id_estado BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL
);

-- =======================================
-- TABLA: TIPO_TRAMO
-- =======================================
CREATE TABLE IF NOT EXISTS tipo_tramo (
    id_tipo_tramo BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL
);

-- =======================================
-- TABLA: TRAMO
-- =======================================
CREATE TABLE IF NOT EXISTS tramo (
    id_tramo BIGSERIAL PRIMARY KEY,
    latitud_origen DOUBLE PRECISION,
    longitud_origen DOUBLE PRECISION,
    latitud_destino DOUBLE PRECISION,
    longitud_destino DOUBLE PRECISION,
    costo_aproximado NUMERIC(10,2),
    costo_real NUMERIC(10,2),
    distancia_km DOUBLE PRECISION,
    fecha_hora_inicio BIGINT,
    fecha_hora_fin BIGINT,
    id_camion BIGINT,
    id_tipo_tramo BIGINT REFERENCES tipo_tramo(id_tipo_tramo) ON DELETE SET NULL,
    id_estado BIGINT REFERENCES estado_tramo(id_estado) ON DELETE SET NULL,
    id_ruta BIGINT REFERENCES ruta(id_ruta) ON DELETE CASCADE,
    id_deposito_origen BIGINT REFERENCES deposito(id_deposito) ON DELETE SET NULL,
    id_deposito_destino BIGINT REFERENCES deposito(id_deposito) ON DELETE SET NULL
);

-- =======================================
-- INSERTAR DATOS
-- =======================================

-- TRANSPORTISTAS
INSERT INTO transportista (id_transportista, nombre, apellido, dni, telefono, mail, direccion) VALUES
(1, 'Braina', NULL, NULL, '351339', 'asdsa', 'manzanac'),
(2, 'Juan', 'Track', '42424242', '3513515151', 'JuanElCrack@gmail.com', 'No tiene casa vive en su camion'),
(3, 'Carlos', 'Gómez', '20123456', '3511234567', 'carlos.gomez@transporte.com', 'Av. Colón 123, Córdoba'),
(4, 'María', 'Fernández', '27234567', '3512345678', 'maria.fernandez@transporte.com', 'San Martín 456, Córdoba'),
(5, 'Juan', 'López', '30345678', '3513456789', 'juan.lopez@transporte.com', 'Rivadavia 789, Córdoba');

-- CAMIONES
INSERT INTO camion (id_camion, capacidad_peso, capacidad_volumen, consumo_combustible_km, costo_base_km, disponibilidad, patente, telefono, id_transportista) VALUES
(1, 12000.0, 30.0, 0.35, 150.0, true, 'ABC123', '3516789123', 1),
(2, 12000.0, 30.0, 0.35, 150.0, true, '12123', '12312', 1),
(3, 12000.0, 30.0, 0.35, 150.0, true, '112231123123', '12312', 1),
(4, 15000.0, 500.0, 0.35, 150.0, true, 'FGT123', '3512634582', NULL);

-- CIUDADES
INSERT INTO ciudad (id_ciudad, nombre) VALUES
(1, 'La Plata'),
(2, 'Mar del Plata'),
(3, 'Palermo'),
(4, 'Recoleta'),
(5, 'San Fernando del Valle'),
(6, 'Tinogasta'),
(7, 'Resistencia'),
(8, 'Saenz Peña'),
(9, 'Rawson'),
(10, 'Comodoro Rivadavia'),
(11, 'Córdoba Capital'),
(12, 'Villa Carlos Paz'),
(13, 'Corrientes'),
(14, 'Goya'),
(15, 'Paraná'),
(16, 'Concordia'),
(17, 'Formosa'),
(18, 'Clorinda'),
(19, 'San Salvador de Jujuy'),
(20, 'Palpalá'),
(21, 'Santa Rosa'),
(22, 'General Pico'),
(23, 'La Rioja'),
(24, 'Chilecito'),
(25, 'Mendoza'),
(26, 'San Rafael'),
(27, 'Posadas'),
(28, 'Oberá'),
(29, 'Neuquén'),
(30, 'San Martín de los Andes'),
(31, 'Viedma'),
(32, 'Bariloche'),
(33, 'Salta'),
(34, 'Cafayate'),
(35, 'San Juan'),
(36, 'Calingasta'),
(37, 'San Luis'),
(38, 'Villa Mercedes'),
(39, 'Río Gallegos'),
(40, 'Caleta Olivia'),
(41, 'Santa Fe'),
(42, 'Rosario'),
(43, 'Santiago del Estero'),
(44, 'Termas de Río Hondo'),
(45, 'Ushuaia'),
(46, 'Río Grande'),
(47, 'Buenos Aires');

-- CLIENTES (manteniendo todos los que tenías)
INSERT INTO cliente (id_cliente, nombre, apellido, dni, telefono, mail, direccion) VALUES
(201, 'Juan', 'Pérez', '12345678', '11-1234-5678', 'juan.perez@email.com', 'Av. Corrientes 1234, CABA'),
(202, 'María', 'González', '23456789', '11-2345-6789', 'maria.gonzalez@email.com', 'Av. Santa Fe 2345, CABA'),
(203, 'Carlos', 'Rodríguez', '34567890', '11-3456-7890', 'carlos.rodriguez@email.com', 'Av. Rivadavia 3456, CABA'),
(204, 'Ana', 'Martínez', '45678901', '11-4567-8901', 'ana.martinez@email.com', 'Av. Belgrano 4567, CABA'),
(205, 'Luis', 'Fernández', '56789012', '11-5678-9012', 'luis.fernandez@email.com', 'Av. Callao 5678, CABA'),
(206, 'Laura', 'López', '67890123', '11-6789-0123', 'laura.lopez@email.com', 'Av. 9 de Julio 6789, CABA'),
(207, 'Pedro', 'García', '78901234', '11-7890-1234', 'pedro.garcia@email.com', 'Av. Las Heras 7890, CABA'),
(208, 'Sofía', 'Sánchez', '89012345', '11-8901-2345', 'sofia.sanchez@email.com', 'Av. Cabildo 8901, CABA'),
(209, 'Diego', 'Romero', '90123456', '11-9012-3456', 'diego.romero@email.com', 'Av. Pueyrredón 9012, CABA'),
(210, 'string', 'string', 'string', 'string', 'string', 'string'),
(211, 'Braian', 'Oliva', '1111123123', '+54 9 3512510762', 'juan.perez@gmail.com', 'Av. Corrientes 1234, CBA'),
(212, 'Maria', 'Gonzalez', '456', '5491145678901', 'mariaa.gonzalez@example.com', 'Av. Corrientes 3456, CABA'),
(213, 'Braian', 'Oliva', '1122333', '351111', 'bra.gonzalez@example.com', 'Av. Corrientes 3456, CABA'),
(214, 'NAza', 'Derico', '1221321', '123123', 'stringasd', 'string'),
(215, 'Josue', 'Péraaez', '9999999', '+54 9 11 1234-5678', 'josue.perez@email.com', 'Av. Corrientes 1234, CABA'),
(216, 'María', 'Fernández', '38999888', '+54 9 3516009988', 'mariafdez@hotmail.com', 'Bv. Illia 500, Nueva Córdoba'),
(217, 'Juan', 'Pérez', '40555999', '3515559999', 'juan.perez@example.com', 'Av. Colón 1234'),
(218, 'Briana', 'Flores', '43424343', '3523513511', 'labriana@gmail.com', 'Siempre viva 152');

-- CONTENEDORES (todos los que tenías)
INSERT INTO contenedor (id_contenedor, peso, volumen, estado) VALUES
(101, 1212.0, 11.0, 'Pendiente de entrega'),
(102, 950.75, 6.8, NULL),
(103, 1500.0, 9.2, NULL),
(104, 800.2, 5.5, NULL),
(105, 1750.8, 10.1, NULL),
(106, 900.0, 6.2, NULL),
(107, 1100.3, 7.5, NULL),
(108, 1600.4, 9.9, NULL),
(109, 0.0, 0.0, NULL),
(110, 0.0, 0.0, NULL),
(111, 0.0, 0.0, NULL),
(112, 1500.5, 50.75, NULL),
(113, 1500.5, 50.75, NULL),
(114, 2500.0, 85.5, NULL),
(115, 2500.0, 85.5, NULL),
(116, 2500.0, 85.5, NULL),
(117, 2500.0, 85.5, NULL),
(118, 2500.0, 85.5, NULL),
(119, 12122.0, 120.0, NULL),
(120, 1500.5, 25.0, NULL),
(121, 15000.0, 35.0, NULL),
(122, 15000.0, 35.0, NULL),
(123, 4200.0, 18.0, NULL),
(124, 1200.5, 15.3, NULL),
(125, 1200.5, 15.3, NULL),
(126, 1200.5, 15.3, NULL),
(127, 1200.5, 15.3, NULL),
(128, 100.0, 100.0, 'entregado'),
(129, 15000.0, 30.0, 'disponible'),
(130, 15000.0, 30.0, 'disponible'),
(131, 15000.0, 30.0, 'disponible'),
(132, 1200.5, 15.3, 'disponible');

-- DEPOSITOS (todos los que tenías - son muchos, los mantengo completos)
INSERT INTO deposito (id_deposito, nombre, direccion, latitud, longitud, costo_estadia_diario, id_ciudad) VALUES
(1, 'Depósito 1 de La Plata', 'Calle 1 100', -34.9214, -57.9544, 15000, 1),
(2, 'Depósito 2 de La Plata', 'Calle 2 200', -34.9214, -57.9544, 15000, 1),
(3, 'Depósito 1 de Mar del Plata', 'Calle 1 100', -38.0055, -57.5426, 16000, 2),
(4, 'Depósito 2 de Mar del Plata', 'Calle 2 200', -38.0055, -57.5426, 16000, 2),
(5, 'Depósito 1 de Palermo', 'Av. Santa Fe 3000', -34.5889, -58.4305, 20000, 3),
(6, 'Depósito 2 de Palermo', 'Av. Borges 2500', -34.5889, -58.4305, 20000, 3),
(7, 'Depósito 1 de Recoleta', 'Av. Alvear 1800', -34.5882, -58.3966, 21000, 4),
(8, 'Depósito 2 de Recoleta', 'Calle Quintana 900', -34.5882, -58.3966, 21000, 4),
(9, 'Depósito 1 de San Fernando del Valle', 'Calle Sarmiento 100', -28.4696, -65.7852, 11000, 5),
(10, 'Depósito 2 de San Fernando del Valle', 'Calle Rivadavia 200', -28.4696, -65.7852, 11000, 5),
(11, 'Depósito 1 de Tinogasta', 'Calle Belgrano 150', -28.0651, -67.5573, 10000, 6),
(12, 'Depósito 2 de Tinogasta', 'Calle Copiapó 300', -28.0651, -67.5573, 10000, 6),
(13, 'Depósito 1 de Resistencia', 'Av. 25 de Mayo 100', -27.4512, -58.986, 12000, 7),
(14, 'Depósito 2 de Resistencia', 'Av. Alberdi 200', -27.4512, -58.986, 12000, 7),
(15, 'Depósito 1 de Saenz Peña', 'Av. 2 150', -26.785, -60.438, 11500, 8),
(16, 'Depósito 2 de Saenz Peña', 'Av. 33 300', -26.785, -60.438, 11500, 8),
(17, 'Depósito 1 de Rawson', 'Calle Moreno 100', -43.3, -65.1, 12500, 9),
(18, 'Depósito 2 de Rawson', 'Calle España 200', -43.3, -65.1, 12500, 9),
(19, 'Depósito 1 de Comodoro Rivadavia', 'Av. Roca 100', -45.865, -67.48, 13500, 10),
(20, 'Depósito 2 de Comodoro Rivadavia', 'Av. Kennedy 200', -45.865, -67.48, 13500, 10),
(21, 'Depósito 1 de Córdoba Capital', 'Av. Colón 1000', -31.4201, -64.1888, 18000, 11),
(22, 'Depósito 2 de Córdoba Capital', 'Av. Olmos 300', -31.4201, -64.1888, 18000, 11),
(23, 'Depósito 1 de Villa Carlos Paz', 'Av. Uruguay 500', -31.42, -64.5, 17000, 12),
(24, 'Depósito 2 de Villa Carlos Paz', 'Av. San Martín 600', -31.42, -64.5, 17000, 12),
(25, 'Depósito 1 de Corrientes', 'Costanera 200', -27.48, -58.83, 14000, 13),
(26, 'Depósito 2 de Corrientes', 'Calle Junín 300', -27.48, -58.83, 14000, 13),
(27, 'Depósito 1 de Goya', 'Calle España 100', -29.14, -59.26, 13000, 14),
(28, 'Depósito 2 de Goya', 'Calle Colón 200', -29.14, -59.26, 13000, 14),
(29, 'Depósito 1 de Paraná', 'Av. Ramírez 300', -31.733, -60.533, 16000, 15),
(30, 'Depósito 2 de Paraná', 'Av. Almafuerte 400', -31.733, -60.533, 16000, 15),
(31, 'Depósito 1 de Concordia', 'Av. San Lorenzo 500', -31.39, -58.02, 14500, 16),
(32, 'Depósito 2 de Concordia', 'Av. Eva Perón 600', -31.39, -58.02, 14500, 16),
(33, 'Depósito 1 de Formosa', 'Av. 25 de Mayo 100', -26.18, -58.17, 12500, 17),
(34, 'Depósito 2 de Formosa', 'Calle San Martín 200', -26.18, -58.17, 12500, 17),
(35, 'Depósito 1 de Clorinda', 'Av. San Martín 100', -25.283, -57.72, 12000, 18),
(36, 'Depósito 2 de Clorinda', 'Calle Italia 200', -25.283, -57.72, 12000, 18),
(37, 'Depósito 1 de San Salvador de Jujuy', 'Av. Lavalle 100', -24.183, -65.3, 13000, 19),
(38, 'Depósito 2 de San Salvador de Jujuy', 'Av. Bolivia 200', -24.183, -65.3, 13000, 19),
(39, 'Depósito 1 de Palpalá', 'Av. Libertad 300', -24.25, -65.1, 12500, 20),
(40, 'Depósito 2 de Palpalá', 'Calle Rio Negro 400', -24.25, -65.1, 12500, 20),
(41, 'Depósito 1 de Santa Rosa', 'Av. Luro 500', -36.62, -64.29, 14000, 21),
(42, 'Depósito 2 de Santa Rosa', 'Av. España 600', -36.62, -64.29, 14000, 21),
(43, 'Depósito 1 de General Pico', 'Av. San Martín 700', -35.67, -63.75, 13000, 22),
(44, 'Depósito 2 de General Pico', 'Av. Circunvalación 800', -35.67, -63.75, 13000, 22),
(45, 'Depósito 1 de La Rioja', 'Av. Rivadavia 100', -29.41, -66.85, 13000, 23),
(46, 'Depósito 2 de La Rioja', 'Calle San Nicolás 200', -29.41, -66.85, 13000, 23),
(47, 'Depósito 1 de Chilecito', 'Calle Castro Barros 100', -29.17, -67.5, 12500, 24),
(48, 'Depósito 2 de Chilecito', 'Calle Malvinas 200', -29.17, -67.5, 12500, 24),
(49, 'Depósito 1 de Mendoza', 'Av. San Martín 100', -32.89, -68.83, 16000, 25),
(50, 'Depósito 2 de Mendoza', 'Av. Colón 200', -32.89, -68.83, 16000, 25),
(51, 'Depósito 1 de San Rafael', 'Av. Hipólito Yrigoyen 300', -34.61, -68.33, 15000, 26),
(52, 'Depósito 2 de San Rafael', 'Calle Balloffet 400', -34.61, -68.33, 15000, 26),
(53, 'Depósito 1 de Posadas', 'Av. Uruguay 100', -27.37, -55.89, 14000, 27),
(54, 'Depósito 2 de Posadas', 'Av. Mitre 200', -27.37, -55.89, 14000, 27),
(55, 'Depósito 1 de Oberá', 'Calle Lavalle 300', -27.48, -55.12, 13000, 28),
(56, 'Depósito 2 de Oberá', 'Av. Ucrania 400', -27.48, -55.12, 13000, 28),
(57, 'Depósito 1 de Neuquén', 'Av. Argentina 100', -38.95, -68.06, 17000, 29),
(58, 'Depósito 2 de Neuquén', 'Calle Rivadavia 200', -38.95, -68.06, 17000, 29),
(59, 'Depósito 1 de San Martín de los Andes', 'Av. Koessler 300', -40.16, -71.35, 15000, 30),
(60, 'Depósito 2 de San Martín de los Andes', 'Calle Curruhuinca 400', -40.16, -71.35, 15000, 30),
(61, 'Depósito 1 de Viedma', 'Av. Costanera 100', -40.81, -62.99, 14000, 31),
(62, 'Depósito 2 de Viedma', 'Av. Villarino 200', -40.81, -62.99, 14000, 31),
(63, 'Depósito 1 de Bariloche', 'Av. Bustillo 12000', -41.13, -71.31, 18000, 32),
(64, 'Depósito 2 de Bariloche', 'Av. Moreno 900', -41.13, -71.31, 18000, 32),
(65, 'Depósito 1 de Salta', 'Calle España 100', -24.78, -65.41, 15000, 33),
(66, 'Depósito 2 de Salta', 'Av. Belgrano 300', -24.78, -65.41, 15000, 33),
(67, 'Depósito 1 de Cafayate', 'Calle Rivadavia 300', -26.072, -65.977, 13000, 34),
(68, 'Depósito 2 de Cafayate', 'Av. Güemes 400', -26.072, -65.977, 13000, 34),
(69, 'Depósito 1 de San Juan', 'Av. Libertador 1000', -31.53, -68.52, 15000, 35),
(70, 'Depósito 2 de San Juan', 'Av. Rioja 200', -31.53, -68.52, 15000, 35),
(71, 'Depósito 1 de Calingasta', 'Calle Sarmiento 100', -31.34, -69.43, 12000, 36),
(72, 'Depósito 2 de Calingasta', 'Av. Mitre 200', -31.34, -69.43, 12000, 36),
(73, 'Depósito 1 de San Luis', 'Av. Illia 100', -33.3, -66.33, 14500, 37),
(74, 'Depósito 2 de San Luis', 'Av. España 200', -33.3, -66.33, 14500, 37),
(75, 'Depósito 1 de Villa Mercedes', 'Av. Mitre 300', -33.67, -65.47, 13000, 38),
(76, 'Depósito 2 de Villa Mercedes', 'Av. 25 de Mayo 400', -33.67, -65.47, 13000, 38),
(77, 'Depósito 1 de Río Gallegos', 'Av. Kirchner 100', -51.62, -69.22, 20000, 39),
(78, 'Depósito 2 de Río Gallegos', 'Av. San Martín 200', -51.62, -69.22, 20000, 39),
(79, 'Depósito 1 de Caleta Olivia', 'Av. Independencia 300', -46.43, -67.52, 18000, 40),
(80, 'Depósito 2 de Caleta Olivia', 'Calle Alem 400', -46.43, -67.52, 18000, 40),
(81, 'Depósito 1 de Santa Fe', 'Bv. Gálvez 1000', -31.63, -60.7, 15500, 41),
(82, 'Depósito 2 de Santa Fe', 'Av. Aristóbulo 2000', -31.63, -60.7, 15500, 41),
(83, 'Depósito 1 de Rosario', 'Bv. Oroño 300', -32.95, -60.65, 16500, 42),
(84, 'Depósito 2 de Rosario', 'Av. Pellegrini 400', -32.95, -60.65, 16500, 42),
(85, 'Depósito 1 de Santiago del Estero', 'Av. Belgrano 500', -27.78, -64.26, 14000, 43),
(86, 'Depósito 2 de Santiago del Estero', 'Calle Roca 600', -27.78, -64.26, 14000, 43),
(87, 'Depósito 1 de Termas de Río Hondo', 'Av. Perón 700', -27.49, -64.85, 13000, 44),
(88, 'Depósito 2 de Termas de Río Hondo', 'Calle Salta 800', -27.49, -64.85, 13000, 44),
(89, 'Depósito 1 de Ushuaia', 'Av. San Martín 100', -54.81, -68.32, 25000, 45),
(90, 'Depósito 2 de Ushuaia', 'Calle Maipú 200', -54.81, -68.32, 25000, 45),
(91, 'Depósito 1 de Río Grande', 'Av. Belgrano 300', -53.78, -67.72, 23000, 46),
(92, 'Depósito 2 de Río Grande', 'Calle Islas Malvinas 400', -53.78, -67.72, 23000, 46);

-- ESTADOS (para solicitudes)
INSERT INTO estado (id_estado, nombre) VALUES
(1, 'disponible'),
(2, 'pendiente_entrega'),
(3, 'en_transito'),
(4, 'entregado');

-- ESTADOS TRAMO
INSERT INTO estado_tramo (id_estado, nombre) VALUES
(1, 'pendiente'),
(2, 'asignado'),
(3, 'iniciado'),
(4, 'finalizado');

-- TIPOS TRAMO
INSERT INTO tipo_tramo (id_tipo_tramo, nombre) VALUES
(1, 'a_deposito'),
(2, 'entre_depositos'),
(3, 'desde_deposito');

-- TARIFAS
INSERT INTO tarifas (id, activo, descripcion, fecha_actualizacion, tipo, unidad, valor) VALUES
(1, true, 'Costo base por kilómetro', 1762810049948, 'COSTO_KM_BASE', 'km', 5),
(2, false, 'Precio del combustible por litro', 1762991529889, 'COMBUSTIBLE', 'litro', 1.5),
(3, true, 'Costo de estadía en depósito por día', 1762810050011, 'ESTADIA_DEPOSITO', 'dia', 50),
(4, true, 'Costo base por kilómetro', 1762990443060, 'COSTOaaas', 'km', 5);

-- RUTAS
INSERT INTO ruta (id_ruta, cantidad_depositos, cantidad_tramos, costo_total, distancia_total_km, tiempo_estimado_min, id_solicitud) VALUES
(1, 2, 3, 450539.17, 1893.6486, 1325.82333333333, NULL),
(2, 1, 2, 93570.69, 363.3508, 256.156666666667, NULL);

-- SOLICITUDES (eliminé solo las que referencian id_estado = 131 que no existe)
INSERT INTO solicitud (numero_solicitud, id_contenedor, id_cliente, costo_estimado, tiempo_estimado, costo_final, tiempo_real, id_tarifa, id_estado, id_ruta) VALUES
(1, 101, 201, 5000, 5, 5100, 6, 1, 3, NULL),
(2, 102, 202, 4500, 4, 4700, 5, 2, 3, NULL),
(3, 103, 203, 6000, 6, NULL, NULL, 3, 1, 1),
(4, 104, 201, 7000, 7, 6900, 7, 1, 3, 2),
(5, 105, 204, 4000, 4, 4100, 3, 2, 4, NULL),
(6, NULL, 205, 5200, 5, NULL, NULL, 3, 1, NULL),
(7, NULL, 206, 4800, 4, NULL, NULL, 2, 1, NULL),
(8, 106, 207, 6500, 6, 6600, 6, 1, 3, NULL),
(9, 107, 208, 7200, 7, NULL, NULL, 1, 1, NULL),
(10, NULL, 209, 5300, 5, NULL, NULL, 3, 1, NULL),
(15, 101, 201, 45000, 8, 47000, 9, 3, 1, NULL),
(16, 128, 218, NULL, NULL, NULL, NULL, NULL, 1, NULL),
(17, 128, 218, 93570.69, 256, 72693.38, 0, NULL, 3, 2),
(18, 129, 201, NULL, NULL, NULL, NULL, NULL, 1, NULL),
(19, 130, 201, NULL, NULL, NULL, NULL, NULL, 1, NULL),
(20, 131, 201, NULL, NULL, NULL, NULL, NULL, 1, NULL),
(21, 132, 217, NULL, NULL, NULL, NULL, NULL, 1, NULL);

-- TRAMOS
INSERT INTO tramo (id_tramo, costo_aproximado, costo_real, distancia_km, latitud_destino, latitud_origen, longitud_destino, longitud_origen, fecha_hora_fin, fecha_hora_inicio, id_camion, id_deposito_destino, id_deposito_origen, id_estado, id_ruta, id_tipo_tramo) VALUES
(1, 103715.4, NULL, 429.1974, -27.7834, -24.7821, -64.2642, -65.4232, NULL, NULL, 1, 43, NULL, 2, 1, 1),
(2, 178146.16, NULL, 752.8094, -32.9442, -27.7834, -60.6505, -64.2642, NULL, NULL, NULL, 42, 43, 1, 1, 2),
(3, 168677.61, NULL, 711.6418, -38.0055, -32.9442, -57.5426, -60.6505, NULL, NULL, NULL, NULL, 42, 1, 1, 3),
(4, 39575.28, 40628.01, 150.3273, -31.4201, -32.4075, -64.1888, -63.2404, 1763214958832, 1763214949120, 1, 21, NULL, 4, 2, 1),
(5, 53995.41, 32065.37, 213.0235, -33.1232, -31.4201, -64.3492, -64.1888, 1763214986387, 1763214969494, 1, NULL, 21, 4, 2, 3);

-- Actualizar secuencias para evitar conflictos de IDs
SELECT setval('transportista_id_transportista_seq', (SELECT MAX(id_transportista) FROM transportista));
SELECT setval('camion_id_camion_seq', (SELECT MAX(id_camion) FROM camion));
SELECT setval('ciudad_id_ciudad_seq', (SELECT MAX(id_ciudad) FROM ciudad));
SELECT setval('cliente_id_cliente_seq', (SELECT MAX(id_cliente) FROM cliente));
SELECT setval('contenedor_id_contenedor_seq', (SELECT MAX(id_contenedor) FROM contenedor));
SELECT setval('deposito_id_deposito_seq', (SELECT MAX(id_deposito) FROM deposito));
SELECT setval('estado_id_estado_seq', (SELECT MAX(id_estado) FROM estado));
SELECT setval('estado_tramo_id_estado_seq', (SELECT MAX(id_estado) FROM estado_tramo));
SELECT setval('tipo_tramo_id_tipo_tramo_seq', (SELECT MAX(id_tipo_tramo) FROM tipo_tramo));
SELECT setval('tarifas_id_seq', (SELECT MAX(id) FROM tarifas));
SELECT setval('ruta_id_ruta_seq', (SELECT MAX(id_ruta) FROM ruta));
SELECT setval('solicitud_numero_solicitud_seq', (SELECT MAX(numero_solicitud) FROM solicitud));
SELECT setval('tramo_id_tramo_seq', (SELECT MAX(id_tramo) FROM tramo));

-- =======================================
-- MENSAJE DE CONFIRMACIÓN
-- =======================================
DO $$
BEGIN
    RAISE NOTICE '===========================================';
    RAISE NOTICE 'Base de datos inicializada correctamente';
    RAISE NOTICE '===========================================';
END $$;

-- Resumen de datos cargados
SELECT
    (SELECT COUNT(*) FROM transportista) AS transportistas,
    (SELECT COUNT(*) FROM camion) AS camiones,
    (SELECT COUNT(*) FROM ciudad) AS ciudades,
    (SELECT COUNT(*) FROM cliente) AS clientes,
    (SELECT COUNT(*) FROM contenedor) AS contenedores,
    (SELECT COUNT(*) FROM deposito) AS depositos,
    (SELECT COUNT(*) FROM estado) AS estados_solicitud,
    (SELECT COUNT(*) FROM estado_tramo) AS estados_tramo,
    (SELECT COUNT(*) FROM tipo_tramo) AS tipos_tramo,
    (SELECT COUNT(*) FROM tarifas) AS tarifas,
    (SELECT COUNT(*) FROM ruta) AS rutas,
    (SELECT COUNT(*) FROM solicitud) AS solicitudes,
    (SELECT COUNT(*) FROM tramo) AS tramos;
