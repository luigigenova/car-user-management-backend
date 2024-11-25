package com.desafio.filters;

import com.desafio.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Filtro para autenticação de requisições via JWT.
 * Gerencia a autenticação automática com base no token JWT e permite acesso
 * público para rotas específicas.
 */
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    /**
     * Lista de rotas públicas que não exigem autenticação.
     */
    private static final List<String> PUBLIC_ROUTES = List.of(
            "/api/signin",
            "/api/signup",
            "/api/users",
            "/api/users/available-cars",
            "/api/users/{userId}/add-cars",
            "/api/users/{userId}/remove-car/{carId}",
            "/api/users/{id}",
            "/h2-console",
            "/swagger-ui",
            "/v3/api-docs");

    public JwtRequestFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        boolean isPublicRoute = PUBLIC_ROUTES.stream().anyMatch(path::equals);
        boolean isOptionsMethod = "OPTIONS".equalsIgnoreCase(request.getMethod());
        return isPublicRoute || isOptionsMethod;    
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);

            try {
                String username = jwtUtil.extractUsername(token);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    if (jwtUtil.validateToken(token, username)) {
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    } else {
                        System.err.println("Token inválido: " + token);
                    }
                }
            } catch (Exception e) {
                System.err.println("Erro ao processar o token: " + e.getMessage());
            }
        }

        chain.doFilter(request, response);
    }
}
