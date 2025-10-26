-- Creación de la tabla
CREATE TABLE solicitud (
                           numero_solicitud SERIAL PRIMARY KEY,
                           id_contenedor INT NOT NULL,
                           id_cliente INT NOT NULL,
                           costo_estimado DECIMAL(10,2),
                           tiempo_estimado INT,
                           costo_final DECIMAL(10,2),
                           tiempo_real INT,
                           id_tarifa INT,
                           estado_solicitud VARCHAR(30)

    -- FK simuladas (solo si ya existen las tablas)
    -- FOREIGN KEY (id_contenedor) REFERENCES contenedor(id_contenedor),
    -- FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente),
    -- FOREIGN KEY (id_tarifa) REFERENCES tarifa(id_tarifa)
);

-- Datos de prueba
INSERT INTO solicitud (id_contenedor, id_cliente, costo_estimado, tiempo_estimado, costo_final, tiempo_real, id_tarifa, estado_solicitud) VALUES
                                                                                                                                              (101, 201, 5000.00, 5, 5100.00, 6, 1, 'Completada'),
                                                                                                                                              (102, 202, 4500.00, 4, 4700.00, 5, 2, 'Completada'),
                                                                                                                                              (103, 203, 6000.00, 6, NULL, NULL, 3, 'Pendiente'),
                                                                                                                                              (104, 201, 7000.00, 7, 6900.00, 7, 1, 'Completada'),
                                                                                                                                              (105, 204, 4000.00, 4, 4100.00, 3, 2, 'Cancelada');
