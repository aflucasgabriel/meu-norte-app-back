package com.ufu.finance.util;

import com.ufu.finance.enums.TipoTransacao;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TipoTransacaoConverterTest {

    private final TipoTransacaoConverter converter = new TipoTransacaoConverter();

    @Test
    void convertToDatabaseColumnReturnsNullForNullValue() {
        assertNull(converter.convertToDatabaseColumn(null));
    }

    @Test
    void convertToDatabaseColumnReturnsEnumName() {
        assertEquals("R", converter.convertToDatabaseColumn(TipoTransacao.R));
        assertEquals("D", converter.convertToDatabaseColumn(TipoTransacao.D));
    }

    @Test
    void convertToEntityAttributeReturnsNullForNullValue() {
        assertNull(converter.convertToEntityAttribute(null));
    }

    @Test
    void convertToEntityAttributeReturnsNullForBlankString() {
        assertNull(converter.convertToEntityAttribute(""));
        assertNull(converter.convertToEntityAttribute("   "));
    }

    @Test
    void convertToEntityAttributeParsesValueIgnoringWhitespaceAndCase() {
        assertEquals(TipoTransacao.R, converter.convertToEntityAttribute(" r "));
        assertEquals(TipoTransacao.D, converter.convertToEntityAttribute("d"));
    }
}
