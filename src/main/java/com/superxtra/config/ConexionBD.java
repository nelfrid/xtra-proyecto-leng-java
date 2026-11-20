package com.superxtra.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Proporciona conexiones JDBC reutilizables para el backend.
 */
public final class ConexionBD {

    private ConexionBD() {
    }

    public static Connection obtenerConexion() throws SQLException {
        String url = obtenerVariableObligatoria("DB_URL");
        String usuario = obtenerVariableObligatoria("DB_USER");
        String contrasena = obtenerVariableObligatoria("DB_PASSWORD");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException excepcion) {
            throw new SQLException("El controlador JDBC de MySQL no está disponible", excepcion);
        }

        return DriverManager.getConnection(url, usuario, contrasena);
    }

    private static String obtenerVariableObligatoria(String nombre) {
        String valor = System.getenv(nombre);
        if (valor == null || valor.isBlank()) {
            throw new IllegalStateException(
                    "La variable de entorno " + nombre + " debe estar configurada");
        }
        return valor;
    }
}
