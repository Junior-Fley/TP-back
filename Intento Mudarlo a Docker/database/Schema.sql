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

CREATE TABLE estado_solicitud (
                                  id_estado integer,
                                  nombre varchar(255),
                                  primary key (id_estado)
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
                           id_cliente bigint,
                           id_contenedor bigint,
                           id_estado bigint,
                           fecha varchar(255),
                           FOREIGN KEY (id_estado) REFERENCES "estado_solicitud"(idEstado)
);

CREATE TABLE ciudad (
                        id_ciudad integer,
                        nombre varchar(100) not null,
                        primary key (id_ciudad)
);

CREATE TABLE deposito (
                          costo_estadia_diario numeric(10,2),
                          latitud float,
                          longitud float,
                          id_ciudad bigint,
                          nombre varchar(100),
                          id_deposito integer,
                          direccion varchar(200),
                          primary key (id_deposito)
);

CREATE TABLE estado_tramo (
                              id_estado integer,
                              nombre varchar(50) not null,
                              primary key (id_estado)
);

CREATE TABLE ruta (
                      cantidad_depositos integer,
                      cantidad_tramos integer,
                      costo_total float,
                      distancia_total_km float,
                      tiempo_estimado_min float,
                      id_ruta integer,
                      primary key (id_ruta)
);

CREATE TABLE tipo_tramo (
                            id_tipo_tramo integer,
                            nombre varchar(50) not null,
                            primary key (id_tipo_tramo)
);

CREATE TABLE tramo (
                       costo_aproximado numeric(10,2),
                       costo_real numeric(10,2),
                       distancia_km float,
                       latitud_destino float,
                       latitud_origen float,
                       longitud_destino float,
                       longitud_origen float,
                       fecha_hora_fin varchar(255),
                       fecha_hora_inicio varchar(255),
                       id_camion bigint,
                       id_deposito_destino bigint,
                       id_deposito_origen bigint,
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
