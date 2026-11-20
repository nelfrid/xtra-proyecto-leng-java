package com.superxtra.servicio;

import com.superxtra.dao.UsuarioDAO;
import com.superxtra.model.Usuario;
import java.sql.SQLException;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Contiene la lógica de autenticación.
 */
public final class ServicioAutenticacion {

    private final UsuarioDAO usuarioDAO;

    public ServicioAutenticacion() {
        this.usuarioDAO = new UsuarioDAO();
    }

    public Usuario autenticar(String correo, String contrasena) throws SQLException {
        Usuario usuario = usuarioDAO.buscarPorCorreo(correo);
        if (usuario == null || !BCrypt.checkpw(contrasena, usuario.getContrasena())) {
            return null;
        }
        return usuario;
    }
}
