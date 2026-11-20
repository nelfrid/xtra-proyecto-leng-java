package com.superxtra.controlador;

import com.superxtra.model.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/principal")
public final class PrincipalServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest solicitud, HttpServletResponse respuesta)
            throws ServletException, IOException {
        Usuario usuario = (Usuario) solicitud.getSession().getAttribute("usuario");
        solicitud.setAttribute("usuario", usuario);
        solicitud.setAttribute("puedeGestionarSucursales", usuario.tienePermiso("gestionar_sucursales"));
        solicitud.setAttribute("puedeInsertarInventario", usuario.tienePermiso("insertar_inventario"));
        solicitud.setAttribute("puedeEliminarInventario", usuario.tienePermiso("eliminar_inventario"));
        solicitud.setAttribute("puedeGestionarInventario",
                usuario.tienePermiso("insertar_inventario") || usuario.tienePermiso("eliminar_inventario"));
        solicitud.setAttribute("puedeRealizarVentas", usuario.tienePermiso("realizar_ventas"));
        solicitud.setAttribute("puedeVerResumenes", usuario.tienePermiso("ver_resumenes"));
        solicitud.getRequestDispatcher("/WEB-INF/vistas/principal.jsp").forward(solicitud, respuesta);
    }
}
