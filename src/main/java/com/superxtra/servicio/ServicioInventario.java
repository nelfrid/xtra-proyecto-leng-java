package com.superxtra.servicio;

import com.superxtra.config.ConexionBD;
import com.superxtra.dao.InventarioDAO;
import com.superxtra.dao.MovimientoInventarioDAO;
import com.superxtra.dao.ProductoDAO;
import com.superxtra.dao.CategoriaDAO;
import com.superxtra.dao.UnidadMedidaDAO;
import com.superxtra.dao.SucursalDAO;
import com.superxtra.model.Categoria;
import com.superxtra.model.UnidadMedida;
import com.superxtra.model.Producto;
import com.superxtra.model.Inventario;
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
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();
    private final UnidadMedidaDAO unidadMedidaDAO = new UnidadMedidaDAO();

    public List<Categoria> listarCategorias() throws SQLException {
        return categoriaDAO.listarActivas();
    }

    public List<UnidadMedida> listarUnidadesMedida() throws SQLException {
        return unidadMedidaDAO.listarActivas();
    }

    public Sucursal buscarSucursalActiva(int idSucursal) throws SQLException {
        return sucursalDAO.buscarActivaPorId(idSucursal);
    }

    public List<Inventario> listarPorSucursal(int idSucursal) throws SQLException {
        return inventarioDAO.listarPorSucursal(idSucursal);
    }

    public List<Producto> listarProductosDisponibles(int idSucursal) throws SQLException {
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

    public void crearProductoYAgregarlo(int idSucursal, int idCategoria, int idUnidadMedida, String codigo,
            String nombre, String descripcion, BigDecimal precio, BigDecimal cantidad,
            BigDecimal stockMinimo, int idUsuario) throws SQLException {
        if (codigo == null || codigo.isBlank() || nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El código y el nombre del producto son obligatorios.");
        }
        validarCantidad(precio, "El precio");
        validarCantidad(cantidad, "La cantidad inicial");
        validarCantidad(stockMinimo, "El stock mínimo");

        try (Connection conexion = ConexionBD.obtenerConexion()) {
            conexion.setAutoCommit(false);
            try {
                if (productoDAO.existeCodigo(conexion, codigo.trim())) {
                    throw new IllegalArgumentException("Ya existe un producto con ese código.");
                }
                if (!productoDAO.categoriaActiva(conexion, idCategoria)) {
                    throw new IllegalArgumentException("La categoría seleccionada no está activa.");
                }
                if (!productoDAO.unidadActiva(conexion, idUnidadMedida)) {
                    throw new IllegalArgumentException("La unidad de medida seleccionada no está activa.");
                }

                int idProducto = productoDAO.insertar(conexion, idCategoria, idUnidadMedida, codigo.trim(), nombre.trim(),
                        descripcion == null || descripcion.isBlank() ? null : descripcion.trim(), precio);
                inventarioDAO.insertar(conexion, idSucursal, idProducto, cantidad, stockMinimo);
                movimientoDAO.insertar(conexion, idSucursal, idProducto, idUsuario, "ALTA", cantidad);
                conexion.commit();
            } catch (Exception excepcion) {
                conexion.rollback();
                if (excepcion instanceof SQLException sqlException) {
                    if ("23000".equals(sqlException.getSQLState())) {
                        throw new IllegalArgumentException("Ya existe un producto con ese código.");
                    }
                    throw sqlException;
                }
                if (excepcion instanceof IllegalArgumentException illegalArgumentException) {
                    throw illegalArgumentException;
                }
                throw new SQLException("No fue posible crear el producto", excepcion);
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
