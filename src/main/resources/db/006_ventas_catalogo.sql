USE superXtra;

CREATE TABLE IF NOT EXISTS unidades_medida (
    id_unidad_medida INT UNSIGNED NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(50) NOT NULL,
    abreviatura VARCHAR(10) NOT NULL,
    estado TINYINT(1) NOT NULL DEFAULT 1,
    PRIMARY KEY (id_unidad_medida),
    UNIQUE KEY uq_unidades_medida_nombre (nombre),
    UNIQUE KEY uq_unidades_medida_abreviatura (abreviatura)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS metodos_pago (
    id_metodo_pago INT UNSIGNED NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(50) NOT NULL,
    estado TINYINT(1) NOT NULL DEFAULT 1,
    PRIMARY KEY (id_metodo_pago),
    UNIQUE KEY uq_metodos_pago_nombre (nombre)
) ENGINE = InnoDB;

INSERT IGNORE INTO unidades_medida (nombre, abreviatura) VALUES
    ('Unidad', 'und'), ('Kilogramo', 'kg'), ('Litro', 'L'), ('Caja', 'cja');

INSERT IGNORE INTO metodos_pago (nombre) VALUES
    ('Efectivo'), ('Tarjeta'), ('Transferencia'), ('Yappy');

SET @existe_unidad = (
    SELECT COUNT(*) FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'productos'
      AND column_name = 'id_unidad_medida'
);
SET @sql_unidad = IF(@existe_unidad = 0,
    'ALTER TABLE productos ADD COLUMN id_unidad_medida INT UNSIGNED NULL AFTER id_categoria',
    'SELECT 1');
PREPARE sentencia_unidad FROM @sql_unidad;
EXECUTE sentencia_unidad;
DEALLOCATE PREPARE sentencia_unidad;

UPDATE productos p
INNER JOIN unidades_medida u ON u.abreviatura = 'und'
SET p.id_unidad_medida = u.id_unidad_medida
WHERE p.id_unidad_medida IS NULL;

SET @existe_fk_unidad = (
    SELECT COUNT(*) FROM information_schema.table_constraints
    WHERE constraint_schema = DATABASE() AND table_name = 'productos'
      AND constraint_name = 'fk_productos_unidades_medida'
);
SET @sql_fk_unidad = IF(@existe_fk_unidad = 0,
    'ALTER TABLE productos ADD CONSTRAINT fk_productos_unidades_medida FOREIGN KEY (id_unidad_medida) REFERENCES unidades_medida (id_unidad_medida) ON UPDATE CASCADE ON DELETE RESTRICT',
    'SELECT 1');
PREPARE sentencia_fk_unidad FROM @sql_fk_unidad;
EXECUTE sentencia_fk_unidad;
DEALLOCATE PREPARE sentencia_fk_unidad;

CREATE TABLE IF NOT EXISTS ventas (
    id_venta INT UNSIGNED NOT NULL AUTO_INCREMENT,
    id_sucursal INT UNSIGNED NOT NULL,
    id_vendedor INT UNSIGNED NOT NULL,
    id_metodo_pago INT UNSIGNED NOT NULL,
    fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    subtotal DECIMAL(12, 2) NOT NULL DEFAULT 0,
    itbms DECIMAL(12, 2) NOT NULL DEFAULT 0,
    total DECIMAL(12, 2) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'REGISTRADA',
    PRIMARY KEY (id_venta),
    KEY idx_ventas_sucursal (id_sucursal),
    KEY idx_ventas_vendedor (id_vendedor),
    KEY idx_ventas_metodo_pago (id_metodo_pago),
    CONSTRAINT fk_ventas_sucursales FOREIGN KEY (id_sucursal) REFERENCES sucursales (id_sucursal),
    CONSTRAINT fk_ventas_vendedores FOREIGN KEY (id_vendedor) REFERENCES usuarios (id_usuario),
    CONSTRAINT fk_ventas_metodos_pago FOREIGN KEY (id_metodo_pago) REFERENCES metodos_pago (id_metodo_pago),
    CONSTRAINT chk_ventas_total_no_negativo CHECK (total >= 0)
) ENGINE = InnoDB;

SET @existe_subtotal = (SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'ventas' AND column_name = 'subtotal');
SET @sql_subtotal = IF(@existe_subtotal = 0, 'ALTER TABLE ventas ADD COLUMN subtotal DECIMAL(12, 2) NOT NULL DEFAULT 0 AFTER fecha', 'SELECT 1');
PREPARE sentencia_subtotal FROM @sql_subtotal; EXECUTE sentencia_subtotal; DEALLOCATE PREPARE sentencia_subtotal;
SET @existe_itbms = (SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'ventas' AND column_name = 'itbms');
SET @sql_itbms = IF(@existe_itbms = 0, 'ALTER TABLE ventas ADD COLUMN itbms DECIMAL(12, 2) NOT NULL DEFAULT 0 AFTER subtotal', 'SELECT 1');
PREPARE sentencia_itbms FROM @sql_itbms; EXECUTE sentencia_itbms; DEALLOCATE PREPARE sentencia_itbms;

CREATE TABLE IF NOT EXISTS detalle_ventas (
    id_detalle INT UNSIGNED NOT NULL AUTO_INCREMENT,
    id_venta INT UNSIGNED NOT NULL,
    id_producto INT UNSIGNED NOT NULL,
    cantidad DECIMAL(12, 3) NOT NULL,
    precio_unitario DECIMAL(12, 2) NOT NULL,
    subtotal DECIMAL(12, 2) NOT NULL,
    PRIMARY KEY (id_detalle),
    KEY idx_detalle_venta (id_venta),
    KEY idx_detalle_producto (id_producto),
    CONSTRAINT fk_detalle_ventas_venta FOREIGN KEY (id_venta) REFERENCES ventas (id_venta) ON DELETE CASCADE,
    CONSTRAINT fk_detalle_ventas_producto FOREIGN KEY (id_producto) REFERENCES productos (id_producto),
    CONSTRAINT chk_detalle_cantidad_positiva CHECK (cantidad > 0),
    CONSTRAINT chk_detalle_subtotal_no_negativo CHECK (subtotal >= 0)
) ENGINE = InnoDB;
