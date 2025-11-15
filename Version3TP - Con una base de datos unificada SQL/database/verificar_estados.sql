-- ========================================
-- VERIFICACIÓN DE ESTADOS DESPUÉS DE LIMPIEZA
-- ========================================

-- 1. Ver cuántos estados quedan en total
SELECT COUNT(*) as 'Total de estados' FROM estado;

-- 2. Ver todos los estados que quedaron
SELECT * FROM estado ORDER BY id_estado;

-- 3. Verificar el próximo ID que se asignará
SELECT * FROM sqlite_sequence WHERE name = 'estado';

