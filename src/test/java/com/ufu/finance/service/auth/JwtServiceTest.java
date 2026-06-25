package com.ufu.finance.service.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() throws Exception {
        jwtService = new JwtService();
        setPrivateField(jwtService, "secret", "01234567890123456789012345678901");
        setPrivateField(jwtService, "expiration", 3600000L);
    }

    private static void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    void generateTokenProducesNonNullToken() {
        String token = jwtService.generateToken(1L, "Teste");

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void extractUserIdAndNomeReturnExpectedClaims() {
        String token = jwtService.generateToken(10L, "NomeTeste");

        assertEquals(10L, jwtService.extractUserId(token));
        assertEquals("NomeTeste", jwtService.extractNome(token));
    }

    @Test
    void validateTokenReturnsTrueForValidToken() {
        String token = jwtService.generateToken(5L, "Usuario");

        assertTrue(jwtService.validateToken(token));
        assertFalse(jwtService.isTokenExpired(token));
    }

    @Test
    void isTokenExpiredReturnsTrueForExpiredToken() throws Exception {
        JwtService expiredService = new JwtService();
        setPrivateField(expiredService, "secret", "01234567890123456789012345678901");
        setPrivateField(expiredService, "expiration", -1000L);

        String token = expiredService.generateToken(2L, "Antigo");

        assertTrue(expiredService.isTokenExpired(token));
        assertFalse(expiredService.validateToken(token));
    }

    @Test
    void validateTokenReturnsFalseForMalformedToken() {
        assertFalse(jwtService.validateToken("invalid.token.value"));
    }
}
