package com.ufu.finance.dto;

import java.math.BigDecimal;
import java.util.List;

/** Evolução mês a mês de receitas e despesas em um ano — para gráfico de linha */
public class EvolucaoMensalDTO {

    private int ano;
    private List<PontoMensalDTO> receitas;
    private List<PontoMensalDTO> despesas;

    public EvolucaoMensalDTO() {}

    public EvolucaoMensalDTO(int ano, List<PontoMensalDTO> receitas, List<PontoMensalDTO> despesas) {
        this.ano = ano;
        this.receitas = receitas;
        this.despesas = despesas;
    }

    public int getAno() { return ano; }
    public void setAno(int ano) { this.ano = ano; }

    public List<PontoMensalDTO> getReceitas() { return receitas; }
    public void setReceitas(List<PontoMensalDTO> receitas) { this.receitas = receitas; }

    public List<PontoMensalDTO> getDespesas() { return despesas; }
    public void setDespesas(List<PontoMensalDTO> despesas) { this.despesas = despesas; }

    // ── Inner DTO ─────────────────────────────────────────────────────────────

    public static class PontoMensalDTO {
        private int mes;
        private BigDecimal total;

        public PontoMensalDTO() {}

        public PontoMensalDTO(int mes, BigDecimal total) {
            this.mes = mes;
            this.total = total;
        }

        public int getMes() { return mes; }
        public void setMes(int mes) { this.mes = mes; }

        public BigDecimal getTotal() { return total; }
        public void setTotal(BigDecimal total) { this.total = total; }
    }
}
