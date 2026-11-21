package com.superxtra.model;

import java.math.BigDecimal;

public final class ResumenMetodoPago {
    private final String metodoPago;
    private final long cantidadVentas;
    private final BigDecimal totalVendido;

    public ResumenMetodoPago(String metodoPago, long cantidadVentas, BigDecimal totalVendido) {
        this.metodoPago = metodoPago;
        this.cantidadVentas = cantidadVentas;
        this.totalVendido = totalVendido;
    }

    public String getMetodoPago() { return metodoPago; }
    public long getCantidadVentas() { return cantidadVentas; }
    public BigDecimal getTotalVendido() { return totalVendido; }
}
