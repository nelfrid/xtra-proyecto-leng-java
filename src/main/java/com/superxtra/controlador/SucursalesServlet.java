package com.superxtra.controlador;

import com.superxtra.model.Sucursal;
import com.superxtra.model.Usuario;
import com.superxtra.servicio.ServicioSucursales;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/sucursales")
public final class SucursalesServlet extends HttpServlet {

    private final ServicioSucursales servicioSucursales = new ServicioSucursales();

    @Override
    protected void doGet(HttpServletRequest solicitud, HttpServletResponse respuesta)
            throws ServletException, IOException {
        Usuario usuario = (Usuario) solicitud.getSession().getAttribute("usuario");
        if (!usuario.tienePermiso("gestionar_sucursales")) {
            respuesta.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "No tienes permiso para gestionar sucursales");
            return;
        }

        try {
            var sucursales = servicioSucursales.listarSucursalesActivas();
            Sucursal seleccionada = seleccionarSucursal(solicitud, sucursales, usuario);
            solicitud.setAttribute("sucursales", sucursales);
            solicitud.setAttribute("sucursalSeleccionada", seleccionada);
            if (seleccionada != null) {
                solicitud.setAttribute("productosInventario",
                        servicioSucursales.listarProductosPorSucursal(seleccionada.getIdSucursal()));
            }
            solicitud.getRequestDispatcher("/WEB-INF/vistas/sucursales.jsp").forward(solicitud, respuesta);
        } catch (SQLException | NumberFormatException excepcion) {
            getServletContext().log("No fue posible consultar las sucursales", excepcion);
            solicitud.setAttribute("error", "No fue posible cargar la información de sucursales.");
            solicitud.getRequestDispatcher("/WEB-INF/vistas/sucursales.jsp").forward(solicitud, respuesta);
        }
    }

    private Sucursal seleccionarSucursal(HttpServletRequest solicitud, java.util.List<Sucursal> sucursales,
            Usuario usuario) throws SQLException {
        String parametro = solicitud.getParameter("id_sucursal");
        int idSucursal = parametro == null || parametro.isBlank()
                ? usuario.getIdSucursal()
                : Integer.parseInt(parametro);

        return sucursales.stream()
                .filter(sucursal -> sucursal.getIdSucursal() == idSucursal)
                .findFirst()
                .orElse(null);
    }
}
