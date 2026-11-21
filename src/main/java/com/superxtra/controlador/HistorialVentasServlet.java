package com.superxtra.controlador;

import com.superxtra.model.Usuario;
import com.superxtra.servicio.ServicioReportesVentas;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/historial-ventas")
public final class HistorialVentasServlet extends HttpServlet {
    private final ServicioReportesVentas servicio = new ServicioReportesVentas();

    @Override
    protected void doGet(HttpServletRequest solicitud, HttpServletResponse respuesta)
            throws ServletException, IOException {
        Usuario usuario = (Usuario) solicitud.getSession().getAttribute("usuario");
        if (usuario == null || !usuario.tienePermiso("ver_resumenes")) {
            respuesta.sendError(HttpServletResponse.SC_FORBIDDEN, "No tienes permiso para consultar el historial");
            return;
        }
        try {
            solicitud.setAttribute("ventas", servicio.listarHistorial());
            solicitud.getRequestDispatcher("/WEB-INF/vistas/historial-ventas.jsp").forward(solicitud, respuesta);
        } catch (SQLException excepcion) {
            getServletContext().log("No fue posible cargar el historial", excepcion);
            solicitud.setAttribute("error", "No fue posible cargar el historial de ventas.");
            solicitud.getRequestDispatcher("/WEB-INF/vistas/historial-ventas.jsp").forward(solicitud, respuesta);
        }
    }
}
