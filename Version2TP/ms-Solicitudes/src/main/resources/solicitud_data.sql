-- Datos de prueba para contenedores
INSERT INTO contenedor (id_contenedor, peso, volumen) VALUES
(101, 1200.50, 8.5),
(102, 950.75, 6.8),
(103, 1500.00, 9.2),
(104, 800.20, 5.5),
(105, 1750.80, 10.1),
(106, 900.00, 6.2),
(107, 1100.30, 7.5),
(108, 1600.40, 9.9);

-- Datos de prueba para solicitudes
INSERT INTO solicitud (id_contenedor, id_cliente, costo_estimado, tiempo_estimado, costo_final, tiempo_real, id_tarifa, estado_solicitud) VALUES
(101, 201, 5000.00, 5, 5100.00, 6, 1, 'completada'),
(102, 202, 4500.00, 4, 4700.00, 5, 2, 'completada'),
(103, 203, 6000.00, 6, NULL, NULL, 3, 'pendiente'),
(104, 201, 7000.00, 7, 6900.00, 7, 1, 'completada'),
(105, 204, 4000.00, 4, 4100.00, 3, 2, 'cancelada'),
(NULL, 205, 5200.00, 5, NULL, NULL, 3, 'pendiente'),
(NULL, 206, 4800.00, 4, NULL, NULL, 2, 'pendiente'),
(106, 207, 6500.00, 6, 6600.00, 6, 1, 'completada'),
(107, 208, 7200.00, 7, NULL, NULL, 1, 'pendiente'),
(NULL, 209, 5300.00, 5, NULL, NULL, 3, 'pendiente');
