
-- Verificación del listado inicial completo (Esperado: 12 registros en orden ascendente por código)
SELECT id_producto, codigo, nombre, categoria, precio, stock
FROM public.producto
ORDER BY codigo ASC;

-- Verificación del conteo exacto de registros iniciales (Esperado: 12)
SELECT COUNT(*) AS total_productos
FROM public.producto;

-- Prueba B1: Entrada 'CUADERNO' (Esperado: P001 y P002; contador 2)
SELECT id_producto, codigo, nombre, categoria, precio, stock
FROM public.producto
WHERE (codigo ILIKE '%CUADERNO%' OR nombre ILIKE '%CUADERNO%')
ORDER BY codigo ASC;

-- Prueba B2: Entrada 'T00' (Esperado: T001 a T004; contador 4)
SELECT id_producto, codigo, nombre, categoria, precio, stock
FROM public.producto
WHERE (codigo ILIKE '%T00%' OR nombre ILIKE '%T00%')
ORDER BY codigo ASC;

-- Prueba B3: Categoría 'Papelería', sin texto (Esperado: P001 a P004; contador 4)
SELECT id_producto, codigo, nombre, categoria, precio, stock
FROM public.producto
WHERE categoria = 'Papelería'
ORDER BY codigo ASC;

-- Prueba B4: Solo con existencias (Esperado: 9 productos con stock > 0)
SELECT id_producto, codigo, nombre, categoria, precio, stock
FROM public.producto
WHERE stock > 0
ORDER BY codigo ASC;

-- Prueba B5: 'USB' + Categoría 'Tecnología' + Solo con existencias (Esperado: T001 y T003; contador 2)
SELECT id_producto, codigo, nombre, categoria, precio, stock
FROM public.producto
WHERE (codigo ILIKE '%USB%' OR nombre ILIKE '%USB%')
  AND categoria = 'Tecnología'
  AND stock > 0
ORDER BY codigo ASC;

-- Prueba B6: Entrada 'ZZZ999' (Esperado: 0 productos)
SELECT id_producto, codigo, nombre, categoria, precio, stock
FROM public.producto
WHERE (codigo ILIKE '%ZZZ999%' OR nombre ILIKE '%ZZZ999%')
ORDER BY codigo ASC;

-- Prueba B7: Restauración tras limpiar filtros (Esperado: 12 productos ordenados por código)
SELECT id_producto, codigo, nombre, categoria, precio, stock
FROM public.producto
ORDER BY codigo ASC;