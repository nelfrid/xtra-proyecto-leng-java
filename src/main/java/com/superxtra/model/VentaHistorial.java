package com.superxtra.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class VentaHistorial {
    private final int idVenta;
    private final String sucursal;
    private final String vendedor;
    private final String metodoPago;
    private final LocalDateTime fecha;
    private final BigDecimal total;
    private final String estado;

    public VentaHistorial(int idVenta, String sucursal, String vendedor, String metodoPago,
            LocalDateTime fecha, BigDecimal total, String estado) {
        this.idVenta = idVenta;
        this.sucursal = sucursal;
        this.vendedor = vendedor;
        this.metodoPago = metodoPago;
        this.fecha = fecha;
        this.total = total;
        this.estado = estado;
    }

    public int getIdVenta() { return idVenta; }
    public String getSucursal() { return sucursal; }
    public String getVendedor() { return vendedor; }
    public String getMetodoPago() { return metodoPago; }
    public LocalDateTime getFecha() { return fecha; }
    public BigDecimal getTotal() { return total; }
    public String getEstado() { return estado; }
}
