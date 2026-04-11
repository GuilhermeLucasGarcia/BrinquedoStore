package com.brinquedostore.api.repository;

import com.brinquedostore.api.model.Brinquedo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrinquedoRepository extends JpaRepository<Brinquedo, Long> {
    List<Brinquedo> findByNomeContainingIgnoreCaseOrderByNomeAsc(String nome);
    List<Brinquedo> findTop8ByNomeContainingIgnoreCaseOrderByNomeAsc(String nome);
    List<Brinquedo> findByCategoriaNomeIgnoreCase(String categoria);
    boolean existsByCategoriaId(Long categoriaId);
}
