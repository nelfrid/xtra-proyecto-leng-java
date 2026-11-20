USE superXtra;

CREATE TABLE IF NOT EXISTS movimientos_inventario (
    id_movimiento INT UNSIGNED NOT NULL AUTO_INCREMENT,
    id_sucursal INT UNSIGNED NOT NULL,
    id_producto INT UNSIGNED NOT NULL,
    id_usuario INT UNSIGNED NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    cantidad DECIMAL(12, 3) NOT NULL,
    fecha_movimiento TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_movimiento),
    KEY idx_movimientos_sucursal (id_sucursal),
    KEY idx_movimientos_producto (id_producto),
    KEY idx_movimientos_usuario (id_usuario),
    CONSTRAINT fk_movimientos_sucursales FOREIGN KEY (id_sucursal)
        REFERENCES sucursales (id_sucursal) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_movimientos_productos FOREIGN KEY (id_producto)
        REFERENCES productos (id_producto) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_movimientos_usuarios FOREIGN KEY (id_usuario)
        REFERENCES usuarios (id_usuario) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT chk_movimientos_cantidad_no_negativa CHECK (cantidad >= 0),
    CONSTRAINT chk_movimientos_tipo CHECK (tipo IN ('ALTA', 'ELIMINACION'))
) ENGINE = InnoDB;
