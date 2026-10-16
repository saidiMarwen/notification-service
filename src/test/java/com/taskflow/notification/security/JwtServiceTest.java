package com.taskflow.notification.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private static final String SECRET = Base64.getEncoder()
            .encodeToString("0123456789abcdef0123456789abcdef".getBytes());

    private final JwtService jwtService = new JwtService(SECRET);
    private final SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET));

    private String token(String email, long expiresInMs) {
        Date now = new Date();
        return Jwts.builder().subject(email).issuedAt(now)
                .expiration(new Date(now.getTime() + expiresInMs))
                .signWith(key).compact();
    }

    @Test
    void validTokenIsAcceptedAndEmailExtracted() {
        String t = token("bob@test.com", 60_000);
        assertTrue(jwtService.isValid(t));
        assertEquals("bob@test.com", jwtService.extractEmail(t));
    }

    @Test
    void expiredTokenIsRejected() {
        assertFalse(jwtService.isValid(token("bob@test.com", -1000)));
    }

    @Test
    void tamperedOrGarbageTokenIsRejected() {
        assertFalse(jwtService.isValid(token("bob@test.com", 60_000) + "x"));
        assertFalse(jwtService.isValid("abc.def.ghi"));
    }
}