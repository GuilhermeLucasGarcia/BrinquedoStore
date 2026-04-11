package com.brinquedostore.api.repository;

import com.brinquedostore.api.model.Marca;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarcaRepository extends JpaRepository<Marca, Long> {
    java.util.List<Marca> findByNomeContainingIgnoreCaseOrderByNomeAsc(String nome);
    java.util.List<Marca> findTop8ByNomeContainingIgnoreCaseOrderByNomeAsc(String nome);
}
