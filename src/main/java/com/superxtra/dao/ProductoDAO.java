package com.superxtra.dao;

import com.superxtra.config.ConexionBD;
import com.superxtra.model.ProductoCatalogo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Consultas de productos que pueden asociarse a un inventario.
 */
public final class ProductoDAO {

    private static final String CONSULTA_DISPONIBLES = """
            SELECT p.id_producto, p.codigo, p.nombre, p.precio
            FROM productos p
            WHERE p.estado = 1
              AND NOT EXISTS (
                  SELECT 1
                  FROM inventario i
                  WHERE i.id_producto = p.id_producto
                    AND i.id_sucursal = ?
              )
            ORDER BY p.nombre
            """;

    public List<ProductoCatalogo> listarDisponiblesParaSucursal(int idSucursal) throws SQLException {
        List<ProductoCatalogo> productos = new ArrayList<>();
        try (Connection conexion = ConexionBD.obtenerConexion();
                PreparedStatement sentencia = conexion.prepareStatement(CONSULTA_DISPONIBLES)) {
            sentencia.setInt(1, idSucursal);
            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    productos.add(new ProductoCatalogo(
                            resultado.getInt("id_producto"),
                            resultado.getString("codigo"),
                            resultado.getString("nombre"),
                            resultado.getBigDecimal("precio")));
                }
            }
        }
        return productos;
    }
}
