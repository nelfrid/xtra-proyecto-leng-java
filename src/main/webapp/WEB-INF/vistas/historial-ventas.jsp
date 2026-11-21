<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0"><title>Historial de ventas | Super Xtra</title><link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/app.css"></head>
<body class="pagina-panel">
    <header class="navbar"><a class="marca-navbar" href="${pageContext.request.contextPath}/principal"><img src="${pageContext.request.contextPath}/assets/img/logo-xtra.png" alt="Xtra"></a><div class="usuario-navbar"><span>${sessionScope.usuario.nombre}</span><small>${sessionScope.usuario.nombreRol}</small></div><a class="boton-salir" href="${pageContext.request.contextPath}/cerrar-sesion">Cerrar sesión</a></header>
    <main class="contenido-panel">
        <a class="enlace-regreso" href="${pageContext.request.contextPath}/principal">← Volver al panel</a>
        <section class="encabezado-panel encabezado-seccion"><p class="etiqueta">Ventas</p><h1>Historial de ventas</h1><p class="texto-secundario">Consulta todas las operaciones registradas en el sistema.</p></section>
        <c:if test="${not empty error}"><p class="mensaje-error" role="alert">${error}</p></c:if>
        <div class="tabla-contenedor">
            <table class="tabla-inventario"><caption>Operaciones registradas</caption><thead><tr><th>Venta</th><th>Fecha</th><th>Sucursal</th><th>Vendedor</th><th>Método de pago</th><th>Total</th><th>Estado</th></tr></thead>
                <tbody><c:forEach var="venta" items="${ventas}"><tr><td>#${venta.idVenta}</td><td>${venta.fecha}</td><td>${venta.sucursal}</td><td>${venta.vendedor}</td><td>${venta.metodoPago}</td><td>$ ${venta.total}</td><td><span class="estado-venta">${venta.estado}</span></td></tr></c:forEach><c:if test="${empty ventas}"><tr><td colspan="7" class="celda-vacia">Todavía no hay ventas registradas.</td></tr></c:if></tbody>
            </table>
        </div>
    </main>
</body>
</html>
