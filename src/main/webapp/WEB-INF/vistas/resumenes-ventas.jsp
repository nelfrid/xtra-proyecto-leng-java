<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0"><title>Resumen de ventas | Super Xtra</title><link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/app.css"></head>
<body class="pagina-panel">
    <header class="navbar"><a class="marca-navbar" href="${pageContext.request.contextPath}/principal"><img src="${pageContext.request.contextPath}/assets/img/logo-xtra.png" alt="Xtra"></a><div class="usuario-navbar"><span>${sessionScope.usuario.nombre}</span><small>${sessionScope.usuario.nombreRol}</small></div><a class="boton-salir" href="${pageContext.request.contextPath}/cerrar-sesion">Cerrar sesión</a></header>
    <main class="contenido-panel">
        <a class="enlace-regreso" href="${pageContext.request.contextPath}/principal">← Volver al panel</a>
        <section class="encabezado-panel encabezado-seccion"><p class="etiqueta">Analítica</p><h1>Resumen de ventas</h1><p class="texto-secundario">Una lectura rápida del comportamiento de las ventas registradas.</p></section>
        <c:if test="${not empty error}"><p class="mensaje-error" role="alert">${error}</p></c:if>
        <section class="metricas-ventas"><article class="metrica-venta"><span>Ventas registradas</span><strong>${resumen.cantidadVentas}</strong></article><article class="metrica-venta metrica-destacada"><span>Total vendido</span><strong>$ ${resumen.totalVendido}</strong></article></section>
        <div class="tabla-contenedor"><table class="tabla-inventario"><caption>Ventas por método de pago</caption><thead><tr><th>Método</th><th>Cantidad de ventas</th><th>Total vendido</th></tr></thead><tbody><c:forEach var="fila" items="${resumenMetodos}"><tr><td>${fila.metodoPago}</td><td>${fila.cantidadVentas}</td><td>$ ${fila.totalVendido}</td></tr></c:forEach></tbody></table></div>
    </main>
</body>
</html>
