package com.brinquedostore.api.service;

import com.brinquedostore.api.repository.IntegranteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class IntegranteServiceTest {

    private IntegranteService integranteService;

    @BeforeEach
    void setUp() {
        integranteService = new IntegranteService(mock(IntegranteRepository.class), new BCryptPasswordEncoder());
    }

    @Test
    void deveAceitarSenhaForte() {
        assertTrue(integranteService.senhaAtendePolitica("Senha@123"));
    }

    @Test
    void deveRejeitarSenhaSemCaracterEspecial() {
        assertFalse(integranteService.senhaAtendePolitica("Senha123"));
    }
}
