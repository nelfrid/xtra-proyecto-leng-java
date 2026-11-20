<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Iniciar sesión | Super Xtra</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/app.css">
</head>
<body class="pagina-acceso">
    <main class="acceso">
        <section class="tarjeta-acceso" aria-labelledby="titulo-acceso">
            <div class="marca-acceso">
                <img src="${pageContext.request.contextPath}/assets/img/logo-xtra.png" alt="Super Xtra">
            </div>
            <p class="etiqueta">Panel operativo</p>
            <h1 id="titulo-acceso">Inicia sesión</h1>
            <p class="texto-secundario">Accede a la operación de tu sucursal.</p>

            <% if (request.getAttribute("error") != null) { %>
                <p class="mensaje-error" role="alert"><%= request.getAttribute("error") %></p>
            <% } %>

            <form method="post" action="${pageContext.request.contextPath}/login" class="formulario">
                <label for="correo">Correo</label>
                <input id="correo" name="correo" type="email" autocomplete="username" required autofocus>

                <label for="contrasena">Contraseña</label>
                <input id="contrasena" name="contrasena" type="password" autocomplete="current-password" required>

                <button type="submit" class="boton boton-principal">Entrar al panel</button>
            </form>

            <p class="nota-acceso">Usa las credenciales asignadas por el administrador.</p>
        </section>
    </main>
</body>
</html>
