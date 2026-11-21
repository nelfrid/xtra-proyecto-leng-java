USE superXtra;

INSERT IGNORE INTO sucursales (nombre, direccion, telefono) VALUES
    ('Xtra Calidonia', 'Avenida Central, Calidonia, Panamá', '6000-1002'),
    ('Xtra Arraiján', 'Carretera Panamericana, Arraiján', '6000-1003'),
    ('Xtra Vía España', 'Vía España, Panamá', '6000-1004');

INSERT IGNORE INTO categorias (nombre, descripcion, estado) VALUES
    ('Arroz y granos', 'Arroz, frijoles y granos', 1),
    ('Pastas', 'Pastas y salsas', 1),
    ('Bebidas', 'Bebidas y refrescos', 1),
    ('Lácteos', 'Leche y derivados', 1),
    ('Carnes', 'Carnes y proteínas', 1),
    ('Limpieza', 'Productos de limpieza', 1),
    ('Higiene personal', 'Cuidado e higiene personal', 1),
    ('Panadería', 'Pan y repostería', 1),
    ('Enlatados', 'Alimentos enlatados', 1),
    ('Condimentos', 'Condimentos y especias', 1),
    ('Snacks', 'Bocadillos y snacks', 1),
    ('Congelados', 'Productos congelados', 1),
    ('Frutas', 'Frutas frescas', 1),
    ('Vegetales', 'Vegetales frescos', 1),
    ('Mascotas', 'Alimentos para mascotas', 1),
    ('Bebés', 'Productos para bebés', 1),
    ('Papelería', 'Artículos de papelería', 1),
    ('Hogar', 'Artículos para el hogar', 1),
    ('Desayuno', 'Productos para desayuno', 1),
    ('Refrigerados', 'Productos refrigerados', 1);

INSERT IGNORE INTO productos
    (id_categoria, id_unidad_medida, codigo, nombre, descripcion, precio, estado)
SELECT c.id_categoria, um.id_unidad_medida, d.codigo, d.nombre, d.descripcion, d.precio, 1
FROM (
    SELECT 'SKU-001' codigo, 'Arroz premium 5 kg' nombre, 'Arroz de grano largo' descripcion, 7.95 precio, 'Arroz y granos' categoria, 'kg' unidad
    UNION ALL SELECT 'SKU-002', 'Pasta spaghetti 500 g', 'Pasta de trigo', 1.35, 'Pastas', 'kg'
    UNION ALL SELECT 'SKU-003', 'Jugo de naranja 1 L', 'Jugo natural', 2.25, 'Bebidas', 'L'
    UNION ALL SELECT 'SKU-004', 'Leche entera 1 L', 'Leche pasteurizada', 1.85, 'Lácteos', 'L'
    UNION ALL SELECT 'SKU-005', 'Pollo entero', 'Pollo fresco', 6.50, 'Carnes', 'kg'
    UNION ALL SELECT 'SKU-006', 'Detergente líquido', 'Detergente para ropa', 4.75, 'Limpieza', 'L'
    UNION ALL SELECT 'SKU-007', 'Jabón de baño', 'Jabón corporal', 1.10, 'Higiene personal', 'und'
    UNION ALL SELECT 'SKU-008', 'Pan molde', 'Pan de molde', 2.40, 'Panadería', 'und'
    UNION ALL SELECT 'SKU-009', 'Atún en lata', 'Atún en agua', 1.95, 'Enlatados', 'und'
    UNION ALL SELECT 'SKU-010', 'Sal yodada', 'Sal de mesa', 0.85, 'Condimentos', 'kg'
    UNION ALL SELECT 'SKU-011', 'Papas fritas', 'Snack de papas', 2.15, 'Snacks', 'und'
    UNION ALL SELECT 'SKU-012', 'Vegetales mixtos', 'Vegetales congelados', 3.80, 'Congelados', 'kg'
    UNION ALL SELECT 'SKU-013', 'Banano', 'Banano fresco', 1.25, 'Frutas', 'kg'
    UNION ALL SELECT 'SKU-014', 'Tomate', 'Tomate fresco', 1.60, 'Vegetales', 'kg'
    UNION ALL SELECT 'SKU-015', 'Comida para perro', 'Alimento seco', 9.90, 'Mascotas', 'kg'
    UNION ALL SELECT 'SKU-016', 'Pañales talla M', 'Pañales desechables', 8.50, 'Bebés', 'cja'
    UNION ALL SELECT 'SKU-017', 'Cuaderno universitario', 'Cuaderno de 100 páginas', 2.75, 'Papelería', 'und'
    UNION ALL SELECT 'SKU-018', 'Toalla de cocina', 'Toalla absorbente', 3.25, 'Hogar', 'und'
    UNION ALL SELECT 'SKU-019', 'Cereal de maíz', 'Cereal para desayuno', 4.60, 'Desayuno', 'und'
    UNION ALL SELECT 'SKU-020', 'Yogur natural', 'Yogur refrigerado', 1.45, 'Refrigerados', 'und'
) d
INNER JOIN categorias c ON c.nombre = d.categoria
INNER JOIN unidades_medida um ON um.abreviatura = d.unidad;

INSERT IGNORE INTO inventario (id_sucursal, id_producto, cantidad, stock_minimo)
SELECT s.id_sucursal, p.id_producto, 50, 5
FROM sucursales s
CROSS JOIN productos p
WHERE s.nombre IN ('Xtra Albrook Mall', 'Xtra Calidonia', 'Xtra Arraiján', 'Xtra Vía España')
  AND p.codigo BETWEEN 'SKU-001' AND 'SKU-020';

INSERT IGNORE INTO usuarios (id_rol, id_sucursal, nombre, correo, contrasena, estado)
SELECT r.id_rol, s.id_sucursal, 'Carlos Cajero', 'cajero@xtra.com',
       '$2a$12$FVaWyiNftv1O7qcDWEpePuN3tgwIxqvVxfojTY/lu0l285UulDnLC', 1
FROM roles r CROSS JOIN sucursales s
WHERE r.nombre = 'Cajero' AND s.nombre = 'Xtra Albrook Mall'
  AND NOT EXISTS (SELECT 1 FROM usuarios u WHERE u.correo = 'cajero@xtra.com');

INSERT IGNORE INTO usuarios (id_rol, id_sucursal, nombre, correo, contrasena, estado)
SELECT r.id_rol, s.id_sucursal, 'María Inventario', 'inventario@xtra.com',
       '$2a$12$2TUHVzvVvwZhPbn1sBCwz.3eCS9yF4YDEzhCagBiHTrOQkpveoUlu', 1
FROM roles r CROSS JOIN sucursales s
WHERE r.nombre = 'Encargado de inventario' AND s.nombre = 'Xtra Albrook Mall'
  AND NOT EXISTS (SELECT 1 FROM usuarios u WHERE u.correo = 'inventario@xtra.com');
