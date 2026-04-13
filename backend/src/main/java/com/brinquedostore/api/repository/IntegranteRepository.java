package com.brinquedostore.api.repository;

import com.brinquedostore.api.model.Integrante;
import com.brinquedostore.api.model.PerfilUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IntegranteRepository extends JpaRepository<Integrante, Long> {
    List<Integrante> findAllByOrderByNomeAsc();
    List<Integrante> findAllByPerfilOrderByNomeAsc(PerfilUsuario perfil);
    List<Integrante> findByNomeContainingIgnoreCaseOrNomeUsuarioContainingIgnoreCaseOrderByNomeAsc(String nome, String nomeUsuario);
    List<Integrante> findTop8ByNomeContainingIgnoreCaseOrNomeUsuarioContainingIgnoreCaseOrderByNomeAsc(String nome, String nomeUsuario);
    Optional<Integrante> findByNomeUsuarioIgnoreCase(String nomeUsuario);
    Optional<Integrante> findByEmailIgnoreCase(String email);
    boolean existsByNomeUsuarioIgnoreCase(String nomeUsuario);
    boolean existsByNomeUsuarioIgnoreCaseAndIdNot(String nomeUsuario, Long id);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
}
