<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sucursales | Super Xtra</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/app.css">
</head>
<body class="pagina-panel">
    <header class="navbar">
        <a class="marca-navbar" href="${pageContext.request.contextPath}/principal" aria-label="Panel principal Super Xtra">
            <img src="${pageContext.request.contextPath}/assets/img/logo-xtra.png" alt="Xtra">
        </a>
        <div class="usuario-navbar">
            <span>${sessionScope.usuario.nombre}</span>
            <small>${sessionScope.usuario.nombreRol}</small>
        </div>
        <a class="boton-salir" href="${pageContext.request.contextPath}/cerrar-sesion">Cerrar sesión</a>
    </header>

    <main class="contenido-panel">
        <a class="enlace-regreso" href="${pageContext.request.contextPath}/principal">← Volver al panel</a>
        <section class="encabezado-panel encabezado-seccion">
            <p class="etiqueta">Administración</p>
            <h1>Gestionar sucursales</h1>
            <p class="texto-secundario">Selecciona una sucursal para consultar únicamente sus productos y existencias.</p>
        </section>

        <c:if test="${not empty error}">
            <p class="mensaje-error" role="alert">${error}</p>
        </c:if>

        <form class="selector-sucursal" method="get" action="${pageContext.request.contextPath}/sucursales">
            <label for="id_sucursal">Sucursal a consultar</label>
            <div class="selector-fila">
                <select id="id_sucursal" name="id_sucursal" required>
                    <c:forEach var="sucursal" items="${sucursales}">
                        <option value="${sucursal.idSucursal}"
                            ${sucursalSeleccionada != null && sucursal.idSucursal == sucursalSeleccionada.idSucursal ? 'selected' : ''}>
                            ${sucursal.nombre}
                        </option>
                    </c:forEach>
                </select>
                <button type="submit" class="boton boton-principal">Consultar sucursal</button>
            </div>
        </form>

        <c:if test="${not empty sucursalSeleccionada}">
            <section class="resumen-sucursal">
                <div>
                    <p class="etiqueta">Sucursal seleccionada</p>
                    <h2>${sucursalSeleccionada.nombre}</h2>
                </div>
                <div class="resumen-acciones">
                    <p>${sucursalSeleccionada.direccion}</p>
                    <a class="boton boton-principal" href="${pageContext.request.contextPath}/inventario?id_sucursal=${sucursalSeleccionada.idSucursal}">
                        Gestionar inventario
                    </a>
                </div>
            </section>

            <c:choose>
                <c:when test="${empty productosInventario}">
                    <div class="vacio">
                        <h2>Sin productos registrados</h2>
                        <p>Esta sucursal todavía no tiene productos en su inventario.</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="tabla-contenedor">
                        <table class="tabla-inventario">
                            <caption>Productos de ${sucursalSeleccionada.nombre}</caption>
                            <thead>
                                <tr>
                                    <th>Código</th>
                                    <th>Producto</th>
                                    <th>Precio</th>
                                    <th>Existencia</th>
                                    <th>Stock mínimo</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="producto" items="${productosInventario}">
                                    <tr>
                                        <td>${producto.codigo}</td>
                                        <td>${producto.nombre}</td>
                                        <td>$ ${producto.precio}</td>
                                        <td>${producto.cantidad}</td>
                                        <td>${producto.stockMinimo}</td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>
        </c:if>
    </main>
</body>
</html>
