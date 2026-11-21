package com.superxtra.model;

import java.math.BigDecimal;

/**
 * Producto del catálogo.
 */
public final class Producto {

    private final int idProducto;
    private final String codigo;
    private final String nombre;
    private final BigDecimal precio;
    private final int idUnidadMedida;
    private final BigDecimal existencia;

    public Producto(int idProducto, String codigo, String nombre, BigDecimal precio, int idUnidadMedida) {
        this.idProducto = idProducto;
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
        this.idUnidadMedida = idUnidadMedida;
        this.existencia = null;
    }

    public Producto(int idProducto, String codigo, String nombre, BigDecimal precio,
            int idUnidadMedida, BigDecimal existencia) {
        this.idProducto = idProducto;
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
        this.idUnidadMedida = idUnidadMedida;
        this.existencia = existencia;
    }

    public int getIdProducto() { return idProducto; }
    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public BigDecimal getPrecio() { return precio; }
    public int getIdUnidadMedida() { return idUnidadMedida; }
    public BigDecimal getExistencia() { return existencia; }
}
