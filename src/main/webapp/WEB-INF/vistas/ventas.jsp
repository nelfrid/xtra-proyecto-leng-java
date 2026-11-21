<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ventas | Super Xtra</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/app.css">
</head>
<body class="pagina-panel">
    <header class="navbar">
        <a class="marca-navbar" href="${pageContext.request.contextPath}/principal" aria-label="Panel principal Super Xtra">
            <img src="${pageContext.request.contextPath}/assets/img/logo-xtra.png" alt="Xtra">
        </a>
        <div class="usuario-navbar">
            <span>${sessionScope.usuario.nombre}</span>
            <small>${sessionScope.usuario.nombreRol} · ${sessionScope.usuario.nombreSucursal}</small>
        </div>
        <a class="boton-salir" href="${pageContext.request.contextPath}/cerrar-sesion">Cerrar sesión</a>
    </header>

    <main class="contenido-panel">
        <a class="enlace-regreso" href="${pageContext.request.contextPath}/principal">← Volver al panel</a>
        <section class="encabezado-panel encabezado-seccion">
            <p class="etiqueta">Operación de ventas</p>
            <h1>Registrar venta</h1>
            <p class="texto-secundario">Vendedor: ${sessionScope.usuario.nombre} · Sucursal: ${sessionScope.usuario.nombreSucursal}</p>
        </section>

        <c:if test="${not empty mensajeVenta}"><p class="mensaje-exito" role="status">${mensajeVenta}</p></c:if>
        <c:if test="${not empty resultadoVenta}">
            <section class="comprobante-venta" aria-label="Resumen de la venta registrada">
                <div><span>Subtotal</span><strong>$ ${resultadoVenta.subtotal}</strong></div>
                <div><span>ITBMS · 10%</span><strong>$ ${resultadoVenta.itbms}</strong></div>
                <div class="comprobante-total"><span>Total final</span><strong>$ ${resultadoVenta.total}</strong></div>
            </section>
        </c:if>
        <c:if test="${not empty error}"><p class="mensaje-error" role="alert">${error}</p></c:if>

        <c:choose>
            <c:when test="${empty productosVenta}">
                <div class="vacio"><h2>Sin productos disponibles</h2><p>No hay productos con existencia en esta sucursal.</p></div>
            </c:when>
            <c:otherwise>
                <form method="post" action="${pageContext.request.contextPath}/ventas" class="formulario formulario-venta">
                    <label class="campo-busqueda" for="buscar-producto">
                        <span>Buscar producto</span>
                        <input id="buscar-producto" type="search" placeholder="Código o nombre" autocomplete="off">
                    </label>
                    <div class="tabla-contenedor">
                        <table class="tabla-inventario">
                            <caption>Selecciona los productos de la venta</caption>
                            <thead><tr><th>Elegir</th><th>Código</th><th>Producto</th><th>Precio</th><th>Disponible</th><th>Cantidad</th></tr></thead>
                            <tbody>
                                <c:forEach var="producto" items="${productosVenta}">
                                    <tr class="fila-producto" data-busqueda="${producto.codigo} ${producto.nombre}">
                                        <td><input type="checkbox" name="id_producto" value="${producto.idProducto}"></td>
                                        <td>${producto.codigo}</td>
                                        <td>${producto.nombre}</td>
                                        <td>$ ${producto.precio}</td>
                                        <td>${producto.existencia}</td>
                                        <td><input name="cantidad_${producto.idProducto}" type="number" min="0.001" step="0.001" value="1"></td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                    <fieldset class="metodos-pago">
                        <legend>Método de pago</legend>
                        <div class="metodos-pago-grid">
                            <c:forEach var="metodo" items="${metodosPago}">
                                <label class="metodo-pago-opcion">
                                    <input type="radio" name="metodo_pago_visual" value="${metodo.idMetodoPago}" data-metodo-pago="${metodo.idMetodoPago}">
                                    <span>${metodo.nombre}</span>
                                </label>
                            </c:forEach>
                        </div>
                    </fieldset>
                    <input id="id_metodo_pago" name="id_metodo_pago" type="hidden" value="">
                    <button type="submit" class="boton boton-principal">Registrar venta</button>
                </form>
            </c:otherwise>
        </c:choose>
    </main>
    <script>
        const busqueda = document.querySelector('#buscar-producto');
        const filas = [...document.querySelectorAll('.fila-producto')];
        busqueda?.addEventListener('input', () => {
            const termino = busqueda.value.trim().toLocaleLowerCase();
            filas.forEach(fila => fila.hidden = !fila.dataset.busqueda.toLocaleLowerCase().includes(termino));
        });
        const selectorPago = document.querySelector('#id_metodo_pago');
        document.querySelectorAll('[data-metodo-pago]').forEach(opcion => opcion.addEventListener('change', () => {
            selectorPago.value = opcion.value;
        }));
        selectorPago?.addEventListener('change', () => {
            document.querySelectorAll('[data-metodo-pago]').forEach(opcion => opcion.checked = opcion.value === selectorPago.value);
        });
    </script>
</body>
</html>
