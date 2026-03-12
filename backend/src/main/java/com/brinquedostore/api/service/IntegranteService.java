package com.brinquedostore.api.service;

import com.brinquedostore.api.model.Integrante;
import com.brinquedostore.api.repository.IntegranteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IntegranteService {
    private final IntegranteRepository integranteRepository;

    public IntegranteService(IntegranteRepository integranteRepository) {
        this.integranteRepository = integranteRepository;
    }

    public List<Integrante> listarTodos() {
        return integranteRepository.findAll();
    }
}
