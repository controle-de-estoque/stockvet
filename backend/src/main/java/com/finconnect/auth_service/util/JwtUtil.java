package com.finconnect.auth_service.util;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.annotation.PostConstruct;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String username) {
        return Jwts.builder()
                    .setSubject(username)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(new Date().getTime() + jwtExpirationMs))
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                    .setSigningKey(key).build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
    }

    public boolean validateJwtToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException e) {
            logger.error("Invalid JWT signature: " + e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: " + e.getMessage());
        }

        return false;
    }
}
