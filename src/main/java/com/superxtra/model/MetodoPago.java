package com.superxtra.model;

public final class MetodoPago {
    private final int idMetodoPago;
    private final String nombre;

    public MetodoPago(int idMetodoPago, String nombre) {
        this.idMetodoPago = idMetodoPago;
        this.nombre = nombre;
    }

    public int getIdMetodoPago() { return idMetodoPago; }
    public String getNombre() { return nombre; }
}
