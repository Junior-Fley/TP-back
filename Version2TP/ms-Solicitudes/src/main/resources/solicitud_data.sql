-- Datos de prueba para clientes
INSERT INTO cliente (id_cliente, nombre, apellido, dni, telefono, mail, direccion) VALUES
(201, 'Juan', 'Pérez', '12345678', '11-1234-5678', 'juan.perez@email.com', 'Av. Corrientes 1234, CABA'),
(202, 'María', 'González', '23456789', '11-2345-6789', 'maria.gonzalez@email.com', 'Av. Santa Fe 2345, CABA'),
(203, 'Carlos', 'Rodríguez', '34567890', '11-3456-7890', 'carlos.rodriguez@email.com', 'Av. Rivadavia 3456, CABA'),
(204, 'Ana', 'Martínez', '45678901', '11-4567-8901', 'ana.martinez@email.com', 'Av. Belgrano 4567, CABA'),
(205, 'Luis', 'Fernández', '56789012', '11-5678-9012', 'luis.fernandez@email.com', 'Av. Callao 5678, CABA'),
(206, 'Laura', 'López', '67890123', '11-6789-0123', 'laura.lopez@email.com', 'Av. 9 de Julio 6789, CABA'),
(207, 'Pedro', 'García', '78901234', '11-7890-1234', 'pedro.garcia@email.com', 'Av. Las Heras 7890, CABA'),
(208, 'Sofía', 'Sánchez', '89012345', '11-8901-2345', 'sofia.sanchez@email.com', 'Av. Cabildo 8901, CABA'),
(209, 'Diego', 'Romero', '90123456', '11-9012-3456', 'diego.romero@email.com', 'Av. Pueyrredón 9012, CABA');

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
