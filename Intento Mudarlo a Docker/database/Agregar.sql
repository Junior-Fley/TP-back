-- ================================================
-- ESTADOS DE SOLICITUD
-- ================================================
INSERT INTO estado_solicitud (nombre) VALUES
                                          ('Pendiente'),
                                          ('En proceso'),
                                          ('Finalizada');

-- ================================================
-- ESTADOS DE TRAMO
-- ================================================
INSERT INTO estado_tramo (nombre) VALUES
                                      ('Pendiente'),
                                      ('En curso'),
                                      ('Finalizado');

-- ================================================
-- TIPOS DE TRAMO
-- ================================================
INSERT INTO tipo_tramo (nombre) VALUES
                                    ('Carga'),
                                    ('Descarga'),
                                    ('Traslado');

-- ================================================
-- CIUDADES
-- ================================================
INSERT INTO ciudad (nombre) VALUES
                                ('Córdoba'),
                                ('Villa María'),
                                ('Buenos Aires'),
                                ('La Plata');

-- ================================================
-- DEPÓSITOS (2 por ciudad)
-- ================================================
INSERT INTO deposito (nombre, direccion, costo_estadia_diario, latitud, longitud, id_ciudad) VALUES
                                                                                                 ('Depósito Córdoba Centro', 'Av. Colón 1000', 1500, -31.4167, -64.1833, 1),
                                                                                                 ('Depósito Córdoba Sur', 'Av. Vélez Sarsfield 500', 1300, -31.4522, -64.1888, 1),

                                                                                                 ('Depósito Villa María Norte', 'Bv. España 200', 1200, -32.4075, -63.2406, 2),
                                                                                                 ('Depósito Villa María Sur', 'Ruta 9 km 555', 1100, -32.4352, -63.2557, 2),

                                                                                                 ('Depósito Buenos Aires Centro', 'Av. Rivadavia 1500', 2000, -34.6037, -58.3816, 3),
                                                                                                 ('Depósito Buenos Aires Puerto', 'Puerto Madero Dique 1', 2200, -34.6092, -58.3642, 3),

                                                                                                 ('Depósito La Plata Centro', 'Calle 7 nº 550', 1400, -34.9214, -57.9544, 4),
                                                                                                 ('Depósito La Plata Oeste', 'Av. 44 y 155', 1350, -34.9317, -57.9901, 4);

-- ================================================
-- TARIFAS BASE
-- ================================================
INSERT INTO tarifas (activo, descripcion, tipo, unidad, valor) VALUES
                                                                   (true, 'Costo por km ruta terrestre', 'DISTANCIA', 'KM', 120.50),
                                                                   (true, 'Costo por min tiempo estimado', 'TIEMPO', 'MIN', 15.00),
                                                                   (true, 'Costo por estadía en depósito', 'ESTADIA', 'DIA', 800.00);

-- ================================================
-- TRANSPORTISTAS
-- ================================================
INSERT INTO transportista (nombre, apellido, dni, telefono) VALUES
                                                                ('Carlos', 'Pérez', '30111222', '351-5550001'),
                                                                ('Lucía', 'Gómez', '32233444', '351-5550002');

-- ================================================
-- CAMIONES (1 por transportista)
-- ================================================
INSERT INTO camion (capacidad_peso, capacidad_volumen, marca, modelo, patente, id_transportista) VALUES
                                                                                                     (30000, 60, 'Scania', 'R500', 'ABC123', 1),
                                                                                                     (20000, 40, 'Volvo', 'FH460', 'XYZ987', 2);

-- ================================================
-- CLIENTE
-- ================================================
INSERT INTO cliente (apellido, nombre, dni, mail, direccion, telefono) VALUES
    ('Oliva', 'Braian', '12345678', 'braian@example.com', 'Córdoba Capital', '351-5558888');

-- ================================================
-- CONTENEDOR
-- ================================================
INSERT INTO contenedor (peso, volumen, estado) VALUES
    (15000, 30, 'Pendiente');

-- ================================================
-- SOLICITUD DE TRASLADO EJEMPLO
-- id_cliente = 1
-- id_contenedor = 1
-- id_estado = 1 (Pendiente)
-- ================================================
INSERT INTO solicitud (id_cliente, id_contenedor, id_estado, fecha)
VALUES (1, 1, 1, NOW());
