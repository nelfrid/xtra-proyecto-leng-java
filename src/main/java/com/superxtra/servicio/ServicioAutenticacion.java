package com.superxtra.servicio;

import com.superxtra.dao.UsuarioDAO;
import com.superxtra.model.Usuario;
import java.sql.SQLException;
import org.mindrot.jbcrypt.BCrypt;

// Logica de backend para autenticar y validar a un usuario

public final class ServicioAutenticacion {

    private final UsuarioDAO usuarioDAO;

    public ServicioAutenticacion() {
        this.usuarioDAO = new UsuarioDAO();
    }

    public Usuario autenticar(String correo, String contrasena) throws SQLException {
        // Se guarda en usuario el resultado de la consulta SQL, si no encuentra nada usuario es null, si lo encuentra entonces usuario tiene la informacion del usuario y sus permisos
        Usuario usuario = usuarioDAO.buscarPorCorreo(correo);
        // Si no encuentra el usuasrio o no coincide la contraseña entonces retorna null

        // Compara la contaseña enviada con la contraseña hasheada en la base de datos
        if (usuario == null || !BCrypt.checkpw(contrasena, usuario.getContrasena())) {
            // contrasena es la contraseña ingreso y getContrasena la contraseña encriptada en la base de datos
            return null;
        }
        return usuario;
    }
}
