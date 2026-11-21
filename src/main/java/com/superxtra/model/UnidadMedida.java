package com.superxtra.model;

public final class UnidadMedida {
    private final int idUnidadMedida;
    private final String nombre;
    private final String abreviatura;

    public UnidadMedida(int idUnidadMedida, String nombre, String abreviatura) {
        this.idUnidadMedida = idUnidadMedida;
        this.nombre = nombre;
        this.abreviatura = abreviatura;
    }

    public int getIdUnidadMedida() { return idUnidadMedida; }
    public String getNombre() { return nombre; }
    public String getAbreviatura() { return abreviatura; }
}
