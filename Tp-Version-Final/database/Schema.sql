CREATE TABLE sqlite_sequence(name,seq);

CREATE TABLE camion (
                        id_camion integer,
                        capacidad_peso float,
                        capacidad_volumen float,
                        marca varchar(255),
                        modelo varchar(255),
                        patente varchar(255),
                        id_transportista bigint,
                        primary key (id_camion)
);

CREATE TABLE cliente (
                         id_cliente integer,
                         apellido varchar(255),
                         nombre varchar(255),
                         dni varchar(255),
                         mail varchar(255),
                         direccion varchar(255),
                         telefono varchar(255),
                         primary key (id_cliente)
);

CREATE TABLE tarifas (
                         id integer,
                         activo boolean not null,
                         descripcion varchar(255),
                         tipo varchar(255),
                         unidad varchar(20),
                         valor numeric(10,2) not null,
                         primary key (id)
);

CREATE TABLE "CONTENEDOR" (
                              "id_contenedor" INTEGER,
                              "peso" REAL,
                              "volumen" REAL,
                              "estado" TEXT,
                              PRIMARY KEY("id_contenedor" AUTOINCREMENT)
);

CREATE TABLE solicitud (
                           numero_solicitud INTEGER PRIMARY KEY AUTOINCREMENT,
                           fecha DATETIME NOT NULL,
                           id_cliente bigint NOT NULL,
                           id_contenedor bigint NOT NULL,
                           id_estado bigint NOT NULL,
                           FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente),
                           FOREIGN KEY (id_contenedor) REFERENCES "CONTENEDOR"(id_contenedor),
                           FOREIGN KEY (id_estado) REFERENCES "estado_solicitud"(idEstado)
);

CREATE TABLE ciudad (
                        id_ciudad integer,
                        nombre varchar(100) NOT NULL,
                        primary key (id_ciudad)
);

CREATE TABLE deposito (
                          costo_estadia_diario numeric(10,2),
                          latitud float,
                          longitud float,
                          id_ciudad integer NOT NULL,
                          id_deposito integer,
                          nombre varchar(200),
                          direccion varchar(200),
                          primary key (id_deposito)
);

CREATE TABLE estado_tramo (
                              id_estado integer,
                              nombre varchar(50) NOT NULL,
                              primary key (id_estado)
);

CREATE TABLE ruta (
                      cantidad_depositos integer,
                      cantidad_tramos integer,
                      costo_total numeric(10,2),
                      distancia_total_km float,
                      tiempo_estimado_min float,
                      id_ruta integer,
                      id_solicitud bigint,
                      primary key (id_ruta)
);

CREATE TABLE tipo_tramo (
                            id_tipo_tramo integer,
                            nombre varchar(50) NOT NULL,
                            primary key (id_tipo_tramo)
);

CREATE TABLE tramo (
                       costo_aproximado numeric(10,2),
                       costo_real numeric(10,2),
                       distancia_km float,
                       duracion_min float,
                       id_ciudad_origen bigint,
                       id_ciudad_destino bigint,
                       id_estado bigint,
                       id_ruta bigint,
                       id_tipo_tramo bigint,
                       id_tramo integer,
                       primary key (id_tramo)
);

CREATE TABLE "TRANSPORTISTA" (
                                 "id_transportista" INTEGER,
                                 "nombre" TEXT,
                                 "apellido" TEXT,
                                 "dni" TEXT,
                                 "telefono" TEXT,
                                 PRIMARY KEY("id_transportista" AUTOINCREMENT)
);

CREATE TABLE estado (
                        id_estado integer,
                        nombre varchar(255),
                        primary key (id_estado)
);
