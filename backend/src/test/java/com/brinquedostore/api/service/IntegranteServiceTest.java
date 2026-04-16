package com.brinquedostore.api.service;

import com.brinquedostore.api.model.Integrante;
import com.brinquedostore.api.repository.IntegranteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class IntegranteServiceTest {

    private IntegranteService integranteService;
    private IntegranteRepository integranteRepository;
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        integranteRepository = mock(IntegranteRepository.class);
        passwordEncoder = new BCryptPasswordEncoder();
        integranteService = new IntegranteService(integranteRepository, passwordEncoder);
    }

    @Test
    void deveAceitarSenhaForte() {
        assertTrue(integranteService.senhaAtendePolitica("Senha@123"));
    }

    @Test
    void deveRejeitarSenhaSemCaracterEspecial() {
        assertFalse(integranteService.senhaAtendePolitica("Senha123"));
    }

    @Test
    void deveCriarTokenQuandoEmailExistir() {
        Integrante integrante = new Integrante();
        integrante.setEmail("maria@exemplo.com");
        when(integranteRepository.findByEmailIgnoreCase("maria@exemplo.com")).thenReturn(Optional.of(integrante));
        when(integranteRepository.save(any(Integrante.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String token = integranteService.criarTokenRedefinicaoSenha("maria@exemplo.com");

        assertTrue(token != null && !token.isEmpty());
        assertTrue(integrante.getResetTokenHash() != null && !integrante.getResetTokenHash().isEmpty());
        assertTrue(integrante.getResetTokenExpiraEm() != null);
    }

    @Test
    void deveRejeitarResetQuandoEmailNaoExistir() {
        when(integranteRepository.findByEmailIgnoreCase("maria@exemplo.com")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> integranteService.criarTokenRedefinicaoSenha("maria@exemplo.com"));
    }

    @Test
    void deveRejeitarTokenExpirado() {
        Integrante integrante = new Integrante();
        String token = "token-expirado";
        integrante.setResetTokenHash(integranteService.gerarHashToken(token));
        integrante.setResetTokenExpiraEm(LocalDateTime.now().minusMinutes(1));
        when(integranteRepository.findByResetTokenHash(integrante.getResetTokenHash())).thenReturn(Optional.of(integrante));

        assertThrows(IllegalArgumentException.class,
                () -> integranteService.validarTokenRedefinicao(token));
    }

    @Test
    void deveBloquearReusoDasUltimasTresSenhas() {
        Integrante integrante = new Integrante();
        integrante.setSenha(passwordEncoder.encode("Atual@123"));
        integrante.setSenhaAnterior1(passwordEncoder.encode("Senha1@123"));
        integrante.setSenhaAnterior2(passwordEncoder.encode("Senha2@123"));
        integrante.setSenhaAnterior3(passwordEncoder.encode("Senha3@123"));
        String token = "token-reuso";
        integrante.setResetTokenHash(integranteService.gerarHashToken(token));
        integrante.setResetTokenExpiraEm(LocalDateTime.now().plusMinutes(30));
        when(integranteRepository.findByResetTokenHash(integrante.getResetTokenHash())).thenReturn(Optional.of(integrante));

        assertThrows(IllegalArgumentException.class,
                () -> integranteService.redefinirSenha(token, "Senha2@123", "Senha2@123"));
    }

    @Test
    void deveRedefinirSenhaQuandoTokenForValidoESenhaNova() {
        Integrante integrante = new Integrante();
        integrante.setSenha(passwordEncoder.encode("Atual@123"));
        String token = "token-ok";
        integrante.setResetTokenHash(integranteService.gerarHashToken(token));
        integrante.setResetTokenExpiraEm(LocalDateTime.now().plusMinutes(30));
        when(integranteRepository.findByResetTokenHash(integrante.getResetTokenHash())).thenReturn(Optional.of(integrante));
        when(integranteRepository.save(any(Integrante.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertDoesNotThrow(() -> integranteService.redefinirSenha(token, "Nova@123", "Nova@123"));
        assertTrue(passwordEncoder.matches("Nova@123", integrante.getSenha()));
        assertTrue(integrante.getResetTokenHash() == null);
    }
}
