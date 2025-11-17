package com.example.hotel.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    private static final long EXP_MS = 1000L * 60 * 60 * 6; // 6 hours
    private final SecretKey key;

    // First try property `jwt.secret`, else env var JWT_SECRET_KEY
    public JwtUtil(@Value("${jwt.secret:${JWT_SECRET_KEY:}}") String secretBase64) {
        if (secretBase64 == null || secretBase64.isBlank()) {
            throw new IllegalStateException("JWT secret key is not set. Provide jwt.secret or JWT_SECRET_KEY (Base64).");
        }
        byte[] decoded = Decoders.BASE64.decode(secretBase64.trim());
        if (decoded.length < 32) { // 256-bit minimum for HS256
            throw new IllegalStateException("JWT secret key is too short. Provide at least 32 bytes (256-bit) Base64.");
        }
        this.key = Keys.hmacShaKeyFor(decoded);
    }

    public String generate(String subject, Map<String, Object> claims) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(subject)
                .addClaims(claims)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + EXP_MS))
                .signWith(key, SignatureAlgorithm.HS256)   // 0.11.x style
                .compact();
    }

    public Jws<Claims> parse(String token) {
        return Jwts.parserBuilder()                       // 0.11.x style
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }
}
