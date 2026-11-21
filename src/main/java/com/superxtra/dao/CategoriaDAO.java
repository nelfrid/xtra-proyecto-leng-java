package com.superxtra.dao;

import com.superxtra.config.ConexionBD;
import com.superxtra.model.Categoria;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a categorías activas.
 */
public final class CategoriaDAO {

    public List<Categoria> listarActivas() throws SQLException {
        List<Categoria> categorias = new ArrayList<>();
        String consulta = "SELECT id_categoria, nombre FROM categorias WHERE estado = 1 ORDER BY nombre";
        try (Connection conexion = ConexionBD.obtenerConexion();
                PreparedStatement sentencia = conexion.prepareStatement(consulta);
                ResultSet resultado = sentencia.executeQuery()) {
            while (resultado.next()) {
                categorias.add(new Categoria(
                        resultado.getInt("id_categoria"),
                        resultado.getString("nombre")));
            }
        }
        return categorias;
    }
}
