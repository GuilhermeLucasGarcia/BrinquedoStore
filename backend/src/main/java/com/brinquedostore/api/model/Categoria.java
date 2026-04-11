package com.brinquedostore.api.model;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "\"CATEGORIA\"")
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Informe o nome do catálogo.")
    @Size(max = 120, message = "O nome do catálogo deve ter no máximo 120 caracteres.")
    @Column(name = "\"NOME\"", nullable = false, length = 120)
    private String nome;

    @NotBlank(message = "Informe a descrição do catálogo.")
    @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres.")
    @Column(name = "\"DESCRICAO\"", nullable = false, length = 500)
    private String descricao;

    @Size(max = 500, message = "A URL da imagem deve ter no máximo 500 caracteres.")
    @Column(name = "\"IMG_URL\"", length = 500)
    private String imgUrl;

    @Column(name = "\"ATIVO\"", nullable = false)
    private Boolean ativo = Boolean.TRUE;

    @CreationTimestamp
    @Column(name = "\"DT_CRIACAO\"", nullable = false, updatable = false)
    private LocalDateTime dtCriacao;

    @UpdateTimestamp
    @Column(name = "\"DT_ALTERACAO\"", nullable = false)
    private LocalDateTime dtAlteracao;

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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public LocalDateTime getDtCriacao() {
        return dtCriacao;
    }

    public void setDtCriacao(LocalDateTime dtCriacao) {
        this.dtCriacao = dtCriacao;
    }

    public LocalDateTime getDtAlteracao() {
        return dtAlteracao;
    }

    public void setDtAlteracao(LocalDateTime dtAlteracao) {
        this.dtAlteracao = dtAlteracao;
    }
}
