-- =====================================================================
-- V2 - La cantidad de los ítems de pedido admite decimales
-- (por ejemplo 2,5 kg). Hasta 3 decimales: gramos, mililitros.
-- Los valores enteros existentes se conservan (5 -> 5.000).
-- =====================================================================

ALTER TABLE public.items_pedido
    ALTER COLUMN cantidad TYPE numeric(12,3);
