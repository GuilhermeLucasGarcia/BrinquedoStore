package com.brinquedostore.api.repository;

import com.brinquedostore.api.model.Categoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    @Query("SELECT c FROM Categoria c " +
            "WHERE (:nome IS NULL OR LOWER(c.nome) LIKE LOWER(CONCAT('%', :nome, '%'))) " +
            "AND (:ativo IS NULL OR c.ativo = :ativo)")
    Page<Categoria> buscarComFiltros(@Param("nome") String nome,
                                     @Param("ativo") Boolean ativo,
                                     Pageable pageable);

    List<Categoria> findAllByOrderByNomeAsc();

    List<Categoria> findByAtivoTrueOrderByNomeAsc();

    List<Categoria> findTop8ByAtivoTrueAndNomeContainingIgnoreCaseOrderByNomeAsc(String nome);

    List<Categoria> findTop8ByNomeContainingIgnoreCaseOrderByNomeAsc(String nome);

    Optional<Categoria> findByNomeIgnoreCase(String nome);

    boolean existsByNomeIgnoreCase(String nome);

    boolean existsByNomeIgnoreCaseAndIdNot(String nome, Long id);
}
