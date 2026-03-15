package com.brinquedostore.api.model;

import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "\"CATEGORIA\"")
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "\"NOME\"")
    private String nome;

    @Column(name = "\"IMG_URL\"")
    private String imgUrl;

    @UpdateTimestamp
    @Column(name = "\"DT_ALTERACAO\"")
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

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public LocalDateTime getDtAlteracao() {
        return dtAlteracao;
    }

    public void setDtAlteracao(LocalDateTime dtAlteracao) {
        this.dtAlteracao = dtAlteracao;
    }
}
