package com.brinquedostore.api.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegistrationRateLimiterTest {

    @Test
    void devePermitirAteCincoTentativasNaJanela() {
        RegistrationRateLimiter limiter = new RegistrationRateLimiter();

        assertTrue(limiter.tryAcquire("127.0.0.1"));
        assertTrue(limiter.tryAcquire("127.0.0.1"));
        assertTrue(limiter.tryAcquire("127.0.0.1"));
        assertTrue(limiter.tryAcquire("127.0.0.1"));
        assertTrue(limiter.tryAcquire("127.0.0.1"));
        assertFalse(limiter.tryAcquire("127.0.0.1"));
    }
}
