package com.example.hotel.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", "QXN3aW4tMzJieXRlcy1taW4tZGV2LXNlY3JldC0xMjM0NTY3ODkwMTIzNA==");
    }

    @Test
    void testGenerateAndValidateToken() {
        String subject = "test@example.com";
        Map<String, Object> claims = new HashMap<>();
        claims.put("name", "Test User");
        claims.put("roles", "USER");

        String token = jwtUtil.generate(subject, claims);
        assertNotNull(token);

        String extractedSubject = jwtUtil.extractUsername(token);
        assertEquals(subject, extractedSubject);

        assertTrue(jwtUtil.validateToken(token, subject));
    }

    @Test
    void testExtractClaim() {
        String subject = "test@example.com";
        Map<String, Object> claims = new HashMap<>();
        claims.put("name", "Test User");

        String token = jwtUtil.generate(subject, claims);
        String name = jwtUtil.extractClaim(token, claimsMap -> claimsMap.get("name").toString());
        assertEquals("Test User", name);
    }

    @Test
    void testIsTokenExpired() {
        String subject = "test@example.com";
        Map<String, Object> claims = new HashMap<>();

        String token = jwtUtil.generate(subject, claims);
        assertFalse(jwtUtil.isTokenExpired(token));
    }
}
