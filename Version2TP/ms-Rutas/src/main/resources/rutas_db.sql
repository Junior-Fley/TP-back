-- 🌆 CIUDADES
INSERT INTO ciudad (nombre) VALUES
                                ('Córdoba'),
                                ('Rosario'),
                                ('Buenos Aires'),
                                ('Mendoza');

-- 🏭 DEPÓSITOS
INSERT INTO deposito (nombre, direccion, latitud, longitud, costo_estadia_diario, id_ciudad) VALUES
                                                                                                 ('Depósito Central', 'Av. Colón 1234', -31.4167, -64.1833, 1200.50, 1),
                                                                                                 ('Depósito Norte', 'Ruta 9 KM 15', -32.9442, -60.6505, 950.00, 2),
                                                                                                 ('Depósito Sur', 'Av. Calchaquí 2500', -34.7205, -58.2617, 1100.00, 3);

-- ⚙️ ESTADOS DE TRAMO
INSERT INTO estado_tramo (name) VALUES
                                    ('Pendiente'),
                                    ('En Curso'),
                                    ('Finalizado');

-- 🔧 TIPOS DE TRAMO
INSERT INTO tipo_tramo (nombre) VALUES
                                    ('Carga'),
                                    ('Traslado'),
                                    ('Descarga');

-- 🛣️ RUTAS
INSERT INTO ruta (solicitud, cantidad_tramos, cantidad_depositos) VALUES
                                                                      (1001, 2, 3),
                                                                      (1002, 3, 2);

-- 🧩 TRAMOS
INSERT INTO tramo (latitud_origen, longitud_origen, latitud_destino, longitud_destino,
                   costo_aproximado, costo_real, fecha_hora_inicio, fecha_hora_fin,
                   id_camion, id_tipo_tramo, id_estado, id_ruta, id_deposito_origen, id_deposito_destino)
VALUES
    (-31.4167, -64.1833, -32.9442, -60.6505, 5500.00, 5700.00, '2025-10-25 08:00:00', '2025-10-25 13:30:00',
     1, 1, 3, 1, 1, 2),

    (-32.9442, -60.6505, -34.7205, -58.2617, 6200.00, 6400.00, '2025-10-26 09:00:00', '2025-10-26 15:00:00',
     2, 2, 2, 1, 2, 3),

    (-34.7205, -58.2617, -31.4167, -64.1833, 7300.00, 0.00, '2025-10-27 07:30:00', NULL,
     3, 3, 1, 2, 3, 1);
