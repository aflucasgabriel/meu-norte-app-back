package com.ufu.finance.dto;

import java.math.BigDecimal;

/** Um slice do gráfico de pizza: categoria + valor total */
public class GraficoCategoriaDTO {

    private String categoria;
    private BigDecimal total;

    public GraficoCategoriaDTO() {}

    public GraficoCategoriaDTO(String categoria, BigDecimal total) {
        this.categoria = categoria;
        this.total = total;
    }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
}
