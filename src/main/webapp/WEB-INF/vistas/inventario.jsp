<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Inventario | Super Xtra</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/app.css">
</head>
<body class="pagina-panel">
    <header class="navbar">
        <a class="marca-navbar" href="${pageContext.request.contextPath}/principal" aria-label="Panel principal Super Xtra">
            <img src="${pageContext.request.contextPath}/assets/img/logo-xtra.png" alt="Xtra">
        </a>
        <div class="usuario-navbar">
            <span>${sessionScope.usuario.nombre}</span>
            <small>${sessionScope.usuario.nombreRol} · ${sucursalInventario.nombre}</small>
        </div>
        <a class="boton-salir" href="${pageContext.request.contextPath}/cerrar-sesion">Cerrar sesión</a>
    </header>

    <main class="contenido-panel">
        <c:choose>
            <c:when test="${puedeCambiarSucursal}">
                <a class="enlace-regreso" href="${pageContext.request.contextPath}/sucursales?id_sucursal=${sucursalInventario.idSucursal}">← Volver a sucursales</a>
            </c:when>
            <c:otherwise>
                <a class="enlace-regreso" href="${pageContext.request.contextPath}/principal">← Volver al panel</a>
            </c:otherwise>
        </c:choose>
        <section class="encabezado-panel encabezado-seccion">
            <p class="etiqueta">Operación de inventario</p>
            <h1>Gestionar inventario</h1>
            <p class="texto-secundario">Administra los productos de ${sucursalInventario.nombre}. Esta versión permite agregarlos y eliminarlos, sin modificarlos.</p>
        </section>

        <c:if test="${not empty mensaje}">
            <p class="mensaje-exito" role="status">${mensaje}</p>
        </c:if>
        <c:if test="${not empty error}">
            <p class="mensaje-error" role="alert">${error}</p>
        </c:if>

        <div class="inventario-operaciones">
            <c:if test="${puedeAgregar}">
                <section class="tarjeta-operacion tarjeta-crear">
                    <p class="etiqueta">Nuevo producto</p>
                    <h2>Crear y agregar</h2>
                    <p>Crea un producto que todavía no exista y agrégalo directamente a esta sucursal.</p>
                    <c:choose>
                        <c:when test="${empty categorias}">
                            <p class="texto-ayuda">No existen categorías activas para crear productos.</p>
                        </c:when>
                        <c:otherwise>
                            <form method="post" action="${pageContext.request.contextPath}/inventario" class="formulario">
                                <input type="hidden" name="accion" value="crear">
                                <input type="hidden" name="id_sucursal" value="${sucursalInventario.idSucursal}">
                                <label for="codigo">Código o SKU</label>
                                <input id="codigo" name="codigo" type="text" maxlength="50" required>
                                <label for="nombre">Nombre</label>
                                <input id="nombre" name="nombre" type="text" maxlength="150" required>
                                <label for="descripcion">Descripción</label>
                                <input id="descripcion" name="descripcion" type="text" maxlength="255">
                                <label for="id_categoria">Categoría</label>
                                <select id="id_categoria" name="id_categoria" required>
                                    <c:forEach var="categoria" items="${categorias}">
                                        <option value="${categoria.idCategoria}">${categoria.nombre}</option>
                                    </c:forEach>
                                </select>
                                <label for="id_unidad_medida">Unidad de medida</label>
                                <select id="id_unidad_medida" name="id_unidad_medida" required>
                                    <c:forEach var="unidad" items="${unidadesMedida}">
                                        <option value="${unidad.idUnidadMedida}">${unidad.nombre} (${unidad.abreviatura})</option>
                                    </c:forEach>
                                </select>
                                <label for="precio">Precio</label>
                                <input id="precio" name="precio" type="number" min="0" step="0.01" required>
                                <label for="cantidad_nueva">Cantidad inicial</label>
                                <input id="cantidad_nueva" name="cantidad" type="number" min="0" step="0.001" value="0" required>
                                <label for="stock_minimo_nuevo">Stock mínimo</label>
                                <input id="stock_minimo_nuevo" name="stock_minimo" type="number" min="0" step="0.001" value="0" required>
                                <button type="submit" class="boton boton-principal">Crear producto</button>
                            </form>
                        </c:otherwise>
                    </c:choose>
                </section>

                <section class="tarjeta-operacion">
                    <p class="etiqueta">Alta</p>
                    <h2>Agregar producto</h2>
                    <p>Asocia un producto activo que todavía no pertenezca a esta sucursal.</p>
                    <c:choose>
                        <c:when test="${empty productosDisponibles}">
                            <p class="texto-ayuda">No hay productos disponibles para agregar.</p>
                        </c:when>
                        <c:otherwise>
                            <form method="post" action="${pageContext.request.contextPath}/inventario" class="formulario">
                                <input type="hidden" name="accion" value="agregar">
                                <input type="hidden" name="id_sucursal" value="${sucursalInventario.idSucursal}">
                                <label for="id_producto">Producto</label>
                                <select id="id_producto" name="id_producto" required>
                                    <c:forEach var="producto" items="${productosDisponibles}">
                                        <option value="${producto.idProducto}">${producto.codigo} · ${producto.nombre}</option>
                                    </c:forEach>
                                </select>
                                <label for="cantidad">Cantidad inicial</label>
                                <input id="cantidad" name="cantidad" type="number" min="0" step="0.001" value="0" required>
                                <label for="stock_minimo">Stock mínimo</label>
                                <input id="stock_minimo" name="stock_minimo" type="number" min="0" step="0.001" value="0" required>
                                <button type="submit" class="boton boton-principal">Agregar al inventario</button>
                            </form>
                        </c:otherwise>
                    </c:choose>
                </section>
            </c:if>

            <section class="tarjeta-operacion">
                <p class="etiqueta">Inventario actual</p>
                <h2>Productos registrados</h2>
                <p>Elimina un producto únicamente de esta sucursal. El producto del catálogo no se elimina.</p>
                <c:if test="${empty productosInventario}">
                    <p class="texto-ayuda">No hay productos registrados en esta sucursal.</p>
                </c:if>
            </section>
        </div>

        <div class="tabla-contenedor">
            <table class="tabla-inventario">
                <caption>Inventario de ${sucursalInventario.nombre}</caption>
                <thead>
                    <tr>
                        <th>Código</th>
                        <th>Producto</th>
                        <th>Existencia</th>
                        <th>Stock mínimo</th>
                        <th>Acción</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="producto" items="${productosInventario}">
                        <tr>
                            <td>${producto.codigo}</td>
                            <td>${producto.nombre}</td>
                            <td>${producto.cantidad}</td>
                            <td>${producto.stockMinimo}</td>
                            <td>
                                <c:if test="${puedeEliminar}">
                                    <form method="post" action="${pageContext.request.contextPath}/inventario">
                                        <input type="hidden" name="accion" value="eliminar">
                                        <input type="hidden" name="id_producto" value="${producto.idProducto}">
                                        <input type="hidden" name="id_sucursal" value="${sucursalInventario.idSucursal}">
                                        <button type="submit" class="boton boton-peligro">Eliminar</button>
                                    </form>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty productosInventario}">
                        <tr><td colspan="5" class="celda-vacia">No hay productos en el inventario.</td></tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </main>
</body>
</html>
