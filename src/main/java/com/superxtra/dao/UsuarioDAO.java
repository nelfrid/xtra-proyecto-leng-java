package com.superxtra.dao;

import com.superxtra.config.ConexionBD;
import com.superxtra.model.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 * Acceso a datos de usuarios y sus permisos.
 */
public final class UsuarioDAO {

    private static final String CONSULTA_POR_CORREO = """
            SELECT u.id_usuario, u.nombre, u.correo, u.contrasena,
                   r.id_rol, r.nombre AS nombre_rol,
                   s.id_sucursal, s.nombre AS nombre_sucursal,
                   p.nombre AS permiso
            FROM usuarios u
            INNER JOIN roles r ON r.id_rol = u.id_rol
            INNER JOIN sucursales s ON s.id_sucursal = u.id_sucursal
            LEFT JOIN rol_permiso rp ON rp.id_rol = r.id_rol
            LEFT JOIN permisos p ON p.id_permiso = rp.id_permiso
            WHERE u.correo = ?
              AND u.estado = 1
              AND s.estado = 1
            """;

    public Usuario buscarPorCorreo(String correo) throws SQLException {
        try (Connection conexion = ConexionBD.obtenerConexion();
                PreparedStatement sentencia = conexion.prepareStatement(CONSULTA_POR_CORREO)) {
            sentencia.setString(1, correo);

            try (ResultSet resultado = sentencia.executeQuery()) {
                Usuario usuario = null;
                Set<String> permisos = new HashSet<>();

                while (resultado.next()) {
                    if (usuario == null) {
                        usuario = new Usuario(
                                resultado.getInt("id_usuario"),
                                resultado.getString("nombre"),
                                resultado.getString("correo"),
                                resultado.getString("contrasena"),
                                resultado.getInt("id_rol"),
                                resultado.getString("nombre_rol"),
                                resultado.getInt("id_sucursal"),
                                resultado.getString("nombre_sucursal"),
                                permisos);
                    }

                    String permiso = resultado.getString("permiso");
                    if (permiso != null) {
                        permisos.add(permiso);
                    }
                }

                if (usuario == null) {
                    return null;
                }

                return new Usuario(
                        usuario.getIdUsuario(),
                        usuario.getNombre(),
                        usuario.getCorreo(),
                        usuario.getContrasena(),
                        usuario.getIdRol(),
                        usuario.getNombreRol(),
                        usuario.getIdSucursal(),
                        usuario.getNombreSucursal(),
                        permisos);
            }
        }
    }
}
