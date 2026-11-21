package com.superxtra.controlador;

import com.superxtra.model.ItemVenta;
import com.superxtra.model.Usuario;
import com.superxtra.servicio.ServicioVentas;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/ventas")
public final class VentasServlet extends HttpServlet {

    private final ServicioVentas servicioVentas = new ServicioVentas();

    @Override
    protected void doGet(HttpServletRequest solicitud, HttpServletResponse respuesta)
            throws ServletException, IOException {
        Usuario usuario = (Usuario) solicitud.getSession().getAttribute("usuario");
        if (!puedeVender(usuario)) {
            respuesta.sendError(HttpServletResponse.SC_FORBIDDEN, "No tienes permiso para realizar ventas");
            return;
        }
        cargarVista(solicitud, respuesta, usuario);
    }

    @Override
    protected void doPost(HttpServletRequest solicitud, HttpServletResponse respuesta)
            throws ServletException, IOException {
        Usuario usuario = (Usuario) solicitud.getSession().getAttribute("usuario");
        try {
            exigirPermiso(usuario);
            int idMetodoPago = entero(solicitud.getParameter("id_metodo_pago"), "método de pago");
            String[] productos = solicitud.getParameterValues("id_producto");
            List<ItemVenta> items = new ArrayList<>();
            if (productos != null) {
                for (String producto : productos) {
                    int idProducto = entero(producto, "producto");
                    BigDecimal cantidad = decimal(solicitud.getParameter("cantidad_" + idProducto));
                    items.add(new ItemVenta(idProducto, cantidad));
                }
            }
            var resultado = servicioVentas.registrarVenta(usuario.getIdSucursal(), usuario.getIdUsuario(),
                    idMetodoPago, items);
            solicitud.getSession().setAttribute("mensajeVenta",
                    "Venta registrada correctamente. Total: $ " + resultado.getTotal());
            solicitud.getSession().setAttribute("resultadoVenta", resultado);
            respuesta.sendRedirect(solicitud.getContextPath() + "/ventas");
        } catch (IllegalArgumentException | SQLException excepcion) {
            solicitud.setAttribute("error", excepcion.getMessage());
            cargarVista(solicitud, respuesta, usuario);
        }
    }

    private void cargarVista(HttpServletRequest solicitud, HttpServletResponse respuesta, Usuario usuario)
            throws ServletException, IOException {
        try {
            solicitud.setAttribute("productosVenta",
                    servicioVentas.listarProductosDisponibles(usuario.getIdSucursal()));
            solicitud.setAttribute("metodosPago", servicioVentas.listarMetodosPago());
            solicitud.setAttribute("mensajeVenta", solicitud.getSession().getAttribute("mensajeVenta"));
            solicitud.setAttribute("resultadoVenta", solicitud.getSession().getAttribute("resultadoVenta"));
            solicitud.getSession().removeAttribute("mensajeVenta");
            solicitud.getSession().removeAttribute("resultadoVenta");
            solicitud.getRequestDispatcher("/WEB-INF/vistas/ventas.jsp").forward(solicitud, respuesta);
        } catch (SQLException excepcion) {
            getServletContext().log("No fue posible cargar los productos para venta", excepcion);
            solicitud.setAttribute("error", "No fue posible cargar el módulo de ventas.");
            solicitud.getRequestDispatcher("/WEB-INF/vistas/ventas.jsp").forward(solicitud, respuesta);
        }
    }

    private boolean puedeVender(Usuario usuario) {
        return usuario != null && usuario.tienePermiso("realizar_ventas");
    }

    private void exigirPermiso(Usuario usuario) {
        if (!puedeVender(usuario)) {
            throw new IllegalArgumentException("No tienes permiso para realizar ventas.");
        }
    }

    private int entero(String valor, String campo) {
        try {
            int numero = Integer.parseInt(valor);
            if (numero <= 0) {
                throw new NumberFormatException();
            }
            return numero;
        } catch (NumberFormatException | NullPointerException excepcion) {
            throw new IllegalArgumentException("El " + campo + " no es válido.");
        }
    }

    private BigDecimal decimal(String valor) {
        try {
            BigDecimal numero = new BigDecimal(valor);
            if (numero.signum() <= 0) {
                throw new NumberFormatException();
            }
            return numero;
        } catch (NumberFormatException | NullPointerException excepcion) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }
    }
}
