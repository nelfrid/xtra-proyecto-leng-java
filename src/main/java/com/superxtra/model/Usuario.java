package com.superxtra.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

// Clase de usuario

public final class Usuario {

    private final int idUsuario;
    private final String nombre;
    private final String correo;
    private final String contrasena;
    private final int idRol;
    private final String nombreRol;
    private final int idSucursal;
    private final String nombreSucursal;
    private final Set<String> permisos;

    public Usuario(int idUsuario, String nombre, String correo, String contrasena,
            int idRol, String nombreRol, int idSucursal, String nombreSucursal,
            Set<String> permisos) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.correo = correo;
        this.contrasena = contrasena;
        this.idRol = idRol;
        this.nombreRol = nombreRol;
        this.idSucursal = idSucursal;
        this.nombreSucursal = nombreSucursal;
        this.permisos = Collections.unmodifiableSet(new HashSet<>(permisos));
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public int getIdRol() {
        return idRol;
    }

    public String getNombreRol() {
        return nombreRol;
    }

    public int getIdSucursal() {
        return idSucursal;
    }

    public String getNombreSucursal() {
        return nombreSucursal;
    }

    public Set<String> getPermisos() {
        return permisos;
    }

    public boolean tienePermiso(String permiso) {
        return permisos.contains(permiso);
    }
}
