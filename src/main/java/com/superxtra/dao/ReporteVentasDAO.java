package com.superxtra.dao;

import com.superxtra.config.ConexionBD;
import com.superxtra.model.ResumenMetodoPago;
import com.superxtra.model.ResumenVentas;
import com.superxtra.model.VentaHistorial;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class ReporteVentasDAO {

    public List<VentaHistorial> listarHistorial() throws SQLException {
        List<VentaHistorial> ventas = new ArrayList<>();
        String consulta = """
                SELECT v.id_venta, s.nombre AS sucursal, u.nombre AS vendedor,
                       mp.nombre AS metodo_pago, v.fecha, v.total, v.estado
                FROM ventas v
                INNER JOIN sucursales s ON s.id_sucursal = v.id_sucursal
                INNER JOIN usuarios u ON u.id_usuario = v.id_vendedor
                INNER JOIN metodos_pago mp ON mp.id_metodo_pago = v.id_metodo_pago
                ORDER BY v.fecha DESC, v.id_venta DESC
                """;
        try (Connection conexion = ConexionBD.obtenerConexion();
                PreparedStatement sentencia = conexion.prepareStatement(consulta);
                ResultSet resultado = sentencia.executeQuery()) {
            while (resultado.next()) {
                ventas.add(new VentaHistorial(resultado.getInt("id_venta"),
                        resultado.getString("sucursal"), resultado.getString("vendedor"),
                        resultado.getString("metodo_pago"), resultado.getTimestamp("fecha").toLocalDateTime(),
                        resultado.getBigDecimal("total"), resultado.getString("estado")));
            }
        }
        return ventas;
    }

    public ResumenVentas obtenerResumen() throws SQLException {
        String consulta = "SELECT COUNT(*) AS cantidad, COALESCE(SUM(total), 0) AS total FROM ventas WHERE estado = 'REGISTRADA'";
        try (Connection conexion = ConexionBD.obtenerConexion();
                PreparedStatement sentencia = conexion.prepareStatement(consulta);
                ResultSet resultado = sentencia.executeQuery()) {
            resultado.next();
            return new ResumenVentas(resultado.getLong("cantidad"), resultado.getBigDecimal("total"));
        }
    }

    public List<ResumenMetodoPago> resumirPorMetodoPago() throws SQLException {
        List<ResumenMetodoPago> resumen = new ArrayList<>();
        String consulta = """
                SELECT mp.nombre AS metodo_pago, COUNT(v.id_venta) AS cantidad,
                       COALESCE(SUM(v.total), 0) AS total
                FROM metodos_pago mp
                LEFT JOIN ventas v ON v.id_metodo_pago = mp.id_metodo_pago AND v.estado = 'REGISTRADA'
                WHERE mp.estado = 1
                GROUP BY mp.id_metodo_pago, mp.nombre
                ORDER BY mp.id_metodo_pago
                """;
        try (Connection conexion = ConexionBD.obtenerConexion();
                PreparedStatement sentencia = conexion.prepareStatement(consulta);
                ResultSet resultado = sentencia.executeQuery()) {
            while (resultado.next()) {
                resumen.add(new ResumenMetodoPago(resultado.getString("metodo_pago"),
                        resultado.getLong("cantidad"), resultado.getBigDecimal("total")));
            }
        }
        return resumen;
    }
}
