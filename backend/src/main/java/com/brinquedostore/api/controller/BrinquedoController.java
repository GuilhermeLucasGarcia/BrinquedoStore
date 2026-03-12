package com.brinquedostore.api.controller;

import com.brinquedostore.api.model.Brinquedo;
import com.brinquedostore.api.service.BrinquedoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brinquedos")
@CrossOrigin(origins = "*")
public class BrinquedoController {

    private final BrinquedoService brinquedoService;

    @Autowired
    public BrinquedoController(BrinquedoService brinquedoService) {
        this.brinquedoService = brinquedoService;
    }

    @GetMapping
    public List<Brinquedo> listarTodos() {
        return brinquedoService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Brinquedo> buscarPorId(@PathVariable Long id) {
        return brinquedoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/categoria/{categoria}")
    public List<Brinquedo> buscarPorCategoria(@PathVariable String categoria) {
        return brinquedoService.buscarPorCategoria(categoria);
    }

    @PostMapping
    public Brinquedo salvar(@RequestBody Brinquedo brinquedo) {
        return brinquedoService.salvar(brinquedo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        brinquedoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
