package com.superxtra.model;

import java.math.BigDecimal;

public final class ResumenVentas {
    private final long cantidadVentas;
    private final BigDecimal totalVendido;

    public ResumenVentas(long cantidadVentas, BigDecimal totalVendido) {
        this.cantidadVentas = cantidadVentas;
        this.totalVendido = totalVendido;
    }

    public long getCantidadVentas() { return cantidadVentas; }
    public BigDecimal getTotalVendido() { return totalVendido; }
}
