-- Script para agregar columnas apellido y dni a la tabla transportista

ALTER TABLE transportista ADD COLUMN apellido TEXT;
ALTER TABLE transportista ADD COLUMN dni TEXT;

