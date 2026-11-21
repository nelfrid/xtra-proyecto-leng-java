package com.superxtra.servicio;

import com.superxtra.dao.ReporteVentasDAO;
import com.superxtra.model.ResumenMetodoPago;
import com.superxtra.model.ResumenVentas;
import com.superxtra.model.VentaHistorial;
import java.sql.SQLException;
import java.util.List;

public final class ServicioReportesVentas {
    private final ReporteVentasDAO reporteDAO = new ReporteVentasDAO();

    public List<VentaHistorial> listarHistorial() throws SQLException {
        return reporteDAO.listarHistorial();
    }

    public ResumenVentas obtenerResumen() throws SQLException {
        return reporteDAO.obtenerResumen();
    }

    public List<ResumenMetodoPago> resumirPorMetodoPago() throws SQLException {
        return reporteDAO.resumirPorMetodoPago();
    }
}
