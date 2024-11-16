package com.desafio.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.function.Function;

/**
 * Utilitário para geração e validação de tokens JWT.
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    /**
     * Extrai o username do token JWT.
     * 
     * @param token O token JWT.
     * @return O username contido no token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrai uma informação específica (claim) do token JWT.
     * 
     * @param token O token JWT.
     * @param claimsResolver Função para resolver a claim.
     * @return A informação extraída.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    /**
     * Gera um token JWT para o usuário.
     * 
     * @param username O username do usuário.
     * @return O token JWT gerado.
     */
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 horas
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    /**
     * Valida o token JWT.
     * 
     * @param token O token JWT.
     * @param username O username do usuário.
     * @return true se o token for válido, false caso contrário.
     */
    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extrai a data de expiração do token JWT.
     * 
     * @param token O token JWT.
     * @return A data de expiração.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
