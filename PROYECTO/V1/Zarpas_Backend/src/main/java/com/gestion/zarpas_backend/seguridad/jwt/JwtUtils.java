package com.gestion.zarpas_backend.seguridad.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expirationMs}")
    private int jwtExpirationMs;

    public String generateJwtToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key key() {
        System.out.println("JwtUtils: Usando jwtSecret para la clave: " + jwtSecret);
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        System.out.println("JwtUtils: --- Iniciando validación de token JWT ---");
        System.out.println("JwtUtils: Token recibido para validar: " + authToken.substring(0, Math.min(authToken.length(), 30)) + "...");
        try {
            Jwts.parser().setSigningKey(key()).build().parse(authToken);
            logger.debug("Token JWT validado exitosamente.");
            System.out.println("JwtUtils: Token JWT validado exitosamente. Retornando true.");
            return true;
        } catch (SignatureException e) {
            logger.error("Firma JWT inválida: {}", e.getMessage());
            System.out.println("JwtUtils: ERROR - Firma JWT inválida: " + e.getMessage());
            e.printStackTrace();
        } catch (MalformedJwtException e) {
            logger.error("Token JWT no válido: {}", e.getMessage());
            System.out.println("JwtUtils: ERROR - Token JWT no válido (mal formado): " + e.getMessage());
            e.printStackTrace();
        } catch (ExpiredJwtException e) {
            logger.error("Token JWT ha expirado: {}", e.getMessage());
            System.out.println("JwtUtils: ERROR - Token JWT ha expirado: " + e.getMessage());
            e.printStackTrace();
        } catch (UnsupportedJwtException e) {
            logger.error("Token JWT no soportado: {}", e.getMessage());
            System.out.println("JwtUtils: ERROR - Token JWT no soportado: " + e.getMessage());
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            logger.error("Cadena JWT vacía o argumento ilegal: {}", e.getMessage());
            System.out.println("JwtUtils: ERROR - Cadena JWT vacía o argumento ilegal: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            logger.error("JwtUtils: ERROR inesperado durante la validación del token: {}", e.getMessage());
            System.out.println("JwtUtils: ERROR inesperado: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("JwtUtils: Validación de token FALLIDA. Retornando false.");
        return false;
    }
}