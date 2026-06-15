package com.ufu.finance.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.ufu.finance.entity.Transacao;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class TransacaoResponseDTO {

    private Long idTransacao;
    private Long idUsuario;
    private Integer idCategoria;
    private String nomeCategoria;
    private BigDecimal valor;
    private LocalDateTime dataHoraTransacao;
    private String descricao;
    private String tipo;         // "R" ou "D"
    private String tipoDescricao; // "Receita" ou "Despesa"

    /**
     * Alerta de orçamento (RF10). Populado APENAS em POST /api/transaction —
     * nas listagens o campo é null e omitido do JSON via @JsonInclude.
     * true → essa transação fez o gasto na categoria/mês atingir >= 80% do limite.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean alertaOrcamento;

    public TransacaoResponseDTO(Transacao t) {
        this.idTransacao   = t.getId();
        this.idUsuario     = t.getUsuario().getId();
        this.idCategoria   = t.getCategoria().getId();
        this.nomeCategoria = t.getCategoria().getNome();
        this.valor         = t.getValor();
        this.dataHoraTransacao = t.getDataHoraTransacao();
        this.descricao     = t.getDescricao();
        this.tipo          = t.getTipo().name();
        this.tipoDescricao = t.getTipo().getDescricao();
    }
}
