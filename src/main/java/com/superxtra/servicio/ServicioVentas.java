package com.superxtra.servicio;

import com.superxtra.config.ConexionBD;
import com.superxtra.dao.MetodoPagoDAO;
import com.superxtra.dao.VentaDAO;
import com.superxtra.model.ItemVenta;
import com.superxtra.model.MetodoPago;
import com.superxtra.model.Producto;
import com.superxtra.model.ResultadoVenta;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Registra ventas y descuenta inventario dentro de una única transacción.
 */
public final class ServicioVentas {

    private final VentaDAO ventaDAO = new VentaDAO();
    private final MetodoPagoDAO metodoPagoDAO = new MetodoPagoDAO();

    public List<Producto> listarProductosDisponibles(int idSucursal) throws SQLException {
        return ventaDAO.listarProductosDisponibles(idSucursal);
    }

    public List<MetodoPago> listarMetodosPago() throws SQLException {
        return metodoPagoDAO.listarActivos();
    }

    public ResultadoVenta registrarVenta(int idSucursal, int idVendedor, int idMetodoPago,
            List<ItemVenta> items) throws SQLException {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("La venta debe incluir al menos un producto.");
        }
        Set<Integer> productosRepetidos = new HashSet<>();
        for (ItemVenta item : items) {
            if (item.cantidad() == null || item.cantidad().signum() <= 0
                    || !productosRepetidos.add(item.idProducto())) {
                throw new IllegalArgumentException("Las cantidades deben ser positivas y no debe repetir productos.");
            }
        }

        try (Connection conexion = ConexionBD.obtenerConexion()) {
            conexion.setAutoCommit(false);
            try {
                if (!ventaDAO.sucursalActiva(conexion, idSucursal)) {
                    throw new IllegalArgumentException("La sucursal no está activa.");
                }
                if (!ventaDAO.vendedorActivo(conexion, idVendedor, idSucursal)) {
                    throw new IllegalArgumentException("El vendedor no está activo en esta sucursal.");
                }
                if (!ventaDAO.metodoPagoActivo(conexion, idMetodoPago)) {
                    throw new IllegalArgumentException("El método de pago seleccionado no es válido.");
                }

                BigDecimal subtotal = BigDecimal.ZERO;
                var productos = new java.util.ArrayList<Producto>();
                for (ItemVenta item : items) {
                    Producto producto = ventaDAO.buscarYBloquear(conexion, idSucursal, item.idProducto());
                    if (producto == null) {
                        throw new IllegalArgumentException("Uno de los productos no está disponible.");
                    }
                    if (producto.getExistencia().compareTo(item.cantidad()) < 0) {
                        throw new IllegalArgumentException("No existe suficiente inventario para " + producto.getNombre() + ".");
                    }
                    productos.add(producto);
                    subtotal = subtotal.add(producto.getPrecio().multiply(item.cantidad()));
                }
                subtotal = subtotal.setScale(2, RoundingMode.HALF_UP);
                BigDecimal itbms = subtotal.multiply(new BigDecimal("0.10")).setScale(2, RoundingMode.HALF_UP);
                BigDecimal total = subtotal.add(itbms).setScale(2, RoundingMode.HALF_UP);

                int idVenta = ventaDAO.insertarVenta(conexion, idSucursal, idVendedor, idMetodoPago,
                        subtotal, itbms, total);
                for (int indice = 0; indice < items.size(); indice++) {
                    ItemVenta item = items.get(indice);
                    Producto producto = productos.get(indice);
                    BigDecimal subtotalDetalle = producto.getPrecio().multiply(item.cantidad())
                            .setScale(2, RoundingMode.HALF_UP);
                    ventaDAO.insertarDetalle(conexion, idVenta, item.idProducto(), item.cantidad(),
                            producto.getPrecio(), subtotalDetalle);
                    ventaDAO.descontarInventario(conexion, idSucursal, item.idProducto(), item.cantidad());
                }
                conexion.commit();
                return new ResultadoVenta(subtotal, itbms, total);
            } catch (Exception excepcion) {
                conexion.rollback();
                if (excepcion instanceof SQLException sqlException) {
                    throw sqlException;
                }
                if (excepcion instanceof IllegalArgumentException illegalArgumentException) {
                    throw illegalArgumentException;
                }
                throw new SQLException("No fue posible registrar la venta", excepcion);
            } finally {
                conexion.setAutoCommit(true);
            }
        }
    }
}
