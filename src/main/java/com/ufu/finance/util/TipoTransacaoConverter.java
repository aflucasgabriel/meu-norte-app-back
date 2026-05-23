package com.ufu.finance.util;

import com.ufu.finance.enums.TipoTransacao;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converte TipoTransacao (enum Java) para VARCHAR(1) no banco.
 * 
 * R → Receita
 * D → Despesa
 */
@Converter(autoApply = false)
public class TipoTransacaoConverter implements AttributeConverter<TipoTransacao, String> {

    @Override
    public String convertToDatabaseColumn(TipoTransacao tipo) {
        if (tipo == null) {
            return null;
        }
        return tipo.name(); // "R" ou "D"
    }

    @Override
    public TipoTransacao convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return null;
        }
        return TipoTransacao.valueOf(dbData.trim().toUpperCase());
    }
}
