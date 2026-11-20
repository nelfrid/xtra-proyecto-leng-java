package com.superxtra.prueba;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;

import com.superxtra.config.ConexionBD;

/**
 * Verifica que el backend pueda abrir una conexión con MySQL.
 */
class ConexionBaseDatosTest {

    @Test
    void debeConectarseConMySql() throws SQLException {
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            assertNotNull(conexion, "La conexión no debe ser nula");
            assertTrue(conexion.isValid(5), "La conexión debe estar activa");
        }
    }
}
