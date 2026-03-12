package com.brinquedostore.api.model;

import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "\"INTEGRANTE\"")
public class Integrante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "\"NOME\"")
    private String nome;

    @Column(name = "\"IMG_URL\"")
    private String imgUrl;

    @UpdateTimestamp
    @Column(name = "\"DE_ALTERACAO\"")
    private LocalDateTime dtAlteracao;
}
