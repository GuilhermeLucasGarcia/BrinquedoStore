package com.brinquedostore.api.service;

import com.brinquedostore.api.model.Brinquedo;
import com.brinquedostore.api.model.CarrinhoItem;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.ArrayList;
import java.util.List;

@Service
@SessionScope
public class CarrinhoService {

    private final List<CarrinhoItem> itens = new ArrayList<>();

    public void adicionar(Brinquedo brinquedo, Integer quantidade) {
        for (CarrinhoItem item : itens) {
            if (item.getId().equals(brinquedo.getId())) {
                item.setQuantidade(item.getQuantidade() + quantidade);
                return;
            }
        }
        
        itens.add(new CarrinhoItem(
            brinquedo.getId(),
            brinquedo.getNome(),
            brinquedo.getValor(),
            brinquedo.getImagemUrl(),
            quantidade
        ));
    }

    public void remover(Long id) {
        itens.removeIf(item -> item.getId().equals(id));
    }

    public void atualizarQuantidade(Long id, Integer quantidade) {
        if (quantidade <= 0) {
            remover(id);
            return;
        }
        
        for (CarrinhoItem item : itens) {
            if (item.getId().equals(id)) {
                item.setQuantidade(quantidade);
                return;
            }
        }
    }

    public List<CarrinhoItem> getItens() {
        return itens;
    }

    public Double getTotal() {
        return itens.stream()
                .mapToDouble(CarrinhoItem::getSubtotal)
                .sum();
    }

    public Integer getQuantidadeTotal() {
        return itens.stream()
                .mapToInt(CarrinhoItem::getQuantidade)
                .sum();
    }

    public void limpar() {
        itens.clear();
    }
}
