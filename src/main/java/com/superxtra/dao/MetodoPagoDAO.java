package com.superxtra.dao;

import com.superxtra.config.ConexionBD;
import com.superxtra.model.MetodoPago;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class MetodoPagoDAO {

    public List<MetodoPago> listarActivos() throws SQLException {
        List<MetodoPago> metodos = new ArrayList<>();
        String consulta = "SELECT id_metodo_pago, nombre FROM metodos_pago WHERE estado = 1 ORDER BY id_metodo_pago";
        try (Connection conexion = ConexionBD.obtenerConexion();
                PreparedStatement sentencia = conexion.prepareStatement(consulta);
                ResultSet resultado = sentencia.executeQuery()) {
            while (resultado.next()) {
                metodos.add(new MetodoPago(resultado.getInt("id_metodo_pago"),
                        resultado.getString("nombre")));
            }
        }
        return metodos;
    }
}
