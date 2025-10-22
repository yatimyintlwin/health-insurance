package com.insurance.health.utils;

import com.insurance.health.model.AppUser;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMillis;

    private Key getSigningKey() {
        log.trace("Retrieving signing key for JWT");
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateToken(AppUser user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtExpirationMillis);

        log.debug("Generating JWT for email: {}", user.getEmail());
        log.trace("Token expiration set to: {}", expiry);

        String token = Jwts.builder()
                .setSubject(user.getId())
                .claim("email", user.getEmail())
                .claim("role", user.getRole())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

        log.info("JWT token generated successfully for email: {}", user.getEmail());
        return token;
    }

    public String extractUserId(String token) {
        String userId = getClaims(token).getSubject();
        log.debug("Extracted username from token: {}", userId);
        return userId;
    }

    public boolean isTokenExpired(String token) {
        try {
            boolean expired = getClaims(token).getExpiration().before(new Date());
            log.debug("Token expired: {}", expired);
            return expired;
        } catch (JwtException e) {
            log.error("Could not check token expiration: {}", e.getMessage());
            return true;
        }
    }

    private Claims getClaims(String token) {
        log.trace("Parsing JWT token to extract claims");
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
