package com.brinquedostore.api.model;

import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
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
}
