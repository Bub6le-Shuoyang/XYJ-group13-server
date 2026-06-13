package com.xyj.xyjserver.common.util;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    @Test
    void generateToken_shouldContainUserIdAndRole() {
        String token = JwtUtil.generateToken(42L, "USER");

        assertTrue(JwtUtil.validateToken(token));
        assertEquals(42L, JwtUtil.getUserId(token));

        Claims claims = JwtUtil.parseToken(token);
        assertEquals("42", claims.getSubject());
        assertEquals("USER", claims.get("role", String.class));
        assertNull(claims.get("type"));
    }

    @Test
    void generateRefreshToken_shouldContainRefreshType() {
        String refreshToken = JwtUtil.generateRefreshToken(99L, "ADMIN");

        assertTrue(JwtUtil.validateToken(refreshToken));

        Claims claims = JwtUtil.parseToken(refreshToken);
        assertEquals("99", claims.getSubject());
        assertEquals("ADMIN", claims.get("role", String.class));
        assertEquals("refresh", claims.get("type", String.class));
    }

    @Test
    void validateToken_shouldReturnFalseForInvalidToken() {
        assertFalse(JwtUtil.validateToken("invalid.token.value"));
        assertFalse(JwtUtil.validateToken(""));
        assertNull(JwtUtil.getUserId("not-a-jwt"));
    }

    @Test
    void expirationTime_shouldBeTwoHoursInSeconds() {
        assertEquals(7200L, JwtUtil.EXPIRATION_TIME / 1000);
    }
}
