package com.superxtra.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Proporciona conexiones JDBC reutilizables para el backend.
 */
public final class ConexionBD {

    private static final String URL_POR_DEFECTO =
            "jdbc:mysql://localhost:3306/superXtra?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true";

    private ConexionBD() {
    }

    public static Connection obtenerConexion() throws SQLException {
        String url = obtenerVariable("DB_URL", URL_POR_DEFECTO);
        String usuario = obtenerVariableObligatoria("DB_USER");
        String contrasena = obtenerVariableObligatoria("DB_PASSWORD");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException excepcion) {
            throw new SQLException("El controlador JDBC de MySQL no está disponible", excepcion);
        }

        return DriverManager.getConnection(url, usuario, contrasena);
    }

    private static String obtenerVariable(String nombre, String valorPorDefecto) {
        String valor = System.getenv(nombre);
        return valor == null || valor.isBlank() ? valorPorDefecto : valor;
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
