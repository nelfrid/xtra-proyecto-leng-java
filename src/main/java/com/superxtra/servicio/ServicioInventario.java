package com.superxtra.servicio;

import com.superxtra.config.ConexionBD;
import com.superxtra.dao.InventarioDAO;
import com.superxtra.dao.MovimientoInventarioDAO;
import com.superxtra.dao.ProductoDAO;
import com.superxtra.dao.SucursalDAO;
import com.superxtra.model.ProductoCatalogo;
import com.superxtra.model.ProductoInventario;
import com.superxtra.model.Sucursal;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Reglas y transacciones para agregar o eliminar productos del inventario.
 */
public final class ServicioInventario {

    private final InventarioDAO inventarioDAO = new InventarioDAO();
    private final ProductoDAO productoDAO = new ProductoDAO();
    private final MovimientoInventarioDAO movimientoDAO = new MovimientoInventarioDAO();
    private final SucursalDAO sucursalDAO = new SucursalDAO();

    public Sucursal buscarSucursalActiva(int idSucursal) throws SQLException {
        return sucursalDAO.buscarActivaPorId(idSucursal);
    }

    public List<ProductoInventario> listarPorSucursal(int idSucursal) throws SQLException {
        return inventarioDAO.listarPorSucursal(idSucursal);
    }

    public List<ProductoCatalogo> listarProductosDisponibles(int idSucursal) throws SQLException {
        return productoDAO.listarDisponiblesParaSucursal(idSucursal);
    }

    public void agregarProducto(int idSucursal, int idProducto, int idUsuario,
            BigDecimal cantidad, BigDecimal stockMinimo) throws SQLException {
        validarCantidad(cantidad, "La cantidad inicial");
        validarCantidad(stockMinimo, "El stock mínimo");

        try (Connection conexion = ConexionBD.obtenerConexion()) {
            conexion.setAutoCommit(false);
            try {
                if (inventarioDAO.existe(conexion, idSucursal, idProducto)) {
                    throw new IllegalArgumentException("El producto ya existe en el inventario de esta sucursal.");
                }
                if (!inventarioDAO.productoActivo(conexion, idProducto)) {
                    throw new IllegalArgumentException("El producto seleccionado no está activo.");
                }

                inventarioDAO.insertar(conexion, idSucursal, idProducto, cantidad, stockMinimo);
                movimientoDAO.insertar(conexion, idSucursal, idProducto, idUsuario,
                        "ALTA", cantidad);
                conexion.commit();
            } catch (Exception excepcion) {
                conexion.rollback();
                if (excepcion instanceof SQLException sqlException) {
                    throw sqlException;
                }
                if (excepcion instanceof IllegalArgumentException illegalArgumentException) {
                    throw illegalArgumentException;
                }
                throw new SQLException("No fue posible agregar el producto al inventario", excepcion);
            } finally {
                conexion.setAutoCommit(true);
            }
        }
    }

    public void eliminarProducto(int idSucursal, int idProducto, int idUsuario) throws SQLException {
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            conexion.setAutoCommit(false);
            try {
                BigDecimal cantidad = inventarioDAO.obtenerCantidad(conexion, idSucursal, idProducto);
                if (cantidad == null) {
                    throw new IllegalArgumentException("El producto no existe en el inventario de esta sucursal.");
                }

                inventarioDAO.eliminar(conexion, idSucursal, idProducto);
                movimientoDAO.insertar(conexion, idSucursal, idProducto, idUsuario,
                        "ELIMINACION", cantidad);
                conexion.commit();
            } catch (Exception excepcion) {
                conexion.rollback();
                if (excepcion instanceof SQLException sqlException) {
                    throw sqlException;
                }
                if (excepcion instanceof IllegalArgumentException illegalArgumentException) {
                    throw illegalArgumentException;
                }
                throw new SQLException("No fue posible eliminar el producto del inventario", excepcion);
            } finally {
                conexion.setAutoCommit(true);
            }
        }
    }

    private void validarCantidad(BigDecimal cantidad, String campo) {
        if (cantidad == null || cantidad.signum() < 0) {
            throw new IllegalArgumentException(campo + " debe ser cero o mayor.");
        }
    }
}
