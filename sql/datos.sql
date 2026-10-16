-- Vacía la tabla public.producto y reinicia la secuencia del ID a 1
-- sin afectar ninguna otra base de datos del servidor.
TRUNCATE TABLE public.producto RESTART IDENTITY;


-- INSERCIÓN DE LOS 12 DATOS INICIALES OBLIGATORIOS
INSERT INTO public.producto (codigo, nombre, categoria, precio, stock) VALUES
-- Categoría: Papelería (4 productos)
('P001', 'Cuaderno universitario', 'Papelería', 65.00, 25),
('P002', 'Cuaderno cuadriculado', 'Papelería', 72.50, 12),
('P003', 'Lapicero azul', 'Papelería', 12.00, 80),
('P004', 'Resma de papel carta', 'Papelería', 185.00, 0),

-- Categoría: Tecnología (4 productos)
('T001', 'Memoria USB 32 GB', 'Tecnología', 210.00, 8),
('T002', 'Mouse inalámbrico', 'Tecnología', 320.00, 6),
('T003', 'Teclado USB', 'Tecnología', 450.00, 4),
('T004', 'Cable HDMI', 'Tecnología', 180.00, 0),

-- Categoría: Accesorios (4 productos)
('A001', 'Mochila escolar', 'Accesorios', 850.00, 5),
('A002', 'Estuche de lápices', 'Accesorios', 95.00, 18),
('A003', 'Botella reutilizable', 'Accesorios', 240.00, 0),
('A004', 'Regla metálica', 'Accesorios', 45.00, 20);

-- CONSULTA DE VERIFICACIÓN INICIAL

SELECT id_producto, codigo, nombre, categoria, precio, stock
FROM public.producto
ORDER BY codigo ASC;