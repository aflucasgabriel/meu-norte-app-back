package com.ufu.finance.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Resumo financeiro de um mês: totais de receita, despesa e saldo.
 * Inclui breakdown por categoria para montar gráficos.
 */
public class ResumoMensalDTO {

    private int mes;
    private int ano;
    private BigDecimal totalReceitas;
    private BigDecimal totalDespesas;
    private BigDecimal saldo; // receitas - despesas

    private List<GraficoCategoriaDTO> despesasPorCategoria;
    private List<GraficoCategoriaDTO> receitasPorCategoria;

    public ResumoMensalDTO() {}

    public ResumoMensalDTO(int mes, int ano,
                           BigDecimal totalReceitas,
                           BigDecimal totalDespesas,
                           List<GraficoCategoriaDTO> despesasPorCategoria,
                           List<GraficoCategoriaDTO> receitasPorCategoria) {
        this.mes = mes;
        this.ano = ano;
        this.totalReceitas = totalReceitas;
        this.totalDespesas = totalDespesas;
        this.saldo = totalReceitas.subtract(totalDespesas);
        this.despesasPorCategoria = despesasPorCategoria;
        this.receitasPorCategoria = receitasPorCategoria;
    }

    public int getMes() { return mes; }
    public void setMes(int mes) { this.mes = mes; }

    public int getAno() { return ano; }
    public void setAno(int ano) { this.ano = ano; }

    public BigDecimal getTotalReceitas() { return totalReceitas; }
    public void setTotalReceitas(BigDecimal totalReceitas) { this.totalReceitas = totalReceitas; }

    public BigDecimal getTotalDespesas() { return totalDespesas; }
    public void setTotalDespesas(BigDecimal totalDespesas) { this.totalDespesas = totalDespesas; }

    public BigDecimal getSaldo() { return saldo; }
    public void setSaldo(BigDecimal saldo) { this.saldo = saldo; }

    public List<GraficoCategoriaDTO> getDespesasPorCategoria() { return despesasPorCategoria; }
    public void setDespesasPorCategoria(List<GraficoCategoriaDTO> v) { this.despesasPorCategoria = v; }

    public List<GraficoCategoriaDTO> getReceitasPorCategoria() { return receitasPorCategoria; }
    public void setReceitasPorCategoria(List<GraficoCategoriaDTO> v) { this.receitasPorCategoria = v; }
}
