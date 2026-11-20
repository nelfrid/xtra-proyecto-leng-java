package com.superxtra.dao;

import com.superxtra.config.ConexionBD;
import com.superxtra.model.Sucursal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a datos de sucursales.
 */
public final class SucursalDAO {

    private static final String CONSULTA_SUCURSALES_ACTIVAS = """
            SELECT id_sucursal, nombre, direccion, telefono
            FROM sucursales
            WHERE estado = 1
            ORDER BY nombre
            """;

    private static final String CONSULTA_SUCURSAL_ACTIVA = """
            SELECT id_sucursal, nombre, direccion, telefono
            FROM sucursales
            WHERE id_sucursal = ? AND estado = 1
            """;

    public List<Sucursal> listarActivas() throws SQLException {
        List<Sucursal> sucursales = new ArrayList<>();
        try (Connection conexion = ConexionBD.obtenerConexion();
                PreparedStatement sentencia = conexion.prepareStatement(CONSULTA_SUCURSALES_ACTIVAS);
                ResultSet resultado = sentencia.executeQuery()) {
            while (resultado.next()) {
                sucursales.add(mapear(resultado));
            }
        }
        return sucursales;
    }

    public Sucursal buscarActivaPorId(int idSucursal) throws SQLException {
        try (Connection conexion = ConexionBD.obtenerConexion();
                PreparedStatement sentencia = conexion.prepareStatement(CONSULTA_SUCURSAL_ACTIVA)) {
            sentencia.setInt(1, idSucursal);
            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next() ? mapear(resultado) : null;
            }
        }
    }

    private Sucursal mapear(ResultSet resultado) throws SQLException {
        return new Sucursal(
                resultado.getInt("id_sucursal"),
                resultado.getString("nombre"),
                resultado.getString("direccion"),
                resultado.getString("telefono"));
    }
}
