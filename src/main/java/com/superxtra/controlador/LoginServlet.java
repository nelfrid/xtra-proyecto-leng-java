package com.superxtra.controlador;

import com.superxtra.model.Usuario;
import com.superxtra.servicio.ServicioAutenticacion;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/login")
public final class LoginServlet extends HttpServlet {

    private final ServicioAutenticacion servicioAutenticacion = new ServicioAutenticacion();

    @Override
    protected void doGet(HttpServletRequest solicitud, HttpServletResponse respuesta)
            throws ServletException, IOException {
        HttpSession sesion = solicitud.getSession(false);
        if (sesion != null && sesion.getAttribute("usuario") != null) {
            respuesta.sendRedirect(solicitud.getContextPath() + "/principal");
            return;
        }
        solicitud.getRequestDispatcher("/WEB-INF/vistas/login.jsp").forward(solicitud, respuesta);
    }

    @Override
    protected void doPost(HttpServletRequest solicitud, HttpServletResponse respuesta)
            throws ServletException, IOException {
        String correo = valor(solicitud.getParameter("correo"));
        String contrasena = valor(solicitud.getParameter("contrasena"));

        if (correo.isBlank() || contrasena.isBlank()) {
            mostrarError(solicitud, respuesta, "Escribe tu correo y contraseña.");
            return;
        }

        try {
            Usuario usuario = servicioAutenticacion.autenticar(correo, contrasena);
            if (usuario == null) {
                mostrarError(solicitud, respuesta, "El correo o la contraseña no son válidos.");
                return;
            }

            HttpSession sesionAnterior = solicitud.getSession(false);
            if (sesionAnterior != null) {
                sesionAnterior.invalidate();
            }
            HttpSession sesion = solicitud.getSession(true);
            sesion.setAttribute("usuario", usuario);
            respuesta.sendRedirect(solicitud.getContextPath() + "/principal");
        } catch (SQLException | IllegalStateException excepcion) {
            getServletContext().log("No fue posible autenticar al usuario", excepcion);
            mostrarError(solicitud, respuesta, "No fue posible iniciar sesión. Verifica la configuración del sistema.");
        }
    }

    private void mostrarError(HttpServletRequest solicitud, HttpServletResponse respuesta, String mensaje)
            throws ServletException, IOException {
        solicitud.setAttribute("error", mensaje);
        solicitud.getRequestDispatcher("/WEB-INF/vistas/login.jsp").forward(solicitud, respuesta);
    }

    private String valor(String valor) {
        return valor == null ? "" : valor.trim();
    }
}
