<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Panel principal | Super Xtra</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/app.css">
</head>
<body class="pagina-panel">
    <header class="navbar">
        <a class="marca-navbar" href="${pageContext.request.contextPath}/principal" aria-label="Panel principal Super Xtra">
            <img src="${pageContext.request.contextPath}/assets/img/logo-xtra.png" alt="Xtra">
        </a>
        <div class="usuario-navbar">
            <span>${usuario.nombre}</span>
            <small>${usuario.nombreRol}</small>
        </div>
        <a class="boton-salir" href="${pageContext.request.contextPath}/cerrar-sesion">Cerrar sesión</a>
    </header>

    <main class="contenido-panel">
        <section class="encabezado-panel">
            <p class="etiqueta">Panel principal</p>
            <h1>¡Bienvenido, ${usuario.nombre}!</h1>
            <p class="sucursal-usuario">${usuario.nombreSucursal}</p>
            <p class="texto-secundario">Estas son las operaciones disponibles para tu rol.</p>
        </section>

        <section class="modulos" aria-label="Módulos disponibles">
            <c:if test="${puedeGestionarSucursales}">
                <a class="modulo modulo-enlace" href="${pageContext.request.contextPath}/sucursales">
                    <span class="modulo-numero">01</span>
                    <h2>Sucursales</h2>
                    <p>Administra la información y el estado de las sucursales.</p>
                    <span class="modulo-estado">Disponible</span>
                </a>
            </c:if>

            <c:if test="${puedeGestionarInventario}">
                <a class="modulo modulo-enlace" href="${pageContext.request.contextPath}/inventario">
                    <span class="modulo-numero">02</span>
                    <h2>Gestionar inventario</h2>
                    <p>Agrega productos existentes o elimínalos del inventario de tu sucursal.</p>
                    <span class="modulo-estado">Disponible</span>
                </a>
            </c:if>

            <c:if test="${puedeRealizarVentas}">
                <article class="modulo modulo-destacado">
                    <span class="modulo-numero">03</span>
                    <h2>Ventas</h2>
                    <p>Registra ventas y consulta el estado de la operación.</p>
                    <span class="modulo-estado">Disponible</span>
                </article>
            </c:if>

            <c:if test="${puedeVerResumenes}">
                <article class="modulo">
                    <span class="modulo-numero">04</span>
                    <h2>Resúmenes</h2>
                    <p>Consulta el comportamiento de las ventas por periodo.</p>
                    <span class="modulo-estado">Disponible</span>
                </article>
            </c:if>
        </section>
    </main>
</body>
</html>
