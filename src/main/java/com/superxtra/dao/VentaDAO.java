package com.superxtra.dao;

import com.superxtra.config.ConexionBD;
import com.superxtra.model.Producto;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public final class VentaDAO {

    private static final String PRODUCTOS_DISPONIBLES = """
            SELECT p.id_producto, p.codigo, p.nombre, p.precio, i.cantidad AS existencia
            FROM inventario i
            INNER JOIN productos p ON p.id_producto = i.id_producto
            WHERE i.id_sucursal = ? AND p.estado = 1 AND i.cantidad > 0
            ORDER BY p.nombre
            """;

    public List<Producto> listarProductosDisponibles(int idSucursal) throws SQLException {
        List<Producto> productos = new ArrayList<>();
        try (Connection conexion = ConexionBD.obtenerConexion();
                PreparedStatement sentencia = conexion.prepareStatement(PRODUCTOS_DISPONIBLES)) {
            sentencia.setInt(1, idSucursal);
            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    productos.add(mapear(resultado));
                }
            }
        }
        return productos;
    }

    public Producto buscarYBloquear(Connection conexion, int idSucursal, int idProducto)
            throws SQLException {
        String consulta = """
                SELECT p.id_producto, p.codigo, p.nombre, p.precio, i.cantidad AS existencia
                FROM inventario i
                INNER JOIN productos p ON p.id_producto = i.id_producto
                WHERE i.id_sucursal = ? AND p.id_producto = ? AND p.estado = 1
                FOR UPDATE
                """;
        try (PreparedStatement sentencia = conexion.prepareStatement(consulta)) {
            sentencia.setInt(1, idSucursal);
            sentencia.setInt(2, idProducto);
            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next() ? mapear(resultado) : null;
            }
        }
    }

    public boolean metodoPagoActivo(Connection conexion, int idMetodoPago) throws SQLException {
        String consulta = "SELECT 1 FROM metodos_pago WHERE id_metodo_pago = ? AND estado = 1";
        try (PreparedStatement sentencia = conexion.prepareStatement(consulta)) {
            sentencia.setInt(1, idMetodoPago);
            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next();
            }
        }
    }

    public boolean sucursalActiva(Connection conexion, int idSucursal) throws SQLException {
        String consulta = "SELECT 1 FROM sucursales WHERE id_sucursal = ? AND estado = 1";
        try (PreparedStatement sentencia = conexion.prepareStatement(consulta)) {
            sentencia.setInt(1, idSucursal);
            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next();
            }
        }
    }

    public boolean vendedorActivo(Connection conexion, int idVendedor, int idSucursal) throws SQLException {
        String consulta = "SELECT 1 FROM usuarios WHERE id_usuario = ? AND id_sucursal = ? AND estado = 1";
        try (PreparedStatement sentencia = conexion.prepareStatement(consulta)) {
            sentencia.setInt(1, idVendedor);
            sentencia.setInt(2, idSucursal);
            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next();
            }
        }
    }

    public int insertarVenta(Connection conexion, int idSucursal, int idVendedor,
            int idMetodoPago, BigDecimal subtotal, BigDecimal itbms, BigDecimal total) throws SQLException {
        String consulta = """
                INSERT INTO ventas (id_sucursal, id_vendedor, id_metodo_pago, subtotal, itbms, total, estado)
                VALUES (?, ?, ?, ?, ?, ?, 'REGISTRADA')
                """;
        try (PreparedStatement sentencia = conexion.prepareStatement(consulta, Statement.RETURN_GENERATED_KEYS)) {
            sentencia.setInt(1, idSucursal);
            sentencia.setInt(2, idVendedor);
            sentencia.setInt(3, idMetodoPago);
            sentencia.setBigDecimal(4, subtotal);
            sentencia.setBigDecimal(5, itbms);
            sentencia.setBigDecimal(6, total);
            sentencia.executeUpdate();
            try (ResultSet claves = sentencia.getGeneratedKeys()) {
                if (!claves.next()) {
                    throw new SQLException("No se pudo obtener la venta creada.");
                }
                return claves.getInt(1);
            }
        }
    }

    public void insertarDetalle(Connection conexion, int idVenta, int idProducto,
            BigDecimal cantidad, BigDecimal precio, BigDecimal subtotal) throws SQLException {
        String consulta = """
                INSERT INTO detalle_ventas (id_venta, id_producto, cantidad, precio_unitario, subtotal)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (PreparedStatement sentencia = conexion.prepareStatement(consulta)) {
            sentencia.setInt(1, idVenta);
            sentencia.setInt(2, idProducto);
            sentencia.setBigDecimal(3, cantidad);
            sentencia.setBigDecimal(4, precio);
            sentencia.setBigDecimal(5, subtotal);
            sentencia.executeUpdate();
        }
    }

    public void descontarInventario(Connection conexion, int idSucursal, int idProducto,
            BigDecimal cantidad) throws SQLException {
        String consulta = """
                UPDATE inventario SET cantidad = cantidad - ?
                WHERE id_sucursal = ? AND id_producto = ? AND cantidad >= ?
                """;
        try (PreparedStatement sentencia = conexion.prepareStatement(consulta)) {
            sentencia.setBigDecimal(1, cantidad);
            sentencia.setInt(2, idSucursal);
            sentencia.setInt(3, idProducto);
            sentencia.setBigDecimal(4, cantidad);
            if (sentencia.executeUpdate() != 1) {
                throw new IllegalArgumentException("La existencia cambió y ya no es suficiente.");
            }
        }
    }

    private Producto mapear(ResultSet resultado) throws SQLException {
        return new Producto(resultado.getInt("id_producto"), resultado.getString("codigo"),
                resultado.getString("nombre"), resultado.getBigDecimal("precio"), 0,
                resultado.getBigDecimal("existencia"));
    }
}
