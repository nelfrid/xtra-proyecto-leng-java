package com.superxtra.dao;

import com.superxtra.config.ConexionBD;
import com.superxtra.model.UnidadMedida;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class UnidadMedidaDAO {

    public List<UnidadMedida> listarActivas() throws SQLException {
        List<UnidadMedida> unidades = new ArrayList<>();
        String consulta = "SELECT id_unidad_medida, nombre, abreviatura FROM unidades_medida WHERE estado = 1 ORDER BY nombre";
        try (Connection conexion = ConexionBD.obtenerConexion();
                PreparedStatement sentencia = conexion.prepareStatement(consulta);
                ResultSet resultado = sentencia.executeQuery()) {
            while (resultado.next()) {
                unidades.add(new UnidadMedida(resultado.getInt("id_unidad_medida"),
                        resultado.getString("nombre"), resultado.getString("abreviatura")));
            }
        }
        return unidades;
    }
}
