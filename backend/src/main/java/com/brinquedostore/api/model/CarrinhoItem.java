package com.brinquedostore.api.model;

public class CarrinhoItem {
    private Long id;
    private String nome;
    private Double valor;
    private String imagemUrl;
    private Integer quantidade;

    public CarrinhoItem() {}

    public CarrinhoItem(Long id, String nome, Double valor, String imagemUrl, Integer quantidade) {
        this.id = id;
        this.nome = nome;
        this.valor = valor;
        this.imagemUrl = imagemUrl;
        this.quantidade = quantidade;
    }
    
    public Double getSubtotal() {
        return valor * quantidade;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public String getImagemUrl() {
        return imagemUrl;
    }

    public void setImagemUrl(String imagemUrl) {
        this.imagemUrl = imagemUrl;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }
}
