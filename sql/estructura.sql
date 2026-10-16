-- ==========================================
-- CREACIÓN DE LA BASE DE DATOS
-- ==========================================
-- Ejecutar esta instrucción de forma individual desde la base 'postgres'
-- o crear la base desde el asistente visual de pgAdmin 4 antes de abrir el Query Tool:
-- CREATE DATABASE papeleria_campus;

-- ==========================================
-- CREACIÓN DE LA TABLA
-- ==========================================
DROP TABLE IF EXISTS public.producto;

CREATE TABLE public.producto (
                                 id_producto INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                 codigo VARCHAR(10) NOT NULL UNIQUE,
                                 nombre VARCHAR(80) NOT NULL,
                                 categoria VARCHAR(20) NOT NULL,
                                 precio NUMERIC(10, 2) NOT NULL,
                                 stock INTEGER NOT NULL,

    -- 1. Código normalizado: no vacío, sin espacios laterales y en mayúsculas
                                 CONSTRAINT chk_producto_codigo
                                     CHECK (
                                         char_length(btrim(codigo)) BETWEEN 1 AND 10
                                             AND codigo = btrim(codigo)
                                             AND codigo = upper(codigo)
                                         ),

    -- 2. Nombre de 3 a 80 caracteres después de aplicar trim
                                 CONSTRAINT chk_producto_nombre
                                     CHECK (
                                         char_length(btrim(nombre)) BETWEEN 3 AND 80
                                         ),

    -- 3. Categoría permitida: Papelería, Tecnología o Accesorios
                                 CONSTRAINT chk_producto_categoria
                                     CHECK (
                                         categoria IN ('Papelería', 'Tecnología', 'Accesorios')
                                         ),

    -- 4. Precio estrictamente mayor que cero
                                 CONSTRAINT chk_producto_precio
                                     CHECK (
                                         precio > 0
                                         ),

    -- 5. Stock mayor o igual que cero
                                 CONSTRAINT chk_producto_stock
                                     CHECK (
                                         stock >= 0
                                         )
);