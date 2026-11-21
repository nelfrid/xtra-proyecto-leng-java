package com.superxtra.dao;

import com.superxtra.config.ConexionBD;
import com.superxtra.model.Producto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Consultas de productos que pueden asociarse a un inventario.
 */
public final class ProductoDAO {

    private static final String CONSULTA_DISPONIBLES = """
            SELECT p.id_producto, p.codigo, p.nombre, p.precio, p.id_unidad_medida
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

    public List<Producto> listarDisponiblesParaSucursal(int idSucursal) throws SQLException {
        List<Producto> productos = new ArrayList<>();
        try (Connection conexion = ConexionBD.obtenerConexion();
                PreparedStatement sentencia = conexion.prepareStatement(CONSULTA_DISPONIBLES)) {
            sentencia.setInt(1, idSucursal);
            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    productos.add(new Producto(
                            resultado.getInt("id_producto"),
                            resultado.getString("codigo"),
                            resultado.getString("nombre"),
                            resultado.getBigDecimal("precio"),
                            resultado.getInt("id_unidad_medida")));
                }
            }
        }
        return productos;
    }

    public boolean existeCodigo(Connection conexion, String codigo) throws SQLException {
        String consulta = "SELECT 1 FROM productos WHERE codigo = ?";
        try (PreparedStatement sentencia = conexion.prepareStatement(consulta)) {
            sentencia.setString(1, codigo);
            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next();
            }
        }
    }

    public boolean categoriaActiva(Connection conexion, int idCategoria) throws SQLException {
        String consulta = "SELECT 1 FROM categorias WHERE id_categoria = ? AND estado = 1";
        try (PreparedStatement sentencia = conexion.prepareStatement(consulta)) {
            sentencia.setInt(1, idCategoria);
            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next();
            }
        }
    }

    public boolean unidadActiva(Connection conexion, int idUnidadMedida) throws SQLException {
        String consulta = "SELECT 1 FROM unidades_medida WHERE id_unidad_medida = ? AND estado = 1";
        try (PreparedStatement sentencia = conexion.prepareStatement(consulta)) {
            sentencia.setInt(1, idUnidadMedida);
            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next();
            }
        }
    }

    public int insertar(Connection conexion, int idCategoria, int idUnidadMedida, String codigo, String nombre,
            String descripcion, java.math.BigDecimal precio) throws SQLException {
        String consulta = """
                INSERT INTO productos (id_categoria, id_unidad_medida, codigo, nombre, descripcion, precio, estado)
                VALUES (?, ?, ?, ?, ?, ?, 1)
                """;
        try (PreparedStatement sentencia = conexion.prepareStatement(consulta, Statement.RETURN_GENERATED_KEYS)) {
            sentencia.setInt(1, idCategoria);
            sentencia.setInt(2, idUnidadMedida);
            sentencia.setString(3, codigo);
            sentencia.setString(4, nombre);
            sentencia.setString(5, descripcion);
            sentencia.setBigDecimal(6, precio);
            sentencia.executeUpdate();
            try (ResultSet claves = sentencia.getGeneratedKeys()) {
                if (!claves.next()) {
                    throw new SQLException("No se pudo obtener el producto creado.");
                }
                return claves.getInt(1);
            }
        }
    }
}
