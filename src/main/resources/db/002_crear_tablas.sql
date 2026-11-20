CREATE DATABASE IF NOT EXISTS superXtra
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE superXtra;

CREATE TABLE IF NOT EXISTS sucursales (
    id_sucursal INT UNSIGNED NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    direccion VARCHAR(255) NOT NULL,
    telefono VARCHAR(30) NULL,
    estado TINYINT(1) NOT NULL DEFAULT 1,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_sucursal),
    UNIQUE KEY uq_sucursales_nombre (nombre)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS roles (
    id_rol INT UNSIGNED NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(50) NOT NULL,
    descripcion VARCHAR(255) NULL,
    PRIMARY KEY (id_rol),
    UNIQUE KEY uq_roles_nombre (nombre)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS permisos (
    id_permiso INT UNSIGNED NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255) NULL,
    PRIMARY KEY (id_permiso),
    UNIQUE KEY uq_permisos_nombre (nombre)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS categorias (
    id_categoria INT UNSIGNED NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255) NULL,
    estado TINYINT(1) NOT NULL DEFAULT 1,
    PRIMARY KEY (id_categoria),
    UNIQUE KEY uq_categorias_nombre (nombre)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS productos (
    id_producto INT UNSIGNED NOT NULL AUTO_INCREMENT,
    id_categoria INT UNSIGNED NOT NULL,
    codigo VARCHAR(50) NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    descripcion VARCHAR(255) NULL,
    precio DECIMAL(12, 2) NOT NULL,
    estado TINYINT(1) NOT NULL DEFAULT 1,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_producto),
    UNIQUE KEY uq_productos_codigo (codigo),
    KEY idx_productos_categoria (id_categoria),
    CONSTRAINT fk_productos_categorias
        FOREIGN KEY (id_categoria)
        REFERENCES categorias (id_categoria)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT chk_productos_precio_no_negativo CHECK (precio >= 0)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS rol_permiso (
    id_rol INT UNSIGNED NOT NULL,
    id_permiso INT UNSIGNED NOT NULL,
    PRIMARY KEY (id_rol, id_permiso),
    KEY idx_rol_permiso_permiso (id_permiso),
    CONSTRAINT fk_rol_permiso_roles
        FOREIGN KEY (id_rol)
        REFERENCES roles (id_rol)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_rol_permiso_permisos
        FOREIGN KEY (id_permiso)
        REFERENCES permisos (id_permiso)
        ON UPDATE CASCADE
        ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS usuarios (
    id_usuario INT UNSIGNED NOT NULL AUTO_INCREMENT,
    id_rol INT UNSIGNED NOT NULL,
    id_sucursal INT UNSIGNED NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    correo VARCHAR(150) NOT NULL,
    contrasena VARCHAR(255) NOT NULL,
    estado TINYINT(1) NOT NULL DEFAULT 1,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_usuario),
    UNIQUE KEY uq_usuarios_correo (correo),
    KEY idx_usuarios_rol (id_rol),
    KEY idx_usuarios_sucursal (id_sucursal),
    CONSTRAINT fk_usuarios_roles
        FOREIGN KEY (id_rol)
        REFERENCES roles (id_rol)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT fk_usuarios_sucursales
        FOREIGN KEY (id_sucursal)
        REFERENCES sucursales (id_sucursal)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS inventario (
    id_inventario INT UNSIGNED NOT NULL AUTO_INCREMENT,
    id_sucursal INT UNSIGNED NOT NULL,
    id_producto INT UNSIGNED NOT NULL,
    cantidad DECIMAL(12, 3) NOT NULL DEFAULT 0,
    stock_minimo DECIMAL(12, 3) NOT NULL DEFAULT 0,
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id_inventario),
    UNIQUE KEY uq_inventario_sucursal_producto (id_sucursal, id_producto),
    KEY idx_inventario_producto (id_producto),
    CONSTRAINT fk_inventario_sucursales
        FOREIGN KEY (id_sucursal)
        REFERENCES sucursales (id_sucursal)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT fk_inventario_productos
        FOREIGN KEY (id_producto)
        REFERENCES productos (id_producto)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT chk_inventario_cantidad_no_negativa CHECK (cantidad >= 0),
    CONSTRAINT chk_inventario_stock_minimo_no_negativo CHECK (stock_minimo >= 0)
) ENGINE = InnoDB;
