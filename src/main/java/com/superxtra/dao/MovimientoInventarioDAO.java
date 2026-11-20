package com.superxtra.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Registra los movimientos producidos por las operaciones de inventario.
 */
public final class MovimientoInventarioDAO {

    private static final String INSERTAR_MOVIMIENTO = """
            INSERT INTO movimientos_inventario
                (id_sucursal, id_producto, id_usuario, tipo, cantidad)
            VALUES (?, ?, ?, ?, ?)
            """;

    public void insertar(Connection conexion, int idSucursal, int idProducto, int idUsuario,
            String tipo, BigDecimal cantidad) throws SQLException {
        try (PreparedStatement sentencia = conexion.prepareStatement(INSERTAR_MOVIMIENTO)) {
            sentencia.setInt(1, idSucursal);
            sentencia.setInt(2, idProducto);
            sentencia.setInt(3, idUsuario);
            sentencia.setString(4, tipo);
            sentencia.setBigDecimal(5, cantidad);
            sentencia.executeUpdate();
        }
    }
}
