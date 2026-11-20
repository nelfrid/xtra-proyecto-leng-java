USE superXtra;

INSERT IGNORE INTO roles (nombre, descripcion)
VALUES
    ('Administrador', 'Acceso completo al prototipo'),
    ('Encargado de inventario', 'Gestiona las entradas y salidas del inventario'),
    ('Cajero', 'Registra las ventas de la sucursal');

INSERT IGNORE INTO permisos (nombre, descripcion)
VALUES
    ('gestionar_sucursales', 'Gestionar sucursales'),
    ('insertar_inventario', 'Insertar productos en el inventario'),
    ('eliminar_inventario', 'Eliminar productos del inventario'),
    ('realizar_ventas', 'Realizar ventas'),
    ('ver_resumenes', 'Consultar resúmenes de ventas');

INSERT IGNORE INTO sucursales (nombre, direccion, telefono)
VALUES ('Xtra Albrook Mall', 'Albrook Mall, Panamá', NULL);

INSERT IGNORE INTO rol_permiso (id_rol, id_permiso)
SELECT r.id_rol, p.id_permiso
FROM roles r
INNER JOIN permisos p
WHERE r.nombre = 'Administrador';

INSERT IGNORE INTO rol_permiso (id_rol, id_permiso)
SELECT r.id_rol, p.id_permiso
FROM roles r
INNER JOIN permisos p ON p.nombre IN ('insertar_inventario', 'eliminar_inventario')
WHERE r.nombre = 'Encargado de inventario';

INSERT IGNORE INTO rol_permiso (id_rol, id_permiso)
SELECT r.id_rol, p.id_permiso
FROM roles r
INNER JOIN permisos p ON p.nombre = 'realizar_ventas'
WHERE r.nombre = 'Cajero';

INSERT INTO usuarios (
    id_rol,
    id_sucursal,
    nombre,
    correo,
    contrasena,
    estado
)
SELECT
    r.id_rol,
    s.id_sucursal,
    'Nelfrid Hassan',
    'nefridh@xtra.com',
    '$2a$12$hBRRCXqR7EtTi9gWpqENI.JPD08hw/Qin7aVn0/NlSJMq9GrUWNVe',
    1
FROM roles r
INNER JOIN sucursales s ON s.nombre = 'Xtra Albrook Mall'
WHERE r.nombre = 'Administrador'
  AND NOT EXISTS (
      SELECT 1
      FROM usuarios u
      WHERE u.correo = 'nefridh@xtra.com'
  );
