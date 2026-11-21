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

@WebServlet("/resumenes-ventas")
public final class ResumenVentasServlet extends HttpServlet {
    private final ServicioReportesVentas servicio = new ServicioReportesVentas();

    @Override
    protected void doGet(HttpServletRequest solicitud, HttpServletResponse respuesta)
            throws ServletException, IOException {
        Usuario usuario = (Usuario) solicitud.getSession().getAttribute("usuario");
        if (usuario == null || !usuario.tienePermiso("ver_resumenes")) {
            respuesta.sendError(HttpServletResponse.SC_FORBIDDEN, "No tienes permiso para consultar resúmenes");
            return;
        }
        try {
            solicitud.setAttribute("resumen", servicio.obtenerResumen());
            solicitud.setAttribute("resumenMetodos", servicio.resumirPorMetodoPago());
            solicitud.getRequestDispatcher("/WEB-INF/vistas/resumenes-ventas.jsp").forward(solicitud, respuesta);
        } catch (SQLException excepcion) {
            getServletContext().log("No fue posible cargar el resumen", excepcion);
            solicitud.setAttribute("error", "No fue posible cargar el resumen de ventas.");
            solicitud.getRequestDispatcher("/WEB-INF/vistas/resumenes-ventas.jsp").forward(solicitud, respuesta);
        }
    }
}
