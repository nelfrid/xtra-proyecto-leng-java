package com.superxtra.filtro;

import com.superxtra.model.Usuario;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/*")
public final class AutenticacionFiltro implements Filter {

    @Override
    public void doFilter(ServletRequest solicitud, ServletResponse respuesta, FilterChain cadena)
            throws IOException, ServletException {
        HttpServletRequest solicitudHttp = (HttpServletRequest) solicitud;
        HttpServletResponse respuestaHttp = (HttpServletResponse) respuesta;
        String ruta = solicitudHttp.getRequestURI().substring(solicitudHttp.getContextPath().length());

        if (ruta.equals("/login") || ruta.equals("/") || ruta.startsWith("/assets/")) {
            cadena.doFilter(solicitud, respuesta);
            return;
        }

        Usuario usuario = solicitudHttp.getSession(false) == null
                ? null
                : (Usuario) solicitudHttp.getSession(false).getAttribute("usuario");

        if (usuario == null) {
            respuestaHttp.sendRedirect(solicitudHttp.getContextPath() + "/login");
            return;
        }

        cadena.doFilter(solicitud, respuesta);
    }
}
