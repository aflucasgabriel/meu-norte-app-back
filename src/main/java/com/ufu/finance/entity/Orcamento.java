package com.ufu.finance.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Limite mensal de gasto por categoria (RF06).
 * Único por combinação (usuario, categoria, mês, ano) — ver UniqueConstraint.
 */
@Data
@NoArgsConstructor
@Entity
@Table(
        name = "orcamentos",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_orcamento_usuario_categoria_mes_ano",
                columnNames = {"id_usuario", "id_categoria", "mes", "ano"}
        )
)
public class Orcamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Usuário dono do orçamento. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    /** Categoria à qual o limite se aplica. EAGER porque o nome aparece na resposta. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

    @NotNull
    @Column(nullable = false)
    private Integer mes;

    @NotNull
    @Column(nullable = false)
    private Integer ano;

    @NotNull
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal limite;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_atualizacao", nullable = false)
    private LocalDateTime dataAtualizacao;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.dataCriacao = now;
        this.dataAtualizacao = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.dataAtualizacao = LocalDateTime.now();
    }
}
