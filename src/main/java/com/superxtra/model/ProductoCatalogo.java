package com.superxtra.model;

import java.math.BigDecimal;

/**
 * Producto activo disponible para asociarse al inventario.
 */
public final class ProductoCatalogo {

    private final int idProducto;
    private final String codigo;
    private final String nombre;
    private final BigDecimal precio;

    public ProductoCatalogo(int idProducto, String codigo, String nombre, BigDecimal precio) {
        this.idProducto = idProducto;
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public BigDecimal getPrecio() {
        return precio;
    }
}
