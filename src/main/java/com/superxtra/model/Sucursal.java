package com.superxtra.model;

/**
 * Representa una sucursal activa del supermercado.
 */
public final class Sucursal {

    private final int idSucursal;
    private final String nombre;
    private final String direccion;
    private final String telefono;

    public Sucursal(int idSucursal, String nombre, String direccion, String telefono) {
        this.idSucursal = idSucursal;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
    }

    public int getIdSucursal() {
        return idSucursal;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getTelefono() {
        return telefono;
    }
}
