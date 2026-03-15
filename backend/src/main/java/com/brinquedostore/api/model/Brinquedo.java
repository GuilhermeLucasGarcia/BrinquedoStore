package com.brinquedostore.api.model;

import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "\"BRINQUEDO\"")
public class Brinquedo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "\"CODIGO\"")
    private String codigo;

    @Column(name = "\"DESCRICAO\"")
    private String nome;

    @Column(name = "\"DETALHES\"")
    private String descricao;

    @Column(name = "\"VALOR\"")
    private Double valor;

    @Column(name = "\"IMG_URL\"")
    private String imagemUrl;

    @Column(name = "\"DESTAQUE\"")
    private Boolean emDestaque;

    @ManyToOne
    @JoinColumn(name = "\"CATEGORIA_ID\"")
    private Categoria categoria;

    @ManyToOne
    @JoinColumn(name = "\"MARCA_ID\"")
    private Marca marca;

    @UpdateTimestamp
    @Column(name = "\"DT_ALTERACAO\"")
    private LocalDateTime dtAlteracao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
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

    public Boolean getEmDestaque() {
        return emDestaque;
    }

    public void setEmDestaque(Boolean emDestaque) {
        this.emDestaque = emDestaque;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Marca getMarca() {
        return marca;
    }

    public void setMarca(Marca marca) {
        this.marca = marca;
    }

    public LocalDateTime getDtAlteracao() {
        return dtAlteracao;
    }

    public void setDtAlteracao(LocalDateTime dtAlteracao) {
        this.dtAlteracao = dtAlteracao;
    }
}
