package com.brinquedostore.api.repository;

import com.brinquedostore.api.model.Brinquedo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrinquedoRepository extends JpaRepository<Brinquedo, Long> {
    List<Brinquedo> findByCategoriaNome(String categoria);
}
