package com.superxtra.dao;

import com.superxtra.config.ConexionBD;
import com.superxtra.model.ProductoInventario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso al inventario filtrado por sucursal.
 */
public final class InventarioDAO {

    private static final String CONSULTA_POR_SUCURSAL = """
            SELECT p.id_producto, p.codigo, p.nombre, p.precio,
                   i.cantidad, i.stock_minimo
            FROM inventario i
            INNER JOIN productos p ON p.id_producto = i.id_producto
            WHERE i.id_sucursal = ?
              AND p.estado = 1
            ORDER BY p.nombre
            """;

    public List<ProductoInventario> listarPorSucursal(int idSucursal) throws SQLException {
        List<ProductoInventario> productos = new ArrayList<>();
        try (Connection conexion = ConexionBD.obtenerConexion();
                PreparedStatement sentencia = conexion.prepareStatement(CONSULTA_POR_SUCURSAL)) {
            sentencia.setInt(1, idSucursal);
            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    productos.add(new ProductoInventario(
                            resultado.getInt("id_producto"),
                            resultado.getString("codigo"),
                            resultado.getString("nombre"),
                            resultado.getBigDecimal("precio"),
                            resultado.getBigDecimal("cantidad"),
                            resultado.getBigDecimal("stock_minimo")));
                }
            }
        }
        return productos;
    }

    public boolean existe(Connection conexion, int idSucursal, int idProducto) throws SQLException {
        String consulta = "SELECT 1 FROM inventario WHERE id_sucursal = ? AND id_producto = ?";
        try (PreparedStatement sentencia = conexion.prepareStatement(consulta)) {
            sentencia.setInt(1, idSucursal);
            sentencia.setInt(2, idProducto);
            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next();
            }
        }
    }

    public boolean productoActivo(Connection conexion, int idProducto) throws SQLException {
        String consulta = "SELECT 1 FROM productos WHERE id_producto = ? AND estado = 1";
        try (PreparedStatement sentencia = conexion.prepareStatement(consulta)) {
            sentencia.setInt(1, idProducto);
            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next();
            }
        }
    }

    public void insertar(Connection conexion, int idSucursal, int idProducto,
            java.math.BigDecimal cantidad, java.math.BigDecimal stockMinimo) throws SQLException {
        String consulta = """
                INSERT INTO inventario (id_sucursal, id_producto, cantidad, stock_minimo)
                VALUES (?, ?, ?, ?)
                """;
        try (PreparedStatement sentencia = conexion.prepareStatement(consulta)) {
            sentencia.setInt(1, idSucursal);
            sentencia.setInt(2, idProducto);
            sentencia.setBigDecimal(3, cantidad);
            sentencia.setBigDecimal(4, stockMinimo);
            sentencia.executeUpdate();
        }
    }

    public java.math.BigDecimal obtenerCantidad(Connection conexion, int idSucursal, int idProducto)
            throws SQLException {
        String consulta = "SELECT cantidad FROM inventario WHERE id_sucursal = ? AND id_producto = ?";
        try (PreparedStatement sentencia = conexion.prepareStatement(consulta)) {
            sentencia.setInt(1, idSucursal);
            sentencia.setInt(2, idProducto);
            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next() ? resultado.getBigDecimal("cantidad") : null;
            }
        }
    }

    public void eliminar(Connection conexion, int idSucursal, int idProducto) throws SQLException {
        String consulta = "DELETE FROM inventario WHERE id_sucursal = ? AND id_producto = ?";
        try (PreparedStatement sentencia = conexion.prepareStatement(consulta)) {
            sentencia.setInt(1, idSucursal);
            sentencia.setInt(2, idProducto);
            if (sentencia.executeUpdate() != 1) {
                throw new SQLException("El producto ya no existe en el inventario.");
            }
        }
    }
}
