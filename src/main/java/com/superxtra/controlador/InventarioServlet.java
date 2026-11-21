package com.superxtra.controlador;

import com.superxtra.model.Usuario;
import com.superxtra.model.Sucursal;
import com.superxtra.servicio.ServicioInventario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

@WebServlet("/inventario")
public final class InventarioServlet extends HttpServlet {

    private final ServicioInventario servicioInventario = new ServicioInventario();

    @Override
    protected void doGet(HttpServletRequest solicitud, HttpServletResponse respuesta)
            throws ServletException, IOException {
        Usuario usuario = (Usuario) solicitud.getSession().getAttribute("usuario");
        if (!puedeConsultar(usuario)) {
            respuesta.sendError(HttpServletResponse.SC_FORBIDDEN, "No tienes permiso para consultar inventario");
            return;
        }
        try {
            cargarVista(solicitud, respuesta, usuario, resolverSucursal(solicitud, usuario));
        } catch (IllegalArgumentException | SQLException excepcion) {
            solicitud.setAttribute("error", excepcion.getMessage());
            cargarVistaPropia(solicitud, respuesta, usuario);
        }
    }

    @Override
    protected void doPost(HttpServletRequest solicitud, HttpServletResponse respuesta)
            throws ServletException, IOException {
        Usuario usuario = (Usuario) solicitud.getSession().getAttribute("usuario");
        String accion = valor(solicitud.getParameter("accion"));
        Sucursal sucursal = null;

        try {
            sucursal = resolverSucursal(solicitud, usuario);
            if ("agregar".equals(accion)) {
                exigirPermiso(usuario, "insertar_inventario");
                servicioInventario.agregarProducto(
                        sucursal.getIdSucursal(),
                        entero(solicitud.getParameter("id_producto"), "producto"),
                        usuario.getIdUsuario(),
                        decimal(solicitud.getParameter("cantidad"), "cantidad inicial"),
                        decimal(solicitud.getParameter("stock_minimo"), "stock mínimo"));
                solicitud.getSession().setAttribute("mensaje", "Producto agregado al inventario correctamente.");
            } else if ("eliminar".equals(accion)) {
                exigirPermiso(usuario, "eliminar_inventario");
                servicioInventario.eliminarProducto(
                        sucursal.getIdSucursal(),
                        entero(solicitud.getParameter("id_producto"), "producto"),
                        usuario.getIdUsuario());
                solicitud.getSession().setAttribute("mensaje", "Producto eliminado del inventario correctamente.");
            } else if ("crear".equals(accion)) {
                exigirPermiso(usuario, "insertar_inventario");
                servicioInventario.crearProductoYAgregarlo(
                        sucursal.getIdSucursal(),
                        entero(solicitud.getParameter("id_categoria"), "categoría"),
                        entero(solicitud.getParameter("id_unidad_medida"), "unidad de medida"),
                        valor(solicitud.getParameter("codigo")),
                        valor(solicitud.getParameter("nombre")),
                        solicitud.getParameter("descripcion"),
                        decimal(solicitud.getParameter("precio"), "precio"),
                        decimal(solicitud.getParameter("cantidad"), "cantidad inicial"),
                        decimal(solicitud.getParameter("stock_minimo"), "stock mínimo"),
                        usuario.getIdUsuario());
                solicitud.getSession().setAttribute("mensaje", "Producto creado y agregado al inventario correctamente.");
            } else {
                throw new IllegalArgumentException("La operación de inventario no es válida.");
            }
            respuesta.sendRedirect(solicitud.getContextPath() + "/inventario?id_sucursal="
                    + sucursal.getIdSucursal());
        } catch (IllegalArgumentException | SQLException excepcion) {
            solicitud.setAttribute("error", excepcion.getMessage());
            if (sucursal == null) {
                cargarVistaPropia(solicitud, respuesta, usuario);
            } else {
                cargarVista(solicitud, respuesta, usuario, sucursal);
            }
        }
    }

    private void cargarVista(HttpServletRequest solicitud, HttpServletResponse respuesta, Usuario usuario,
            Sucursal sucursal)
            throws ServletException, IOException {
        try {
            solicitud.setAttribute("productosInventario",
                    servicioInventario.listarPorSucursal(sucursal.getIdSucursal()));
            solicitud.setAttribute("productosDisponibles",
                    servicioInventario.listarProductosDisponibles(sucursal.getIdSucursal()));
            solicitud.setAttribute("categorias", servicioInventario.listarCategorias());
            solicitud.setAttribute("unidadesMedida", servicioInventario.listarUnidadesMedida());
            solicitud.setAttribute("sucursalInventario", sucursal);
            solicitud.setAttribute("puedeCambiarSucursal", usuario.tienePermiso("gestionar_sucursales"));
            solicitud.setAttribute("puedeAgregar", usuario.tienePermiso("insertar_inventario"));
            solicitud.setAttribute("puedeEliminar", usuario.tienePermiso("eliminar_inventario"));
            solicitud.setAttribute("mensaje", solicitud.getSession().getAttribute("mensaje"));
            solicitud.getSession().removeAttribute("mensaje");
            solicitud.getRequestDispatcher("/WEB-INF/vistas/inventario.jsp").forward(solicitud, respuesta);
        } catch (SQLException excepcion) {
            getServletContext().log("No fue posible cargar el inventario", excepcion);
            solicitud.setAttribute("error", "No fue posible cargar el inventario.");
            solicitud.getRequestDispatcher("/WEB-INF/vistas/inventario.jsp").forward(solicitud, respuesta);
        }
    }

    private void cargarVistaPropia(HttpServletRequest solicitud, HttpServletResponse respuesta, Usuario usuario)
            throws ServletException, IOException {
        try {
            Sucursal propia = servicioInventario.buscarSucursalActiva(usuario.getIdSucursal());
            if (propia == null) {
                solicitud.setAttribute("error", "La sucursal del usuario no está disponible.");
                solicitud.getRequestDispatcher("/WEB-INF/vistas/inventario.jsp").forward(solicitud, respuesta);
                return;
            }
            cargarVista(solicitud, respuesta, usuario, propia);
        } catch (SQLException excepcion) {
            getServletContext().log("No fue posible cargar la sucursal del usuario", excepcion);
            solicitud.setAttribute("error", "No fue posible cargar el inventario.");
            solicitud.getRequestDispatcher("/WEB-INF/vistas/inventario.jsp").forward(solicitud, respuesta);
        }
    }

    private Sucursal resolverSucursal(HttpServletRequest solicitud, Usuario usuario) throws SQLException {
        int idSucursal = usuario.getIdSucursal();
        String parametro = valor(solicitud.getParameter("id_sucursal"));

        if (usuario.tienePermiso("gestionar_sucursales") && !parametro.isBlank()) {
            idSucursal = entero(parametro, "sucursal");
        }

        Sucursal sucursal = servicioInventario.buscarSucursalActiva(idSucursal);
        if (sucursal == null) {
            throw new IllegalArgumentException("La sucursal seleccionada no está disponible.");
        }
        return sucursal;
    }

    private void exigirPermiso(Usuario usuario, String permiso) {
        if (usuario == null || !usuario.tienePermiso(permiso)) {
            throw new IllegalArgumentException("No tienes permiso para realizar esta operación.");
        }
    }

    private boolean puedeConsultar(Usuario usuario) {
        return usuario != null && (usuario.tienePermiso("insertar_inventario")
                || usuario.tienePermiso("eliminar_inventario"));
    }

    private int entero(String valor, String campo) {
        try {
            int numero = Integer.parseInt(valor);
            if (numero <= 0) {
                throw new NumberFormatException();
            }
            return numero;
        } catch (NumberFormatException excepcion) {
            throw new IllegalArgumentException("El " + campo + " seleccionado no es válido.");
        }
    }

    private BigDecimal decimal(String valor, String campo) {
        try {
            BigDecimal numero = new BigDecimal(valor);
            if (numero.signum() < 0) {
                throw new NumberFormatException();
            }
            return numero;
        } catch (NumberFormatException | NullPointerException excepcion) {
            throw new IllegalArgumentException("La " + campo + " debe ser cero o mayor.");
        }
    }

    private String valor(String valor) {
        return valor == null ? "" : valor.trim();
    }
}
