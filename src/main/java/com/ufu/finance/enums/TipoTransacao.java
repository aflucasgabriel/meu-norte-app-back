package com.ufu.finance.enums;

/**
 * Tipo da transação financeira.
 * Salvo no banco como VARCHAR(1): 'R' = Receita, 'D' = Despesa.
 */
public enum TipoTransacao {
    R("Receita"),
    D("Despesa");

    private final String descricao;

    TipoTransacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
