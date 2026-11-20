package com.superxtra.servicio;

import com.superxtra.dao.InventarioDAO;
import com.superxtra.dao.SucursalDAO;
import com.superxtra.model.ProductoInventario;
import com.superxtra.model.Sucursal;
import java.sql.SQLException;
import java.util.List;

/**
 * Reglas de consulta de sucursales e inventario.
 */
public final class ServicioSucursales {

    private final SucursalDAO sucursalDAO;
    private final InventarioDAO inventarioDAO;

    public ServicioSucursales() {
        this.sucursalDAO = new SucursalDAO();
        this.inventarioDAO = new InventarioDAO();
    }

    public List<Sucursal> listarSucursalesActivas() throws SQLException {
        return sucursalDAO.listarActivas();
    }

    public Sucursal buscarSucursalActiva(int idSucursal) throws SQLException {
        return sucursalDAO.buscarActivaPorId(idSucursal);
    }

    public List<ProductoInventario> listarProductosPorSucursal(int idSucursal) throws SQLException {
        return inventarioDAO.listarPorSucursal(idSucursal);
    }
}
