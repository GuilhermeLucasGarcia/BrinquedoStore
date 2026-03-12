package com.brinquedostore.api.model;

import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "\"MARCA\"")
public class Marca {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "\"NOME\"")
    private String nome;

    @UpdateTimestamp
    @Column(name = "\"DT_ALTERACAO\"")
    private LocalDateTime dtAlteracao;
}
