package com.superxtra.model;

import java.math.BigDecimal;

public final class ResultadoVenta {
    private final BigDecimal subtotal;
    private final BigDecimal itbms;
    private final BigDecimal total;

    public ResultadoVenta(BigDecimal subtotal, BigDecimal itbms, BigDecimal total) {
        this.subtotal = subtotal;
        this.itbms = itbms;
        this.total = total;
    }

    public BigDecimal getSubtotal() { return subtotal; }
    public BigDecimal getItbms() { return itbms; }
    public BigDecimal getTotal() { return total; }
}
