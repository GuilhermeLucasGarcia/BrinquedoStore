package com.brinquedostore.api.service;

import com.brinquedostore.api.model.Brinquedo;
import com.brinquedostore.api.repository.BrinquedoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BrinquedoService {

    private final BrinquedoRepository brinquedoRepository;

    @Autowired
    public BrinquedoService(BrinquedoRepository brinquedoRepository) {
        this.brinquedoRepository = brinquedoRepository;
    }

    public List<Brinquedo> listarTodos() {
        return brinquedoRepository.findAll();
    }

    public Optional<Brinquedo> buscarPorId(Long id) {
        return brinquedoRepository.findById(id);
    }

    public List<Brinquedo> buscarPorCategoria(String categoria) {
        return brinquedoRepository.findByCategoriaNome(categoria);
    }

    public Brinquedo salvar(Brinquedo brinquedo) {
        return brinquedoRepository.save(brinquedo);
    }

    public void deletar(Long id) {
        brinquedoRepository.deleteById(id);
    }
}
