package com.superxtra.model;

import java.math.BigDecimal;

/**
 * Existencia de un producto dentro de una sucursal.
 */
public final class Inventario {

    private final int idProducto;
    private final String codigo;
    private final String nombre;
    private final BigDecimal precio;
    private final BigDecimal cantidad;
    private final BigDecimal stockMinimo;

    public Inventario(int idProducto, String codigo, String nombre, BigDecimal precio,
            BigDecimal cantidad, BigDecimal stockMinimo) {
        this.idProducto = idProducto;
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
        this.cantidad = cantidad;
        this.stockMinimo = stockMinimo;
    }

    public int getIdProducto() { return idProducto; }
    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public BigDecimal getPrecio() { return precio; }
    public BigDecimal getCantidad() { return cantidad; }
    public BigDecimal getStockMinimo() { return stockMinimo; }
}
