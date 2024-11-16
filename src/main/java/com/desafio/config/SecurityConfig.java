package com.desafio.config;

import com.desafio.service.imp.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Security configuration for the application, configuring CORS, CSRF, session management,
 * and access control for specific endpoints including H2 console, Swagger, and Sonar.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    /**
     * Configures the HTTP security for the application, allowing access to specific
     * paths (H2 console, Swagger, and Sonar) and requiring authentication for all other requests.
     * 
     * @param http The HttpSecurity object to configure security for HTTP requests.
     * @return A configured SecurityFilterChain instance.
     * @throws Exception If an error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors().and()
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authorize -> authorize
                // Permit access to H2 Console, Sonar, and Swagger
                .requestMatchers(
                    "/api/signin", 
                    "/api/signup", 
                    "/h2-console/**", 
                    "/sonar/**", 
                    "/swagger-ui/**", 
                    "/v3/api-docs/**", 
                    "/swagger-ui.html", 
                    "/webjars/**"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    /**
     * Configures the password encoder for the application.
     * Uses BCryptPasswordEncoder as the implementation.
     * 
     * @return A PasswordEncoder instance using BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Exposes the AuthenticationManager as a bean, allowing it to be injected and used
     * for authentication purposes in other parts of the application.
     * 
     * @param authenticationConfiguration The AuthenticationConfiguration instance used to build the AuthenticationManager.
     * @return A configured AuthenticationManager instance.
     * @throws Exception If an error occurs during the configuration.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Configures CORS (Cross-Origin Resource Sharing) for the application.
     * Defines allowed origins, methods, headers, and whether credentials are allowed.
     * 
     * @return A CorsConfigurationSource instance for configuring CORS settings.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200")); // Define the allowed origin
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
